package app.dao;

import app.dao.postgresDao.PostgresBookingDao;
import app.dao.postgresDao.PostgresLoginDao;
import app.dao.postgresDao.PostgresPlaceDao;
import app.dto.BookingDto;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresBookingDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    LocalTime openTime = LocalTime.parse("08:00");
    LocalTime closeTime = LocalTime.parse("22:00");
    private final PlaceDao placeDao = new PostgresPlaceDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    private final BookingDao bookingDao = new PostgresBookingDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword(), placeDao, openTime, closeTime);
    private final LoginDao loginDao = new PostgresLoginDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

    @BeforeClass
    public static void beforeAll() {
        postgres.start();
        runMigrations();
    }

    private static void runMigrations() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            System.out.println("starting migration");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db.changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("migration finished successfully");
        } catch (Exception e ){
            System.out.println("Got SQL Exception " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        postgres.stop();
    }

    @Before
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE coworking.booking_table CASCADE");
            st.execute("TRUNCATE TABLE admin.user_table CASCADE");
            st.execute("ALTER SEQUENCE coworking.booking_id_seq RESTART WITH 1");
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("test method get all user bookings")
    public void getAllBookingsForUserTest(){
        String hallName = "Paris";
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date1 = LocalDate.parse("2024-06-28");
        LocalTime startTime1 = LocalTime.parse("10:00");
        LocalTime endTime1 = LocalTime.parse("21:00");
        int bookingId1 = 1;
        BookingDto bookingDto1 = new BookingDto(bookingId1, userLogin, hallName, 0, date1, startTime1, endTime1);

        String roomName = "Red";
        int deskNumber = 1;
        LocalDate date2 = LocalDate.parse("2024-06-20");
        LocalTime startTime2 = LocalTime.parse("11:00");
        LocalTime endTime2 = LocalTime.parse("17:00");
        int bookingId2 = 2;
        BookingDto bookingDto2 = new BookingDto(bookingId2, userLogin, roomName, deskNumber, date2, startTime2, endTime2);

        loginDao.addNewUser(userLogin, userEncodedPassword);
        bookingDao.addNewHallBooking(bookingDto1);
        bookingDao.addNewDeskBooking(bookingDto2);

        System.out.println(bookingDao.getAllBookingsForUser(userLogin));

        assertThat(bookingDao.getAllBookingsForUser(userLogin)).containsExactlyInAnyOrder(bookingDto1, bookingDto2);
    }
    @Test
    @DisplayName("test method get all bookings for all users")
    public void getAllBookingsAllUsersTest(){
        String hallName = "Paris";
        String userLogin1 = "user1";
        int userEncodedPassword1 = 12345;
        LocalDate date1 = LocalDate.parse("2024-06-29");
        LocalTime startTime1 = LocalTime.parse("15:00");
        LocalTime endTime1 = LocalTime.parse("21:00");
        int bookingId1 = 1;
        BookingDto bookingDto1 = new BookingDto(bookingId1, userLogin1, hallName, 0, date1, startTime1, endTime1);

        String roomName = "Red";
        String userLogin2 = "user2";
        int userEncodedPassword2 = 54321;
        int deskNumber = 1;
        LocalDate date2 = LocalDate.parse("2024-06-28");
        LocalTime startTime2 = LocalTime.parse("10:00");
        LocalTime endTime2 = LocalTime.parse("21:00");
        int bookingId2 = 2;
        BookingDto bookingDto2 = new BookingDto(bookingId2, userLogin2, roomName, deskNumber, date2, startTime2, endTime2);

        List<BookingDto> allBookingsForAllUsers = new ArrayList<>();
        allBookingsForAllUsers.add(bookingDto1);
        allBookingsForAllUsers.add(bookingDto2);

        loginDao.addNewUser(userLogin1, userEncodedPassword1);
        loginDao.addNewUser(userLogin2, userEncodedPassword2);
        bookingDao.addNewHallBooking(bookingDto1);
        bookingDao.addNewDeskBooking(bookingDto2);

        assertThat(bookingDao.getAllBookingsAllUsers()).containsAll(allBookingsForAllUsers);
    }
    @Test
    @DisplayName("get booking knowing it id")
    public void getBookingByIdTest(){
        String hallName = "Paris";
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("21:00");
        int bookingId = 1;
        BookingDto bookingDto = new BookingDto(bookingId, userLogin, hallName, 0, date, startTime, endTime);

        loginDao.addNewUser(userLogin, userEncodedPassword);
        bookingDao.addNewHallBooking(bookingDto);
        System.out.println(bookingDto);
        System.out.println(bookingDao.getBookingById(bookingId));

        assertThat(bookingDao.getBookingById(bookingId)).isEqualTo(bookingDto);
    }
    @Test
    @DisplayName("check to add new hall booking")
    public void addNewHallBookingTest(){
        String hallName = "Paris";
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("21:00");

        loginDao.addNewUser(userLogin, userEncodedPassword);

        assertThat(bookingDao.addNewHallBooking(new BookingDto(0, userLogin, hallName, 0, date, startTime, endTime))).
                isEqualTo("Booking added successfully");

    }
    @Test
    @DisplayName("check to add new desk booking")
    public void addNewDeskBookingTest(){
        String roomName = "Red";
        int deskNumber = 1;
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("21:00");

        loginDao.addNewUser(userLogin, userEncodedPassword);

        assertThat(bookingDao.addNewDeskBooking(new BookingDto(0, userLogin, roomName, 0, date, startTime, endTime))).
                isEqualTo("Booking added successfully");

    }

    @Test
    @DisplayName("deleting booking")
    public void deleteBooking(){
        String hallName = "Paris";
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("21:00");
        int bookingId = 1;

        loginDao.addNewUser(userLogin, userEncodedPassword);
        bookingDao.addNewHallBooking(new BookingDto(0, userLogin, hallName, 0, date, startTime, endTime));

        assertThat(bookingDao.deleteBooking(bookingId)).isEqualTo("Deleted successfully");
    }
    @Test
    @DisplayName("test method changing booking time")
    public void changeBookingDateTest(){
        String hallName = "Paris";
        String userLogin = "user";
        int userEncodedPassword = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("21:00");
        LocalDate newDate = LocalDate.parse("2024-06-29");
        int bookingId = 1;

        loginDao.addNewUser(userLogin, userEncodedPassword);
        bookingDao.addNewHallBooking(new BookingDto(0, userLogin, hallName, 0, date, startTime, endTime));

        assertThat(bookingDao.changeBookingDateAndTime(bookingId, newDate, startTime, endTime)).
                isEqualTo("Changed successfully");
    }

    @Test
    @DisplayName("test method get all available halls slots on date")
    public void getAvailableHallsSlotsOnDateTest(){
        String hallName = "Paris";
        String userLogin1 = "user1";
        int userEncodedPassword1 = 12345;
        LocalDate date = LocalDate.parse("2024-06-28");
        LocalTime startTime1 = LocalTime.parse("10:00");
        LocalTime endTime1 = LocalTime.parse("12:00");

        String userLogin2 = "user2";
        int userEncodedPassword2 = 54321;
        LocalTime startTime2 = LocalTime.parse("15:00");
        LocalTime endTime2 = LocalTime.parse("21:00");

        Set<String> allHalls = placeDao.getAllHalls();
        Set<LocalTime> allSlots = new HashSet<>();
        Map<String, Set<LocalTime>> availableHallSlots = new HashMap<>();
        LocalTime currentTime = openTime;
        while(currentTime.isBefore(closeTime)){
            allSlots.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        for(String hall: allHalls){
            availableHallSlots.put(hall, new HashSet<>(allSlots));
        }

        currentTime = startTime1;
        while(currentTime.isBefore(endTime1)){
            Set<LocalTime> set = availableHallSlots.get(hallName);
            set.remove(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        currentTime = startTime2;
        while(currentTime.isBefore(endTime2)){
            Set<LocalTime> set = availableHallSlots.get(hallName);
            set.remove(currentTime);
            currentTime = currentTime.plusHours(1);
        }

        loginDao.addNewUser(userLogin1, userEncodedPassword1);
        loginDao.addNewUser(userLogin2, userEncodedPassword2);
        bookingDao.addNewHallBooking(new BookingDto(0, userLogin1, hallName, 0, date, startTime1, endTime1));
        bookingDao.addNewHallBooking(new BookingDto(0, userLogin2, hallName, 0, date, startTime2, endTime2));

        assertThat(bookingDao.getAvailableHallsSlotsOnDate(date)).isEqualTo(availableHallSlots);
    }

    @Test
    @DisplayName("test method get all available desk slots on date")
    public void getAvailableRoomDesksSlotsOnDateTest(){
        String roomName = "Red";
        LocalDate date = LocalDate.parse("2024-06-28");

        String userLogin1 = "user1";
        int userEncodedPassword1 = 12345;
        int deskNumber1 = 1;
        LocalTime startTime1 = LocalTime.parse("10:00");
        LocalTime endTime1 = LocalTime.parse("21:00");

        String userLogin2 = "user2";
        int userEncodedPassword2 = 54321;
        int deskNumber2 = 2;
        LocalTime startTime2 = LocalTime.parse("15:00");
        LocalTime endTime2 = LocalTime.parse("21:00");

        Set<Integer> desksInRoom = placeDao.getSetOfAllDesksInRoom(roomName);
        Set<LocalTime> allSlots = new HashSet<>();
        Map<Integer, Set<LocalTime>> availableDeskSlots = new HashMap<>();
        LocalTime currentTime = openTime;
        while(currentTime.isBefore(closeTime)){
            allSlots.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        for(int desk: desksInRoom){
            availableDeskSlots.put(desk, new HashSet<>(allSlots));
        }

        currentTime = startTime1;
        while(currentTime.isBefore(endTime1)){
            Set<LocalTime> set = availableDeskSlots.get(deskNumber1);
            set.remove(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        currentTime = startTime2;
        while(currentTime.isBefore(endTime2)){
            Set<LocalTime> set = availableDeskSlots.get(deskNumber2);
            set.remove(currentTime);
            currentTime = currentTime.plusHours(1);
        }

        loginDao.addNewUser(userLogin1, userEncodedPassword1);
        loginDao.addNewUser(userLogin2, userEncodedPassword2);
        bookingDao.addNewDeskBooking((new BookingDto(0, userLogin1, roomName, 0, date, startTime1, endTime1)));
        bookingDao.addNewDeskBooking((new BookingDto(0, userLogin1, roomName, 0, date, startTime1, endTime1)));

        assertThat(bookingDao.getAvailableRoomDesksSlotsOnDate(date, roomName)).isEqualTo(availableDeskSlots);
    }


}
