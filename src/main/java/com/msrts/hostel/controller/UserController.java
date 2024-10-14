package com.msrts.hostel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.User;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.ChangePasswordRequest;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = "Access-Control-Allow-Origin")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private final UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @PatchMapping("/change-password")
    public Response<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        return service.changePassword(request, connectedUser, new Response<>());
    }

    @GetMapping(value = "/user-by-id-or-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<User> userByIdOrEmail(@RequestParam(required = false) Integer userId,
                                          @RequestParam(required = false) String email
    ) {
        LOGGER.info("Start finding the user by userId or email");
        Response<User> response = new Response<>();
        if (userId <= 0 && email.trim().isEmpty()) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        LOGGER.info("Start calling service for finding the user by userId or email");
        return service.getUserByIdOrEmail(userId, email, response);
    }

    @GetMapping(value = "/user-by-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<User> userByMobile(@RequestParam(required = false) String mobile) {
        LOGGER.info("Start finding the user by given mobile");
        Response<User> response = new Response<>();
        if (mobile != null && mobile.trim().length() < 9) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        LOGGER.info("Start calling service for finding the user by mobile");
        return service.getUserByMobileNo(mobile, response);
    }

    @GetMapping(value = "/all-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<User>> allUsers() {
        Response<List<User>> response = new Response<>();
        return service.getAllUsers(response);
    }

    @GetMapping(path = "/users-by-group-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<User>> usersByGroupId(@RequestParam Long groupId) {
        LOGGER.info("Start finding the user by group id");
        Response<List<User>> response = new Response<>();
        if (groupId == null || groupId <= 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        LOGGER.info("Start of calling service for finding the user by groupId");
        return service.findAllUsersByGroupId(groupId, response);
    }

    @PutMapping("/update")
    public Response<User> updateUserDetails(@RequestPart(value = "user") String user,
                                            @RequestPart(value = "file", required = false) MultipartFile file) {
        LOGGER.info("Start of updating the user details");
        Response<User> response = new Response<>();
        if (user == null) {
            LOGGER.error("Invalid user request data hence can't proceed to update user details");
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        try {
            User userData = objectMapper.reader().forType(User.class).readValue(user, User.class);
            LOGGER.info("Converted given string user details into User.class");
            response = service.updateUser(userData,  response);
        } catch (Exception ex) {
            LOGGER.error("Exception occurred while trying to update the user details: {}", ex.getMessage().substring(100));
            response.setErrors(List.of(new Error("", ErrorConstants.RUNTIME_EXCEPTION + ex.getMessage())));
        }
        return response;
    }

}
