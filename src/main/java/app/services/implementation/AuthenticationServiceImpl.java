package app.services.implementation;

import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final LoginDao loginDao;
    private final PasswordEncoder passwordEncoder;
    private final Reader reader;


    public AuthenticationServiceImpl(LoginDao loginDao,
                                     PasswordEncoder passwordEncoder,
                                     Reader reader) {
        this.loginDao = loginDao;
        this.passwordEncoder = passwordEncoder;
        this.reader = reader;
    }

    @Override
    public String auth() {
        try {
            ConsolePrinter.print("Enter login");
            String login = reader.read();
            ConsolePrinter.print("Enter password");
            String pswd = reader.read();
            boolean isUserExists = loginDao.checkIfUserExist(login);
            if (isUserExists) {
                int encodedPswd = passwordEncoder.encode(pswd);
                int savedEncodedPswd = loginDao.getEncodedPassword(login);
                if (encodedPswd == savedEncodedPswd) {
                    ConsolePrinter.print("Login Successful");
                    return login;
                } else {
                    ConsolePrinter.print("Wrong login or password");
                }

            } else {
                ConsolePrinter.print("Wrong login or password");
            }
            return null;
        } catch (Exception e) {
            ConsolePrinter.print("Something went wrong, try again");
        }
        return null;
    }
}