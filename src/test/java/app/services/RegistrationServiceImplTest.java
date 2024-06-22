package app.services;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dao.inMemoryDao.InMemoryLoginDao;
import app.in.ConsoleReader;
import app.services.implementation.RegistrationServiceImpl;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationServiceImplTest {
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    @Test
    public void registerTest() {
        String login = "login";
        String password = "password";
        PasswordEncoder encoder = new HashEncoder();
        LoginDao loginDao = new InMemoryLoginDao();
        RegistrationService registrationService = new RegistrationServiceImpl(encoder, loginDao, crMock);

        Mockito.when(crMock.read()).thenReturn(login, password);

        registrationService.register();

        int encodedPassword = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword = encoder.encode(password);
        assertThat(encodedPassword).isEqualTo(expectedEncodedPassword);

    }


    @Test
    public void registerFailTest() {
        String login = "login";
        String password1 = "password1";
        String password2 = "password2";
        PasswordEncoder encoder = new HashEncoder();
        LoginDao loginDao = new InMemoryLoginDao();
        RegistrationService registrationService = new RegistrationServiceImpl(encoder, loginDao, crMock);


        //successfully register
        Mockito.when(crMock.read()).thenReturn(login, password1);
        registrationService.register();

        int encodedPassword1 = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword1 = encoder.encode(password1);
        assertThat(encodedPassword1).isEqualTo(expectedEncodedPassword1);

        //fail to register with same login
        Mockito.when(crMock.read()).thenReturn(login, password2);
        registrationService.register();

        int encodedPassword2 = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword2 = encoder.encode(password2);
        assertThat(encodedPassword2).isNotEqualTo(expectedEncodedPassword2);
        assertThat(encodedPassword2).isEqualTo(expectedEncodedPassword1);
    }
}
