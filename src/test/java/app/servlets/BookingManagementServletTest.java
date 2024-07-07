package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.postgresDao.SQLParams;
import app.dto.BookingDto;
import app.dto.ReqBookingHallDto;
import app.mapper.BookingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.*;
import java.sql.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@Isolated
public class BookingManagementServletTest extends BaseServletTest {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    BookingManagementServlet bookingManagementServlet = new BookingManagementServlet();
    PasswordEncoder passwordEncoder = new HashEncoder();
    ObjectMapper objectMapper = new ObjectMapper();


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

    public String addNewHallBooking(BookingDto bookingDto) {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_HALL_BOOKING)) {
            ps.setString(1, bookingDto.getUserLogin());
            ps.setString(2, bookingDto.getPlaceName());
            ps.setDate(3, Date.valueOf(bookingDto.getDate()));
            ps.setTime(4, Time.valueOf(bookingDto.getStartTime()));
            ps.setTime(5, Time.valueOf(bookingDto.getEndTime()));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Booking added successfully";
    }

    @Test
    @DisplayName("Check if the status is ok, when booking is changing")
    public void doPutSuccessfullyChangeBookingTest() throws Exception {

        String jsonChanging = """
                   {
                      "bookingId": 1,
                      "date": "2024-07-05",
                      "startTime": "11:00:00",
                      "endTime": "17:00:00"
                      }
                """;

        String jsonAddBooking = """
                   {
                   "placeName": "Paris",
                   "date": "2024-01-01",
                   "startTime": "10:00:00",
                   "endTime": "15:00:00"
                   }
                """;

        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        objectMapper.registerModule(new JSR310Module());
        ReqBookingHallDto reqHall = objectMapper.readValue(jsonAddBooking, ReqBookingHallDto.class);
        BookingDto bookingDto = BookingMapper.INSTANCE.addLoginAndDeskToBookingHallDTO(reqHall, login);
        addNewHallBooking(bookingDto);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(jsonChanging));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookingManagementServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Changed successfully".getBytes());
    }

    @Test
    @DisplayName("Check if the status is ok, when booking is deleted")
    public void doDeleteSuccessfullyChangeBookingTest() throws Exception {

        String jsonDeleting = """
                   {
                      "bookingId": 1
                   }
                """;

        String jsonAddBooking = """
                   {
                   "placeName": "Paris",
                   "date": "2024-01-01",
                   "startTime": "10:00:00",
                   "endTime": "15:00:00"
                   }
                """;

        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        objectMapper.registerModule(new JSR310Module());
        ReqBookingHallDto reqHall = objectMapper.readValue(jsonAddBooking, ReqBookingHallDto.class);
        BookingDto bookingDto = BookingMapper.INSTANCE.addLoginAndDeskToBookingHallDTO(reqHall, login);
        addNewHallBooking(bookingDto);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(jsonDeleting));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookingManagementServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Deleted successfully".getBytes());
    }
    @Test
    @DisplayName("Check if the status is ok, when booking is deleted")
    public void doGetSuccessfullyChangeBookingTest() throws Exception {

        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookingManagementServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
