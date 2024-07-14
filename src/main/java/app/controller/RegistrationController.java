package app.controller;


import app.annotation.Auditable;
import app.annotation.Loggable;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.dto.request.UserRequest;
import app.mapper.UserMapper;
import app.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Auditable
@Loggable
@RestController
public class RegistrationController {

    private final UserMapper userMapper;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(UserMapper userMapper, RegistrationService registrationService) {
        this.userMapper = userMapper;
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registration(@RequestBody UserRequest userRequest) {
        UserDto userDto = userMapper.requestToDto(userRequest);
        OperationResult operationResult = registrationService.register(userDto);
        return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
    }
}
