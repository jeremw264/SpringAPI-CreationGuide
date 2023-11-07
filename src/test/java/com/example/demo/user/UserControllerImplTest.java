package com.example.demo.user;

import com.example.demo.user.dto.CreateUserForm;
import com.example.demo.user.dto.UpdateUserForm;
import com.example.demo.user.dto.UserDTO;
import com.example.demo.user.util.UserMapper;
import com.example.demo.util.PageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerImpl.class)
class UserControllerImplTest {

    private final static String BASE_PATH = "/users";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserControllerImpl userController;

    @MockBean
    private UserService userService;

    @Test
    void getUsers() throws Exception {

        User user1 = new User(1L, "username1", "email1", "password1");
        User user2 = new User(2L, "username2", "email1", "password2");

        List<User> users = List.of(user1, user2);
        List<UserDTO> userDTOS = UserMapper.INSTANCE.toDtoList(users);
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> usersFromService = new PageImpl<>(users, pageable, users.size());

        when(userService.getAllUsers(pageable)).thenReturn(usersFromService);

        MvcResult res = mvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PageDTO<UserDTO> pageDTO = objectMapper.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<PageDTO<UserDTO>>() {
        });

        assertNotNull(pageDTO);
        assertEquals(users.size(), usersFromService.getNumberOfElements());
        assertEquals(userDTOS, pageDTO.getContent());
        assertEquals(pageable.getPageNumber(), pageDTO.getCurrentPage());
        verify(userService, times(1)).getAllUsers(pageable);
    }

    @Test
    void getUserById() throws Exception {

        User user1 = new User(1L, "username1", "email1", "password1");

        when(userService.getUserById(user1.getId())).thenReturn(user1);

        MvcResult res = mvc.perform(get(BASE_PATH + "/" + user1.getId().toString()).characterEncoding("UTF-8")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        UserDTO userDTO = objectMapper.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<UserDTO>() {
        });

        assertNotNull(userDTO);
        verify(userService, times(1)).getUserById(user1.getId());
        assertEquals(user1.getId(), userDTO.getId());
        assertEquals(user1.getEmail(), userDTO.getEmail());
        assertEquals(user1.getUsername(), userDTO.getUsername());
    }

    @Test
    void createUser() throws Exception {

        User user = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");
        UserDTO userDTOExpected = UserMapper.INSTANCE.toDto(user);

        CreateUserForm createUserForm = CreateUserForm.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        when(userService.createUser(createUserForm)).thenReturn(user);

        MvcResult res = mvc
                .perform(post(BASE_PATH)
                        .content(new ObjectMapper().writeValueAsString(createUserForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDTO userCreated = objectMapper.readValue(res.getResponse().getContentAsString(), UserDTO.class);

        assertNotNull(userCreated);
        assertEquals(userDTOExpected, userCreated);

        verify(userService, times(1)).createUser(createUserForm);

    }

    @Test
    void updateUser() throws Exception {
        User user = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        UpdateUserForm updateDeadlineForm = UpdateUserForm.builder()
                .id(user.getId())
                .email("newemail@domain.fr")
                .password("new_password")
                .build();

        User userUpdated = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(updateDeadlineForm.getEmail())
                .password(updateDeadlineForm.getPassword())
                .build();

        UserDTO userDTOExpected = UserMapper.INSTANCE.toDto(userUpdated);

        when(userService.updateUser(updateDeadlineForm)).thenReturn(userUpdated);

        MvcResult res = mvc
                .perform(patch(BASE_PATH)
                        .content(objectMapper.writeValueAsString(updateDeadlineForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDTO deadlineDTOUpdated = objectMapper.readValue(res.getResponse().getContentAsString(), UserDTO.class);

        assertNotNull(deadlineDTOUpdated);
        assertEquals(userDTOExpected, deadlineDTOUpdated);

        verify(userService, times(1)).updateUser(updateDeadlineForm);

    }

    @Test
    void deleteUserById() throws Exception {
        User user = new User(1L, "username1", "firstname.lastname@domain.fr", "password1");

        doNothing().when(userService).deleteUser(user.getId());

        mvc.perform(delete(BASE_PATH + "/" + user.getId().toString())).andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(user.getId());
    }
}