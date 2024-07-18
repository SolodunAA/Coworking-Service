package app.dao;

import app.dao.postgresDao.PostgresLoginDao;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostgresLoginDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    private final LoginDao loginDao = new PostgresLoginDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

    @BeforeAll
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

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @AfterEach
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE admin.user_table CASCADE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("checking the addition of a new user to the user table")
    public void addNewUserTest() {

        String newLogin = "login";
        int encodedPassword = 12345;

        loginDao.addNewUser(newLogin, encodedPassword);

        assertThat(loginDao.checkIfUserExist(newLogin)).isTrue();
    }

    @Test
    @DisplayName("checks the impossibility of adding a second user with the same login")
    public void addDuplicateUserTest() {

        String newLogin = "login";
        int encodedPassword1 = 12345;
        int encodedPassword2 = 123456;

        loginDao.addNewUser(newLogin, encodedPassword1);

        assertThatThrownBy(() -> loginDao.addNewUser(newLogin, encodedPassword2)).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("verification of receipt of password by login")
    public void getEncodedPasswordTest() {

        String newLogin = "login";
        int encodedPassword = 12345;

        loginDao.addNewUser(newLogin, encodedPassword);

        assertThat(loginDao.getEncodedPassword(newLogin)).isEqualTo(encodedPassword);
    }
    @Test
    @DisplayName("testing a method to check whether such a login exists in the user table")
    public void checkIfUserExistTest() {

        String newLogin = "login";
        int encodedPassword = 12345;

        loginDao.addNewUser(newLogin, encodedPassword);
        assertThat(loginDao.checkIfUserExist(newLogin)).isTrue();
    }

}
