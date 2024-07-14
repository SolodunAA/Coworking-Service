package app.services;

import app.dto.OperationResult;
import app.dto.UserDto;

/**
 * AuthenticationService
 */
public interface AuthenticationService {
    /**
     * main authentication method
     * @return status
     */
    OperationResult auth(UserDto userDto);
}
