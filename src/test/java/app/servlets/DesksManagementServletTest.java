package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigReader;
import app.dao.postgresDao.SQLParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class DesksManagementServletTest {
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

    @AfterEach
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE admin.user_table CASCADE");;
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public boolean isDeskExistsInRoom(String roomName, int deskNumber) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_DESK_EXISTS)) {
            ps.setString(1, roomName);
            ps.setInt(2, deskNumber);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                exists = resultSet.getBoolean(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }


    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    DesksManagementServlet desksManagementServlet = new DesksManagementServlet();
    PasswordEncoder passwordEncoder = new HashEncoder();
    ObjectMapper objectMapper = new ObjectMapper();



    @Test
    @DisplayName("Check if the status is ok, when added new desk to room")
    public void doPostAddRoomSuccessfullyHallTest() throws Exception {

        String json = """
                   {
                       "placeName": "Red"
                   }
                """;

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        desksManagementServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Desk added successfully".getBytes());
    }

    @Test
    @DisplayName("Check if the status is ok, when deleted desk")
    public void doDeleteDeskFromRoomSuccessfullyHallTest() throws Exception {

        String json = """
                       {
                       "roomName": "Red",
                       "deskNumber": "2"
                       }
                """;

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        desksManagementServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Desk deleted successfully".getBytes());

        String placeName = "Red";
        int deskNumber = 2;

        assertThat(isDeskExistsInRoom(placeName, deskNumber)).isFalse();
    }

    @Test
    @DisplayName("Check if the status is 403, when post by not admin user")
    public void doPostNotAdminFailTest() throws Exception {
        String login = "justUser";
        String password = "justPassword";

        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        desksManagementServlet.doPost(request, response);

        verify(httpSession).getAttribute("login");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("No access".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 403, when delete by not admin user")
    public void doDeleteNotAdminFailTest() throws Exception {
        String login = "justUser";
        String password = "justPassword";

        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        desksManagementServlet.doDelete(request, response);

        verify(httpSession).getAttribute("login");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("No access".getBytes());
    }
}
