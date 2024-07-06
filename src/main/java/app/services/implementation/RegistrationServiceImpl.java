package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.services.RegistrationService;

public class RegistrationServiceImpl implements RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final LoginDao loginDao;

    public RegistrationServiceImpl(PasswordEncoder passwordEncoder,
                                   LoginDao loginDAO) {
        this.passwordEncoder = passwordEncoder;
        this.loginDao = loginDAO;
    }

    @Override
    public OperationResult register(UserDto userDto) {
        boolean isAlreadyExists = loginDao.checkIfUserExist(userDto.getLogin());
        if (isAlreadyExists) {
            return new OperationResult("Wrong login or password", 404);
        } else {
            int encodedPswd = passwordEncoder.encode(userDto.getPassword());
            loginDao.addNewUser(userDto.getLogin(), encodedPswd);
            return new OperationResult("Successfully register", 200);
        }
    }
}
