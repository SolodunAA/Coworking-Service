package app.servlets;

import app.annotations.Auditable;
import app.annotations.Exceptionable;
import app.annotations.Loggable;
import app.dao.LoginDao;
import app.dto.BookingDto;
import app.dto.RoleDto;
import app.services.AdminOperations;
import app.start.CoworkingApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
@Exceptionable
@Auditable
@Loggable
public class GetAllBookingsByDateServlet extends HttpServlet {
    private final AdminOperations adminOperations;
    private final ObjectMapper objectMapper;
    private final LoginDao loginDao;

    public GetAllBookingsByDateServlet() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        this.adminOperations = CoworkingApp.SERVICES_FACTORY.getAdminOperations();
        this.loginDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getLoginDao();
    }
    /**
     * viewing all halls in coworking
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = (String) req.getSession().getAttribute("login");

        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {

            List<BookingDto> bookingDtos = adminOperations.getAllBookings();
            String json = objectMapper.writeValueAsString(bookingDtos);
            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.getOutputStream().write(json.getBytes());

        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            String res = "No access";
            resp.getOutputStream().write(res.getBytes());
        }
    }
}
