package com.example.demo.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long userId) throws UserResourceException {
        return userRepository.findById(userId).orElseThrow(() -> new UserResourceException("UserNotFound",
                "The user ID is not found in the database.", HttpStatus.NOT_FOUND));
    }

    @Override
    public User createUser(CreateUserForm createUserForm) throws UserResourceException {
        try {
            return userRepository.save(new User(null, createUserForm.getNom(), createUserForm.getEmail()));
        } catch (DataIntegrityViolationException e) {
            throw new UserResourceException("UserAlreadyExists",
                    "The user " + createUserForm.getNom() + " already exists.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new UserResourceException("CreateUserError",
                    "Error while creating the user " + createUserForm.getNom() + ".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User updateUser(UpdateUserForm updateUserForm) throws UserResourceException {
        User userDatabase = getUserById(updateUserForm.getId());
        String newEmail = updateUserForm.getEmail();

        if (!newEmail.isEmpty()) {
            userDatabase.setEmail(newEmail);
        }

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
