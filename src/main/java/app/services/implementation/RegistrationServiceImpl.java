package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dao.UserRoleDao;
import app.dto.Role;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.RegistrationService;

public class RegistrationServiceImpl implements RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final LoginDao loginDAO;
    private final Reader reader;
    private final UserRoleDao userRoleDao;

    public RegistrationServiceImpl(PasswordEncoder passwordEncoder,
                                   LoginDao loginDAO,
                                   Reader reader, UserRoleDao userRoleDao) {
        this.passwordEncoder = passwordEncoder;
        this.loginDAO = loginDAO;
        this.reader = reader;
        this.userRoleDao = userRoleDao;
    }

    @Override
    public void register() {
        ConsolePrinter.print("Enter login");
        String login = reader.read();
        boolean isAlreadyExists = loginDAO.checkIfUserExist(login);
        if (isAlreadyExists) {
            ConsolePrinter.print("Login already exists");
        } else {
            ConsolePrinter.print("Enter password");
            String password = reader.read();
            int encodedPswd = passwordEncoder.encode(password);
            loginDAO.addNewUser(login, encodedPswd);
            Role role = Role.USER;
            userRoleDao.addRoleForUser(login, role);
            ConsolePrinter.print("Successfully register");
        }
    }
}
