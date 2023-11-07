package com.example.demo.user;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "users-pages-cache", unless = "#result==null")
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Cacheable(value = "users-cache", key = "#userId", unless = "#result==null")
    @Override
    public User getUserById(Long userId) throws UserResourceException {
        return userRepository.findById(userId).orElseThrow(() -> new UserResourceException("UserNotFound",
                "The user ID is not found in the database.", HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "users-cache", unless = "#result==null")
    @CacheEvict(value = "users-pages-cache", allEntries = true)
    @Override
    public User createUser(CreateUserForm createUserForm) throws UserResourceException {
        try {
            return userRepository.save(new User(null, createUserForm.getUsername(), createUserForm.getEmail(),
                    createUserForm.getPassword()));
        } catch (DataIntegrityViolationException e) {
            throw new UserResourceException("UserAlreadyExists",
                    "The user " + createUserForm.getUsername() + " already exists.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new UserResourceException("CreateUserError",
                    "Error while creating the user " + createUserForm.getUsername() + ".",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CachePut(value = "users-cache", key = "#updateUserForm.id")
    @CacheEvict(value = "users-pages-cache", allEntries = true)
    @Override
    public User updateUser(UpdateUserForm updateUserForm) throws UserResourceException {
        User userDatabase = getUserById(updateUserForm.getId());
        String newEmail = updateUserForm.getEmail();
        String newPassword = updateUserForm.getPassword();

        if (newEmail != null && !newEmail.isEmpty())
            userDatabase.setEmail(newEmail);

        if (newPassword != null && !newPassword.isEmpty())
            userDatabase.setPassword(newPassword);

        try {
            return userRepository.save(userDatabase);
        } catch (DataIntegrityViolationException e) {
            throw new UserResourceException("UserAlreadyExist", "User with same name already exists.",
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new UserResourceException("UpdateUserError",
                    "Error while updating the user with the ID : " + updateUserForm.getId().toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "users-cache", key = "#userId"),
            @CacheEvict(value = "users-pages-cache", allEntries = true)
    })
    @Override
    public void deleteUser(Long userId) throws UserResourceException {
        try {
            userRepository.delete(getUserById(userId));
        } catch (Exception e) {
            throw new UserResourceException("DeleteUserError",
                    "Error while deleting the user with the ID : " + userId.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
