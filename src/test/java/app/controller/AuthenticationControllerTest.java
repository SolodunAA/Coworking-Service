package app.controller;

import app.dto.OperationResult;
import app.dto.UserDto;
import app.dto.request.UserRequest;
import app.mapper.UserMapper;
import app.mapper.UserMapperImpl;
import app.services.AuthenticationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {
    private MockMvc mockMvc;
    @Mock
    private AuthenticationService authenticationService;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        AuthenticationController authenticationController = new AuthenticationController(userMapper, authenticationService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    public void authenticationSuccessfullyTest() throws Exception {
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

        OperationResult operationResult = new OperationResult("Login Successful", 200);
        when(authenticationService.auth(userDto)).thenReturn(operationResult);

        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(userMapper, times(1)).requestToDto(userRequest);
        verify(authenticationService, times(1)).auth(userDto);
    }
    @Test
    public void authenticationFailTest() throws Exception {
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

        OperationResult operationResult = new OperationResult("Wrong login or password", 404);
        when(authenticationService.auth(userDto)).thenReturn(operationResult);

        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(operationResult.getMessage()));
        verify(userMapper, times(1)).requestToDto(userRequest);
        verify(authenticationService, times(1)).auth(userDto);
    }

}
