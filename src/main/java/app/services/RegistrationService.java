package app.services;

import app.dto.OperationResult;
import app.dto.UserDto;

/**
 * RegistrationService
 */
public interface RegistrationService {
    /**
     * main registration method
     */
    OperationResult register(UserDto userDto);
}
