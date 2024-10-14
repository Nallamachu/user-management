package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.AuthenticationRequest;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.RegisterRequest;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = "Access-Control-Allow-Origin")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public Response<String> register(
            @RequestBody RegisterRequest request
    ) {
        if (request.getMobile().trim().isEmpty()
                || request.getUsername().trim().isEmpty()
                || request.getPassword().trim().isEmpty()) {
            new ResponseEntity<>("Registration failed due to invalid input details", HttpStatus.BAD_REQUEST);
        }
        return service.register(request, false, new Response<>());
    }

    @PostMapping("/authenticate")
    public Response<String> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        Response<String> response = new Response<>();
        if (request.getUsername() == null || request.getPassword() == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return service.authenticate(request, response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
