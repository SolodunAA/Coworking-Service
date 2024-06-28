package app.services;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.LoginDao;
import app.dao.postgresDao.PostgresLoginDao;
import app.in.ConsoleReader;
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

public class RegistrationServiceImplTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );
    private final LoginDao loginDao = new PostgresLoginDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    private final PasswordEncoder encoder = new HashEncoder();
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
    @DisplayName("registrations test")
    public void registerTest() {
        String login = "login";
        String password = "password";

        Mockito.when(crMock.read()).thenReturn(login, password);

        registrationService.register();

        int encodedPassword = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword = encoder.encode(password);
        assertThat(encodedPassword).isEqualTo(expectedEncodedPassword);
        assertThat(loginDao.checkIfUserExist(login)).isTrue();
    }

    @Test
    @DisplayName("fail to register with same login")
    public void registerFailTest() {
        String login = "login";
        String password1 = "password1";
        String password2 = "password2";

        Mockito.when(crMock.read()).thenReturn(login, password1);
        registrationService.register();

        int encodedPassword1 = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword1 = encoder.encode(password1);
        assertThat(encodedPassword1).isEqualTo(expectedEncodedPassword1);
        assertThat(loginDao.checkIfUserExist(login)).isTrue();

        //fail to register with same login
        Mockito.when(crMock.read()).thenReturn(login, password2);
        registrationService.register();

        int encodedPassword2 = loginDao.getEncodedPassword(login);
        int expectedEncodedPassword2 = encoder.encode(password2);
        assertThat(encodedPassword2).isNotEqualTo(expectedEncodedPassword2);
        assertThat(encodedPassword2).isEqualTo(expectedEncodedPassword1);
    }
}
