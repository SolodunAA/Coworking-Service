package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigReader;
import app.dao.postgresDao.SQLParams;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class GetAllBookingsByDateServletTest {
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
    public void addNewUser(String login, String password) {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement ps1 = connection.prepareStatement(SQLParams.INSERT_LOGIN_AND_ROLE);
             PreparedStatement ps2 = connection.prepareStatement(SQLParams.INSERT_PASSWORD)) {
            ps1.setString(1, login);
            ps1.setString(2, "user");
            ps1.executeUpdate();
            ps2.setString(1, login);
            ps2.setInt(2, passwordEncoder.encode(password));
            ps2.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PasswordEncoder passwordEncoder = new HashEncoder();
    GetAllBookingsByDateServlet getAllBookingsByDateServlet = new GetAllBookingsByDateServlet();


    @Test
    @DisplayName("Check if the status is ok, when get all bookings")
    public void doGetSuccessfullyTest() throws Exception {

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        getAllBookingsByDateServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

    }
    @Test
    @DisplayName("Check if the status is 403, when get all bookings by not admin user")
    public void doGetNotAdminFailTest() throws Exception {
        String login = "justUser";
        String password = "justPassword";

        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        getAllBookingsByDateServlet.doGet(request, response);

        verify(httpSession).getAttribute("login");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("No access".getBytes());
    }
}
