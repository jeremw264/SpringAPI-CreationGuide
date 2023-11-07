package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserForm {
    
    @NotBlank(message = "The username is required to create a new user")
    private String username;

    @NotBlank(message = "The email is required to create a new user")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",message = "The email is not valid")
    private String email;

    @NotBlank(message = "The password is required to create a new user")
    private String password;
}
