package app.controller;

import app.dto.OperationResult;
import app.dto.UserDto;
import app.dto.request.UserRequest;
import app.mapper.UserMapper;
import app.mapper.UserMapperImpl;
import app.services.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {
    private MockMvc mockMvc;
    @Mock
    private RegistrationService registrationService;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        RegistrationController registrationController = new RegistrationController(userMapper, registrationService);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    public void registrationSuccessfullyTest() throws Exception {
        String login = "user";
        String password = "user_password";
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword(password);
        userRequest.setLogin(login);
        UserDto userDto = new UserDto();
        userDto.setLogin(login);
        userDto.setPassword(password);
        String json = """
                   {
                   "login": "user",
                   "password": "user_password"
                   }
                """;

        OperationResult operationResult = new OperationResult("Successfully register", 200);
        when(registrationService.register(userDto)).thenReturn(operationResult);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(userMapper, times(1)).requestToDto(userRequest);
        verify(registrationService, times(1)).register(userDto);
    }
    @Test
    public void registrationFailTest() throws Exception {
        String login = "admin";
        String password = "admin";
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword(password);
        userRequest.setLogin(login);
        UserDto userDto = new UserDto();
        userDto.setLogin(login);
        userDto.setPassword(password);
        String json = """
                   {
                   "login": "admin",
                   "password": "admin"
                   }
                """;

        OperationResult operationResult = new OperationResult("Login is being used by another user", 409);
        when(registrationService.register(userDto)).thenReturn(operationResult);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(operationResult.getMessage()));
        verify(userMapper, times(1)).requestToDto(userRequest);
        verify(registrationService, times(1)).register(userDto);
    }

}




