package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dao.LoginDao;
import app.dto.BookingDto;
import app.dto.ReqAvailableHallDto;
import app.dto.OperationResult;
import app.dto.ReqBookingHallDto;
import app.mapper.BookingMapper;
import app.services.UserOperations;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
@Exceptionable
@Auditable
@Loggable
public class BookHallServlet extends HttpServlet {
    private final UserOperations userOperations;
    private final ObjectMapper objectMapper;
    private final LoginDao loginDao;

    public BookHallServlet() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        this.userOperations = CoworkingApp.SERVICES_FACTORY.getUserOperations();
        this.loginDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getLoginDao();
    }

    /**
     * hall reservation
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String requestBody = buffer.toString();
            ReqBookingHallDto reqHall = objectMapper.readValue(requestBody, ReqBookingHallDto.class);
            BookingDto bookingDto = BookingMapper.INSTANCE.addLoginAndDeskToBookingHallDTO(reqHall, login);

            OperationResult operationResult = userOperations.bookHall(bookingDto);

            resp.setStatus(operationResult.getErrCode());
            resp.setContentType("application/json");
            String res = operationResult.getMessage();
            resp.getOutputStream().write(res.getBytes());

        } else {

            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            String res = "You are not logged in to the booking system";
            resp.getOutputStream().write(res.getBytes());
        }
    }

    /**
     * viewing all available time slots
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String requestBody = buffer.toString();
            ReqAvailableHallDto reqAvailableHallDto = objectMapper.readValue(requestBody, ReqAvailableHallDto.class);

            Map<String, Set<LocalTime>> availableSlots = userOperations.getAllAvailableHallSlotsOnDate(reqAvailableHallDto.getLocalDate());
            String json = objectMapper.writeValueAsString(availableSlots);
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
}
