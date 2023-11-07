package com.example.demo.user;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        User user1 = new User(1L, "username1", "email1", "password1");
        User user2 = new User(2L, "username2", "email1", "password2");

        List<User> users = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> agenciesFromService = userService.getAllUsers(pageable);

        assertNotNull(agenciesFromService);
        assertEquals(userPage, agenciesFromService);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getUserByIdWithExistingUser() throws UserResourceException {
        User user = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User userFromService = userService.getUserById(user.getId());

        assertNotNull(userFromService);
        assertEquals(user, userFromService);
        verify(userRepository, times(1)).findById(user.getId());

    }

    @Test
    void getUserNotFoundThrowException() throws UserResourceException {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(UserResourceException.class, () -> userService.getUserById(1L));
    }

    @Test
    void createUser() throws UserResourceException {
        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        CreateUserForm createUserForm = CreateUserForm.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        User userToSave = User.builder()
                .username(createUserForm.getUsername())
                .email(createUserForm.getEmail())
                .password(createUserForm.getPassword())
                .build();

        when(userRepository.save(userToSave)).thenReturn(user);

        User userCreated = userService.createUser(createUserForm);

        assertNotNull(userCreated);
        assertEquals(user, userCreated);

        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void creatingErrorIfAlreadyExistsTest() {
        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        CreateUserForm createUserForm = CreateUserForm.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        User userToSave = User.builder()
                .username(createUserForm.getUsername())
                .email(createUserForm.getEmail())
                .password(createUserForm.getPassword())
                .build();

        when(userRepository.save(userToSave)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserResourceException.class, () -> userService.createUser(createUserForm));

        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void testErrorWhileCreatingTheUser() {

        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        CreateUserForm createUserForm = CreateUserForm.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        User userToSave = User.builder()
                .username(createUserForm.getUsername())
                .email(createUserForm.getEmail())
                .password(createUserForm.getPassword())
                .build();

        when(userRepository.save(userToSave)).thenThrow(IllegalArgumentException.class);

        assertThrows(UserResourceException.class, () -> userService.createUser(createUserForm));

        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void updateUser() throws UserResourceException {

        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        UpdateUserForm updateUserForm = UpdateUserForm.builder()
                .id(user.getId())
                .email("new.email@domain.fr")
                .password("new_password")
                .build();

        User userUpdatedExpected = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(updateUserForm.getEmail())
                .password(updateUserForm.getPassword())
                .build();

        when(userRepository.findById(updateUserForm.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(userUpdatedExpected)).thenReturn(userUpdatedExpected);

        User agencyUpdated = userService.updateUser(updateUserForm);

        assertNotNull(agencyUpdated);
        assertEquals(userUpdatedExpected, agencyUpdated);

        verify(userRepository, times(1)).save(userUpdatedExpected);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testUpdateNonexistentAgency() {

        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        UpdateUserForm updateUserForm = UpdateUserForm.builder()
                .email("new.email@domain.fr")
                .password("new_password")
                .build();

        User userUpdatedExpected = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(updateUserForm.getEmail())
                .password(updateUserForm.getPassword())
                .build();


        when(userRepository.findById(updateUserForm.getId())).thenReturn(Optional.empty());

        assertThrows(UserResourceException.class, () -> userService.updateUser(updateUserForm));

        verify(userRepository, times(1)).findById(updateUserForm.getId());
    }

    @Test
    void testErrorIfDescriptionUpdatedAlreadyExists() {

        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        UpdateUserForm updateUserForm = UpdateUserForm.builder()
                .email("new.email@domain.fr")
                .password("new_password")
                .build();

        User userUpdatedExpected = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(updateUserForm.getEmail())
                .password(updateUserForm.getPassword())
                .build();

        when(userRepository.findById(updateUserForm.getId())).thenReturn(Optional.empty());
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(userUpdatedExpected);

        assertThrows(UserResourceException.class, () -> userService.updateUser(updateUserForm));

        verify(userRepository, times(1)).findById(updateUserForm.getId());
    }

    @Test
    void testErrorWhileUpdatingTheAgency() {
        User user = User.builder()
                .id(1L)
                .username("username1")
                .email("firstname.lastname@domain.fr")
                .password("password1")
                .build();

        UpdateUserForm updateUserForm = UpdateUserForm.builder()
                .email("new.email@domain.fr")
                .password("new_password")
                .build();

        User userUpdatedExpected = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(updateUserForm.getEmail())
                .password(updateUserForm.getPassword())
                .build();

        when(userRepository.findById(updateUserForm.getId())).thenReturn(Optional.empty());
        doThrow(IllegalArgumentException.class).when(userRepository).save(userUpdatedExpected);

        assertThrows(UserResourceException.class, () -> userService.updateUser(updateUserForm));

        verify(userRepository, times(1)).findById(updateUserForm.getId());
    }


    @Test
    void deleteUser() throws UserResourceException {
        User userToDelete = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).delete(userToDelete);

        userService.deleteUser(userToDelete.getId());

        verify(userRepository, times(1)).delete(userToDelete);
        verify(userRepository, times(1)).findById(userToDelete.getId());
    }

    @Test
    void testDeleteNonexistentUser() {
        User userToDelete = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.empty());

        assertThrows(UserResourceException.class, () -> userService.deleteUser(userToDelete.getId()));

        verify(userRepository, times(1)).findById(userToDelete.getId());
    }

    @Test
    void testErrorWhileDeletingTheUser() {
        User userToDelete = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException()).when(userRepository).delete(userToDelete);

        assertThrows(UserResourceException.class, () -> userService.deleteUser(userToDelete.getId()));

        verify(userRepository, times(1)).findById(userToDelete.getId());
    }
}