package app.servlets;
import app.config.ConfigReader;
import app.dao.postgresDao.SQLParams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RegistrationServletTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

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
        if (testConfigFile.exists()) {
            testConfigFile.delete();
        }
    }

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RegistrationServlet registrationServlet = new RegistrationServlet();
    @Test
    @DisplayName("Check if the status is ok, end message Successfully register, when added new user")
    public void doPostSuccessfullyRegisterTest() throws Exception {

        String json = "{\"login\":\"testUser\",\"password\":\"myPassword\"}";

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String login = jsonObject.get("login").getAsString();

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        registrationServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Successfully register".getBytes());

        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_USER_EXISTS)) {
            ps.setString(1, login);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            assertThat(resultSet.getBoolean(1)).isTrue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("Check if the status is 404, end message Wrong login or password, when trying to add existing user")
    public void doPostExistsLoginRegisterTest() throws Exception {

        String json = "{\"login\":\"admin\",\"password\":\"admin\"}";

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        registrationServlet.doPost(request, response);
        System.out.println("status in test = " + response.getStatus());

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(servletOutputStream).write("Wrong login or password".getBytes());

    }
}
