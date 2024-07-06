package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dao.LoginDao;
import app.dto.*;
import app.mapper.BookingMapper;
import app.services.UserOperations;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Exceptionable
@Auditable
@Loggable
public class BookingManagementServlet extends HttpServlet {
    private final UserOperations userOperations;
    private final ObjectMapper objectMapper;
    private final LoginDao loginDao;

    public BookingManagementServlet() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        this.userOperations = CoworkingApp.SERVICES_FACTORY.getUserOperations();
        this.loginDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getLoginDao();
    }

    /**
     * change date and time of bookings
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String requestBody = buffer.toString();
            ReqChangingBookingDto reqChangingBookingDto = objectMapper.readValue(requestBody, ReqChangingBookingDto.class);
            OperationResult operationResult = userOperations.changeBookingDateAndTime(
                    reqChangingBookingDto.getBookingId(),
                    reqChangingBookingDto.getDate(),
                    reqChangingBookingDto.getStartTime(),
                    reqChangingBookingDto.getEndTime()
            );

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
     * delete booking
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            JsonObject jsonObject = JsonParser.parseString(buffer.toString()).getAsJsonObject();
            int bookingId = jsonObject.get("bookingId").getAsInt();
            OperationResult operationResult = userOperations.deleteBookings(login, bookingId);

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
     * view all user bookings
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login)) {

            List<BookingDto> allUserBooking = userOperations.getAllUserBooking(login);
            List<RespBookingDto> allUserBookingsForPrinting = new ArrayList<>();
            for(BookingDto bookingDto: allUserBooking){
                allUserBookingsForPrinting.add(BookingMapper.INSTANCE.bookingDtoToRespBookingDto(bookingDto));
            }
            String json = objectMapper.writeValueAsString(allUserBookingsForPrinting);
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