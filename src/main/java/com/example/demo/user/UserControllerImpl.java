package com.example.demo.user;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;
import com.example.demo.user.dto.UserDTO;
import com.example.demo.user.util.UserMapper;
import com.example.demo.util.PageDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserControllerImpl implements UserController{

    private final UserService userService;

    @Override
    public ResponseEntity<PageDTO<UserDTO>> getUsers(int pageSize, int page) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> users = userService.getAllUsers(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(new PageImpl<>(UserMapper.INSTANCE.toDtoList(users.getContent()), pageable, users.getTotalElements())));
    }

    @Override
    public ResponseEntity<UserDTO> getUserById(Long userId) throws UserResourceException {
        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.INSTANCE.toDto(userService.getUserById(userId)));
    }

    @Override
    public ResponseEntity<UserDTO> createUser(CreateUserForm createUserForm) throws UserResourceException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users").toUriString());
        return ResponseEntity.created(uri).body(UserMapper.INSTANCE.toDto(userService.createUser(createUserForm)));
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(UpdateUserForm updateUserForm) throws UserResourceException {
        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.INSTANCE.toDto(userService.updateUser(updateUserForm)));
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long userId) throws UserResourceException {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
