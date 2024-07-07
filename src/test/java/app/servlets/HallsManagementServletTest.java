package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.postgresDao.SQLParams;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@Isolated
public class HallsManagementServletTest extends BaseServletTest {

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HallsManagementServlet hallsManagementServlet = new HallsManagementServlet();
    PasswordEncoder passwordEncoder = new HashEncoder();

    public boolean isPlaceExists(String placeName) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_PLACE_EXISTS)) {
            ps.setString(1, placeName);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                exists = resultSet.getBoolean(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
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

    @Test
    @DisplayName("Check if the status is ok, when added new hall")
    public void doPostAddHallSuccessfullyTest() throws Exception {

        String json = """
                   {
                       "placeName": "Rome"
                   }
                """;

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        hallsManagementServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Added successfully".getBytes());

        String placeName = "Rome";

        assertThat(isPlaceExists(placeName)).isTrue();

    }

    @Test
    @DisplayName("Check if the status is ok, when deleted the hall")
    public void doDeleteHallSuccessfullyTest() throws Exception {

        String json = """
                   {
                       "placeName": "Paris"
                   }
                """;

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        hallsManagementServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Deleted successfully".getBytes());

        String placeName = "Paris";

        assertThat(isPlaceExists(placeName)).isFalse();

    }

    @Test
    @DisplayName("Check if the status is ok, when get all halls")
    public void doGetHallSuccessfullyTest() throws Exception {

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("admin");

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        hallsManagementServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

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

        hallsManagementServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("No access".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 403, when delete by not admin user")
    public void doDeleteNotAdminFailTest() throws Exception {
        String login = "user";
        String password = "password";

        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        hallsManagementServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("No access".getBytes());
    }

}
