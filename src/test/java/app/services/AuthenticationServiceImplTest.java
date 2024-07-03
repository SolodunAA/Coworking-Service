package app.services;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dao.postgresDao.PostgresLoginDao;
import app.in.ConsoleReader;
import app.services.implementation.AuthenticationServiceImpl;
import app.services.implementation.RegistrationServiceImpl;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationServiceImplTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );
    private final LoginDao loginDao = new PostgresLoginDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    private final PasswordEncoder encoder = new HashEncoder();
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl(loginDao, encoder, crMock);
    private final RegistrationService registrationService = new RegistrationServiceImpl(encoder, loginDao, crMock);


    @BeforeClass
    public static void beforeAll() {
        postgres.start();
        runMigrations();
    }

    private static void runMigrations() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            System.out.println("starting migration");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db.changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("migration finished successfully");
        } catch (Exception e ){
            System.out.println("Got SQL Exception " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        postgres.stop();
    }

    @Before
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE admin.user_table CASCADE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("authentication of unknown user")
    public void authUnknownUserTest() {

        String login = "user1";
        String password = "password1";
        Mockito.when(crMock.read()).thenReturn(login, password);

        assertThat(authenticationService.auth()).isNull();
    }

    @Test
    @DisplayName("authentication with wrong password")
    public void authWrongPasswordTest() {

        String loginReg = "user";
        String passwordReg = "password";
        String passwordWrong ="1234";
        Mockito.when(crMock.read()).thenReturn(loginReg, passwordReg, loginReg, passwordWrong);

        registrationService.register();

        assertThat(authenticationService.auth()).isNull();
    }

    @Test
    @DisplayName("successfully authentication")
    public void authTest() {
        String login = "user";
        String password = "password";

        Mockito.when(crMock.read()).thenReturn(login, password, login, password);
        registrationService.register();

        assertThat(authenticationService.auth()).isEqualTo(login);
    }
}
