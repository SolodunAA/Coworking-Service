package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.services.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final LoginDao loginDao;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationServiceImpl(LoginDao loginDao,
                                     PasswordEncoder passwordEncoder) {
        this.loginDao = loginDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OperationResult auth(UserDto userDto) {
        try {
            boolean isUserExists = loginDao.checkIfUserExist(userDto.getLogin());
            if (isUserExists) {
                int encodedPswd = passwordEncoder.encode(userDto.getPassword());
                int savedEncodedPswd = loginDao.getEncodedPassword(userDto.getLogin());
                if (encodedPswd == savedEncodedPswd) {
                    return new OperationResult("Login Successful", 200);
                } else {
                    return new OperationResult("Wrong login or password", 404);
                }

            } else {
                return new OperationResult("Wrong login or password", 404);
            }
        } catch (Exception e) {
            return new OperationResult("Something went wrong, try again", 500);
        }
    }
}