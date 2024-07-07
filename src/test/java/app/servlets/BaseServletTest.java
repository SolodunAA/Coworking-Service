package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigReader;
import app.dao.LoginDao;
import app.dao.postgresDao.PostgresLoginDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Isolated;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

@Isolated
public abstract class BaseServletTest {
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    ).withReuse(true);

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
        var url = postgres.getJdbcUrl();
        var pswd = postgres.getPassword();
        var user = postgres.getUsername();
        var conf = """
                db_url = <URL>
                db_password = <PSWD>
                db_user = <USER>
                                
                admin_login = admin
                admin_encrypted_password = 92668751
                default_rooms = red,blue,green
                default_desks_in_room = 5
                default_halls = Moscow,Manama
                open_time = 08:00
                close_time = 22:00
                changelog_path = db.changelog/changelog.xml
                """
                .replace("<URL>", url)
                .replace("<PSWD>", pswd)
                .replace("<USER>", user);
        try (var fw = new FileWriter(ConfigReader.TEST_CONFIG_PATH)) {
            fw.write(conf);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterAll
    public static void clear() {
        File testConfigFile = new File(ConfigReader.TEST_CONFIG_PATH);
/*        if (testConfigFile.exists()) {
            testConfigFile.delete();
        }
        postgres.stop();*/
    }


    public static final String INSERT_PASSWORD = """
            INSERT INTO admin.password_table
            (user_id, password_encoded)
            VALUES ((SELECT user_id FROM admin.user_table WHERE user_login = ?), ?);
            """;
    @AfterEach
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE admin.user_table CASCADE;");
            st.execute("TRUNCATE TABLE coworking.booking_table CASCADE;");
            st.execute("ALTER SEQUENCE coworking.booking_id_seq RESTART WITH 1");
            st.execute("ALTER SEQUENCE admin.user_id_seq RESTART WITH 1;");
            st.execute("INSERT INTO admin.user_table (user_login, role) VALUES ('admin', 'admin');");
            st.execute("INSERT INTO admin.password_table (user_id, password_encoded) VALUES ((SELECT user_id FROM admin.user_table WHERE user_login = 'admin'), 92668751);");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
