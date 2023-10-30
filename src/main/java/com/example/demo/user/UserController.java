package com.example.demo.user;

import com.example.demo.exception.ResourceExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;
import com.example.demo.user.dto.UserDTO;
import com.example.demo.util.PageDTO;

@Tag(name = "Users Endpoint")
@RequestMapping("/users")
@RestController
public interface UserController {

    @Operation(summary = "Retrieve all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full content"),
    })
    @GetMapping
    ResponseEntity<PageDTO<UserDTO>> getUsers(@RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "0", required = false) int page);

    @Operation(summary = "Find user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user with the ID in parameter"),
            @ApiResponse(responseCode = "404", description = "The user ID is not found in the database.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long userId) throws UserResourceException;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created."),
            @ApiResponse(responseCode = "409", description = "The user in parameter already exists.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error while creating the user in parameter.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
    })
    @PostMapping
    ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserForm createUserForm) throws UserResourceException;

    @Operation(summary = "Update user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated."),
            @ApiResponse(responseCode = "409", description = "User with the same name already exists.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error while updating the user with the ID.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
    })
    @PutMapping
    ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserForm updateUserForm) throws UserResourceException;

    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted."),
            @ApiResponse(responseCode = "500", description = "Error while deleting the user with the ID.",
                    content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUserById(@PathVariable("id") Long userId) throws UserResourceException;
}
