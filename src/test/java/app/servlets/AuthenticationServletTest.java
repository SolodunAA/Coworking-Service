package app.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@Isolated
public class AuthenticationServletTest extends BaseServletTest {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    AuthenticationServlet authenticationServlet = new AuthenticationServlet();
    @Test
    @DisplayName("Check if the status is ok, end message Login Successful, when log in existing user")
    public void doPostSuccessfullyAuthenticationTest() throws Exception {

        String json = "{\"login\":\"admin\",\"password\":\"admin\"}";
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String login = jsonObject.get("login").getAsString();

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);

        authenticationServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Login Successful".getBytes());
        verify(httpSession).setAttribute("login", login);
    }

    @Test
    @DisplayName("Check if the status is 404, end message Wrong login or password, when password is wrong")
    public void doPostWrongPasswordAuthenticationTest() throws Exception {

        String json = "{\"login\":\"admin\",\"password\":\"nimda\"}";

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        authenticationServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(servletOutputStream).write("Wrong login or password".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 404, end message Wrong login or password, when login doesn't exist")
    public void doPostWrongLoginAuthenticationTest() throws Exception {

        String json = "{\"login\":\"user\",\"password\":\"user\"}";

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        authenticationServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(servletOutputStream).write("Wrong login or password".getBytes());
    }

}
