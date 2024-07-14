package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.services.AuthenticationService;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
@Exceptionable
@Auditable
@Loggable
public class AuthenticationServlet extends HttpServlet {
    private  final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;

    public AuthenticationServlet() {
        this.objectMapper = new ObjectMapper();
        this.authenticationService = CoworkingApp.SERVICES_FACTORY.getAuthenticationService();
    }

    /**
     * user authentication, access to the reservation system
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        String requestBody = buffer.toString();
        UserDto userDto = objectMapper.readValue(requestBody, UserDto.class);

        OperationResult operationResult = authenticationService.auth(userDto);

        if(operationResult.getErrCode() == 200){
            HttpSession session = req.getSession();
            session.setAttribute("login", userDto.getLogin());
        }
        resp.setStatus(operationResult.getErrCode());
        resp.setContentType("application/json");

        String res = operationResult.getMessage();
        resp.getOutputStream().write(res.getBytes());
    }
}
