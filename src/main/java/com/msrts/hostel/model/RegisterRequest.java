package com.msrts.hostel.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msrts.hostel.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String mobile;
    private String referralCode;
    private String referredByCode;
    private Role role;
}
