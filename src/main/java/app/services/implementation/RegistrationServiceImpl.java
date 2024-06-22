package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dto.Role;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.RegistrationService;

public class RegistrationServiceImpl implements RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final LoginDao loginDAO;
    private final Reader reader;

    public RegistrationServiceImpl(PasswordEncoder passwordEncoder,
                                   LoginDao loginDAO,
                                   Reader reader) {
        this.passwordEncoder = passwordEncoder;
        this.loginDAO = loginDAO;
        this.reader = reader;
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
            ConsolePrinter.print("Successfully register");
        }
    }
}
