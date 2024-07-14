package app.controller;

import app.annotation.Auditable;
import app.annotation.Loggable;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.dto.request.UserRequest;
import app.mapper.UserMapper;
import app.services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Auditable
@Loggable
@RestController
public class AuthenticationController {

    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;
    @Autowired
    public AuthenticationController(UserMapper userMapper, AuthenticationService authenticationService) {
        this.userMapper = userMapper;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/authentication", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> authentication(HttpSession session, @RequestBody UserRequest userRequest) {
        UserDto userDto = userMapper.requestToDto(userRequest);
        session.setAttribute("login", userDto.getLogin());
        OperationResult operationResult = authenticationService.auth(userDto);
        return ResponseEntity
                .status(operationResult.getErrCode())
                .body(operationResult.getMessage());
    }
}
