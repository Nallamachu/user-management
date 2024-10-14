package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.constant.Role;
import com.msrts.hostel.entity.Token;
import com.msrts.hostel.entity.TokenType;
import com.msrts.hostel.entity.User;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.*;
import com.msrts.hostel.repository.ReferralSequenceRepository;
import com.msrts.hostel.repository.TokenRepository;
import com.msrts.hostel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Autowired
    private final ReferralSequenceRepository hostelSequenceRepository;

    public Response<String> register(RegisterRequest request, boolean isAppDefaultUser, Response<String> response) {
        try {
            LOGGER.info("Start of user registration {}, {}, {}, {}",
                    request.getFirstname(), request.getLastname(), request.getUsername(), request.getMobile());
            if (request.getUsername() != null) {
                User user = userRepository.findByUsernameOrMobile(request.getUsername(), request.getMobile());
                if (user != null) {
                    LOGGER.error("User already exists with  {} ", request.getUsername());
                    response.setErrors(List.of(new Error("ERROR_USER_EXISTS", ErrorConstants.ERROR_USER_EXISTS)));
                    return response;
                }
            }

            Long currentSequence = hostelSequenceRepository.getSequenceByFirstAndLastName(
                    (request.getLastname().trim().length() > 2)
                            ? request.getFirstname().trim().substring(0, 2).toUpperCase() :
                            request.getFirstname().trim().toUpperCase(),
                    (request.getLastname().trim().length() > 2)
                            ? request.getLastname().trim().substring(0, 2).toUpperCase() :
                            request.getLastname().trim().toUpperCase()
            );
            LOGGER.info("User referral code generated {} ", currentSequence);

            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .mobile(request.getMobile())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role((request.getRole() != null) ? request.getRole() : Role.USER)
                    .referralCode(generateSequence(request, currentSequence))
                    .referredByCode(request.getReferredByCode())
                    .points(100)
                    .build();
            LOGGER.info("User ready to save");
            var savedUser = userRepository.save(user);
            if (user.getReferredByCode() != null) {
                updateReferredUserCoins(user.getReferredByCode());
            }
//            var jwtToken = jwtService.generateToken(user);
//            var refreshToken = jwtService.generateRefreshToken(user);
//            saveUserToken(savedUser, jwtToken);
//            authenticationResponse = AuthenticationResponse.builder()
//                    .accessToken(jwtToken)
//                    .refreshToken(refreshToken)
//                    .build();
            response.setData("User created successfully");
            LOGGER.info("User created successfully");
        } catch (Exception e) {
            LOGGER.error("Error occurred while registering the user with {} , error message {}", request.getUsername(), e.getMessage());
        }
        return response;
    }

    private void updateReferredUserCoins(String referredByCode) {
        User referredUser = userRepository.findByReferralCode(referredByCode);
        if (referredUser != null) {
            referredUser.setPoints(referredUser.getPoints() + 50);
            userRepository.save(referredUser);
        }
    }

    private String generateSequence(RegisterRequest user, Long currentSequence) {
        var sequence = "";
        try {
            var firstName = (user.getLastname().trim().length() > 2)
                    ? user.getFirstname().trim().substring(0, 2).toUpperCase() :
                    user.getFirstname().trim().toUpperCase();
            var lastName = (user.getLastname().trim().length() > 2)
                    ? user.getLastname().trim().substring(0, 2).toUpperCase() :
                    user.getLastname().trim().toUpperCase();
            if (currentSequence != null && currentSequence > 0) {
                sequence = firstName + lastName + (currentSequence + 1);
            } else {
                sequence = firstName + lastName + 1;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while generating the sequence {}, {}, {}", user.getFirstname(), user.getLastname(), currentSequence);
        }
        return sequence;
    }

    public Response<String> authenticate(AuthenticationRequest request, Response<String> response) {
        var user = userRepository.findByUsername(request.getUsername());
        if (user != null) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
            } catch (Exception ex) {
                response.setErrors(List.of(new Error("ERROR_INVALID_CREDENTIALS", ErrorConstants.ERROR_INVALID_CREDENTIALS)));
            }

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
            response.setData(String.valueOf(authenticationResponse));
        } else {
            response.setErrors(List.of(new Error("ERROR_USER_NOT_FOUND", ErrorConstants.ERROR_USER_NOT_FOUND)));
        }
        return response;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(new RefreshToken(user.getId(), user.getEmail(), refreshToken, 2,
                                new Date(System.currentTimeMillis() + refreshExpiration)))
                        .build();
                objectMapper.writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
