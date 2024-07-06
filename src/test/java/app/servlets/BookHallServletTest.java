package app.servlets;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigReader;
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
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.sql.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class BookHallServletTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
        var url = postgres.getJdbcUrl();
        var pswd = postgres.getPassword();
        var user = postgres.getUsername();
        var conf = """
                db_url = <URL>
                db_password = <PSWD>
                db_user = <USER>
                                
                admin_login = admin
                admin_encrypted_password = 92668751
                default_rooms = red,blue,green
                default_desks_in_room = 5
                default_halls = Moscow,Manama
                open_time = 08:00
                close_time = 22:00
                changelog_path = db.changelog/changelog.xml
                """
                .replace("<URL>", url)
                .replace("<PSWD>", pswd)
                .replace("<USER>", user);
        try (var fw = new FileWriter(ConfigReader.TEST_CONFIG_PATH)) {
            fw.write(conf);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterAll
    public static void clear() {
        File testConfigFile = new File(ConfigReader.TEST_CONFIG_PATH);
        if (testConfigFile.exists()) {
            testConfigFile.delete();
        }
    }

    @AfterEach
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE admin.user_table CASCADE");
            st.execute("TRUNCATE TABLE coworking.booking_table CASCADE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    BookHallServlet bookHallServlet = new BookHallServlet();
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
    @DisplayName("Check if the status is ok, end message Booking added successfully, when booking is success")
    public void doPostSuccessfullyHallBookingTest() throws Exception {

        String json = """
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

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(servletOutputStream).write("Booking added successfully".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 404, start time is earlier than openTime")
    public void doPostWrongStartTimeHallBookingTest() throws Exception {

        String json = """
                   {
                   "placeName": "Paris",
                   "date": "2024-01-01",
                   "startTime": "07:00:00",
                   "endTime": "15:00:00"
                   }
                """;
        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(servletOutputStream).write("The booking start time does not coincide with working hours".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 400, start end is after than closeTime")
    public void doPostWrongEndTimeHallBookingTest() throws Exception {

        String json = """
                   {
                   "placeName": "Paris",
                   "date": "2024-01-01",
                   "startTime": "10:00:00",
                   "endTime": "23:00:00"
                   }
                """;
        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(servletOutputStream).write("The booking end time does not coincide with working hours".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 404, when hall doesn't exist")
    public void doPostWrongHallBookingTest() throws Exception {

        String json = """
                   {
                   "placeName": "Rome",
                   "date": "2024-01-01",
                   "startTime": "10:00:00",
                   "endTime": "15:00:00"
                   }
                """;
        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(servletOutputStream).write("This hall doesn't exist".getBytes());
    }

    @Test
    @DisplayName("Check if the status is 400, when hall doesn't exist")
    public void doPostWrongSlotBookingTest() throws Exception {

        String json = """
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
        ReqBookingHallDto reqHall = objectMapper.readValue(json, ReqBookingHallDto.class);
        BookingDto bookingDto = BookingMapper.INSTANCE.addLoginAndDeskToBookingHallDTO(reqHall, login);
        addNewHallBooking(bookingDto);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(servletOutputStream).write("Time slot already reserved".getBytes());
    }

    @Test
    @DisplayName("Check if user doesn't log in")
    public void doPostNotLogInUserTest() throws Exception {

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("userWrong");

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("You are not logged in to the booking system".getBytes());
    }

    @Test
    @DisplayName("Check if user doesn't log in")
    public void doGetNotLogInUserTest() throws Exception {

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn("userWrong");

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(servletOutputStream).write("You are not logged in to the booking system".getBytes());
    }

    @Test
    @DisplayName("Check if the status is ok, when get available slots")
    public void doGetSuccessfullyTest() throws Exception {

        String json = """
                   {
                   "date": "2024-01-01"
                   }
                """;

        String login = "user";
        String password = "userPassword";
        addNewUser(login, password);

        HttpSession httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getSession().getAttribute("login")).thenReturn(login);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        bookHallServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
