package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dto.Role;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.RegistrationService;

public class RegistrationServiceImpl implements RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final LoginDao loginDao;
    private final Reader reader;

    public RegistrationServiceImpl(PasswordEncoder passwordEncoder,
                                   LoginDao loginDAO,
                                   Reader reader) {
        this.passwordEncoder = passwordEncoder;
        this.loginDao = loginDAO;
        this.reader = reader;
    }

    @Override
    public void register() {
        try {
            ConsolePrinter.print("Enter login");
            String login = reader.read();
            boolean isAlreadyExists = loginDao.checkIfUserExist(login);
            if (isAlreadyExists) {
                ConsolePrinter.print("Login already exists");
            } else {
                ConsolePrinter.print("Enter password");
                String password = reader.read();
                int encodedPswd = passwordEncoder.encode(password);
                loginDao.addNewUser(login, encodedPswd);
                ConsolePrinter.print("Successfully register");
            }
        } catch (Exception e) {
            ConsolePrinter.print("Something went wrong, try again");
        }
    }
}
