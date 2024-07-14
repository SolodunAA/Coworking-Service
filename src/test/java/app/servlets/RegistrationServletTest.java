package app.servlets;
import app.dao.postgresDao.SQLParams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Isolated
public class RegistrationServletTest extends BaseServletTest {
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
