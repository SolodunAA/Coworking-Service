package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dao.LoginDao;
import app.dto.OperationResult;
import app.dto.RoleDto;
import app.services.AdminOperations;
import app.services.UserOperations;
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
import java.util.Map;
import java.util.Set;
@Exceptionable
@Auditable
@Loggable
public class RoomsManagementServlet extends HttpServlet {
    private final UserOperations userOperations;
    private final AdminOperations adminOperations;
    private final ObjectMapper objectMapper;
    private final LoginDao loginDao;

    public RoomsManagementServlet() {
        this.objectMapper = new ObjectMapper();
        this.userOperations = CoworkingApp.SERVICES_FACTORY.getUserOperations();
        this.adminOperations = CoworkingApp.SERVICES_FACTORY.getAdminOperations();
        this.loginDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getLoginDao();
    }

    /**
     * viewing all rooms and desks in coworking
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            Map<String, Set<Integer>> allRooms = userOperations.getAllRoomsAndDesks();
            String json = objectMapper.writeValueAsString(allRooms);
            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.getOutputStream().write(json.getBytes());

        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            String res = "You are not logged in to the booking system";
            resp.getOutputStream().write(res.getBytes());
        }
    }

    /**
     * deleting room in coworking
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

            JsonObject jsonObject = JsonParser.parseString(buffer.toString()).getAsJsonObject();
            String reqRoomName = jsonObject.get("placeName").getAsString();

            OperationResult operationResult = adminOperations.deleteRoom(reqRoomName);

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
     * add room in coworking
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

            OperationResult operationResult = adminOperations.addRoom(reqRoomName);

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
