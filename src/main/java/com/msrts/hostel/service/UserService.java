package com.msrts.hostel.service;

import com.msrts.hostel.entity.User;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.ChangePasswordRequest;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;

    public Response<String> changePassword(ChangePasswordRequest request, Principal connectedUser, Response<String> response) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            response.setErrors(List.of(new Error("ERROR_WRONG_PASSWORD", ErrorConstants.ERROR_WRONG_PASSWORD)));
            return response;
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            response.setErrors(List.of(new Error("ERROR_PASSWORDS_NOT_MATCHED", ErrorConstants.ERROR_PASSWORDS_NOT_MATCHED)));
            return response;
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        response.setData("Password Changes Successfully");
        return response;
    }

    public Response<User> getUserByIdOrEmail(Integer id, String email, Response<User> response) {
        if (id > 0) {
            Optional<User> optionalUser = userRepository.findById(id);
            optionalUser.ifPresent(response::setData);
        } else if (!email.trim().isEmpty()) {
            response.setData(userRepository.findByEmail(email));
        } else {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
        }
        return response;
    }

    public Response<User> getUserByMobileNo(String mobile, Response<User> response) {
        User user = userRepository.findByMobile(mobile);
        if (user == null)
            response.setErrors(List.of(new Error("ERROR_USER_NOT_FOUND", ErrorConstants.ERROR_USER_NOT_FOUND)));
        else response.setData(user);
        return response;
    }

    public Response<List<User>> findAllUsersByGroupId(Long groupId, Response<List<User>> response) {
        List<User> userList = userRepository.findAllUsersByPartnerGroupId(groupId);
        if (userList.isEmpty()) {
            response.setErrors(List.of(new Error("ERROR_USER_NOT_FOUND", ErrorConstants.ERROR_USER_NOT_FOUND)));
            return response;
        }
        response.setData(userList);
        return response;
    }

    public Response<List<User>> getAllUsers(Response<List<User>> response) {
        List<User> userList = userRepository.findAll();
        response.setData(userList);
        return response;
    }

    public Response<User> updateUser(User user, Response<User> response) {
        User existing = userRepository.findByUsername(user.getUsername());
        existing.setFirstname(user.getFirstname());
        existing.setMiddlename(user.getMiddlename());
        existing.setLastname(user.getLastname());
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        if (Objects.equals(existing.getPassword(), user.getPassword())) {
            existing.setPassword(user.getPassword());
        } else {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user = userRepository.save(existing);
        response.setData(user);
        return response;
    }

}
