package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.services.RegistrationService;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@Exceptionable
@Auditable
@Loggable
public class RegistrationServlet extends HttpServlet {
    private final RegistrationService registrationService;
    private final ObjectMapper objectMapper;

    public RegistrationServlet() {
        this.objectMapper = new ObjectMapper();
        this.registrationService = CoworkingApp.SERVICES_FACTORY.getRegistrationService();
    }

    /**
     * registration of a new user in the system
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        String requestBody = buffer.toString();
        UserDto userDto = objectMapper.readValue(requestBody, UserDto.class);

        OperationResult operationResult = registrationService.register(userDto);
        resp.setStatus(operationResult.getErrCode());
        resp.setContentType("application/json");
        String res = operationResult.getMessage();
        resp.getOutputStream().write(res.getBytes());
    }
}
