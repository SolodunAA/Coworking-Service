package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dao.LoginDao;
import app.dto.OperationResult;
import app.dto.ReqDeskDto;
import app.dto.RoleDto;
import app.services.AdminOperations;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
@Exceptionable
@Auditable
@Loggable
public class DesksManagementServlet extends HttpServlet {
    private final AdminOperations adminOperations;
    private final ObjectMapper objectMapper;
    private final LoginDao loginDao;

    public DesksManagementServlet() {
        this.objectMapper = new ObjectMapper();
        this.adminOperations = CoworkingApp.SERVICES_FACTORY.getAdminOperations();
        this.loginDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getLoginDao();
    }

    /**
     * deleting desk in coworking
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String requestBody = buffer.toString();
            ReqDeskDto reqDeskDto = objectMapper.readValue(requestBody, ReqDeskDto.class);

            OperationResult operationResult = adminOperations.deleteDesk(
                    reqDeskDto.getRoomName(),
                    reqDeskDto.getDeskNumber());

            resp.setStatus(operationResult.getErrCode());
            resp.setContentType("application/json");
            String res = operationResult.getMessage();
            resp.getOutputStream().write(res.getBytes());

        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            String res = "No access";
            resp.getOutputStream().write(res.getBytes());
        }
    }

    /**
     * add desk in coworking
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            JsonObject jsonObject = JsonParser.parseString(buffer.toString()).getAsJsonObject();
            String reqRoomName = jsonObject.get("placeName").getAsString();

            OperationResult operationResult = adminOperations.addDesk(reqRoomName);

            resp.setStatus(operationResult.getErrCode());
            resp.setContentType("application/json");
            String res = operationResult.getMessage();
            resp.getOutputStream().write(res.getBytes());

        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            String res = "No access";
            resp.getOutputStream().write(res.getBytes());
        }
    }
}
