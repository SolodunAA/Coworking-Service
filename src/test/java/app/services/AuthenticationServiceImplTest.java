package app.services;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dao.inMemoryDao.InMemoryLoginDao;
import app.in.ConsoleReader;
import app.services.implementation.AuthenticationServiceImpl;
import app.services.implementation.RegistrationServiceImpl;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationServiceImplTest {
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    @Test
    public void authUnknownUserTest() {
        LoginDao loginDao = new InMemoryLoginDao();
        PasswordEncoder passwordEncoder = new HashEncoder();

        AuthenticationService authenticationService = new AuthenticationServiceImpl(loginDao, passwordEncoder, crMock);

        String login = "user1";
        String password = "password1";
        Mockito.when(crMock.read()).thenReturn(login, password);

        assertThat(authenticationService.auth()).isNull();
    }

    @Test
    public void authWrongPasswordTest() {
        LoginDao loginDao = new InMemoryLoginDao();
        PasswordEncoder passwordEncoder = new HashEncoder();
        AuthenticationService authenticationService = new AuthenticationServiceImpl(loginDao, passwordEncoder, crMock);
        PasswordEncoder encoder = new HashEncoder();
        RegistrationService registrationService = new RegistrationServiceImpl(encoder, loginDao, crMock);

        String loginReg = "user";
        String passwordReg = "password";
        String passwordWrong ="1234";
        Mockito.when(crMock.read()).thenReturn(loginReg, passwordReg, loginReg, passwordWrong);

        registrationService.register();

        assertThat(authenticationService.auth()).isNull();
    }

    @Test
    public void authTest() {
        String login = "user";
        String password = "password";
        LoginDao loginDao = new InMemoryLoginDao();
        PasswordEncoder passwordEncoder = new HashEncoder();
        loginDao.addNewUser(login, passwordEncoder.encode(password));

        AuthenticationService authenticationService = new AuthenticationServiceImpl(loginDao, passwordEncoder, crMock);

        Mockito.when(crMock.read()).thenReturn(login, password);

        assertThat(authenticationService.auth()).isEqualTo(login);
    }
}
