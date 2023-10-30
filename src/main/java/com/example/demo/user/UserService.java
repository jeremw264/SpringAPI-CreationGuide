package com.example.demo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;

@Service
public interface UserService {
    
    Page<User> getAllUsers(final Pageable pageable);

    User getUserById(final Long userId) throws UserResourceException;

    User createUser(final CreateUserForm createUserForm) throws UserResourceException;

    User updateUser(final UpdateUserForm updateUserForm) throws UserResourceException;

    void deleteUser(final Long userId) throws UserResourceException;

}
