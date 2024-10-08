package app.dao.postgresDao;

import app.config.YmlReader;
import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.BookingDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class PostgresBookingDao implements BookingDao {
    private final String url;
    private final String userName;
    private final String password;
    private final PlaceDao placeDao;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    @Autowired
    public PostgresBookingDao(YmlReader ymlReader, PlaceDao placeDao) {
        this.url = ymlReader.getUrl();
        this.userName = ymlReader.getUsername();
        this.password = ymlReader.getPassword();
        this.placeDao = placeDao;
        this.openTime = LocalTime.parse(ymlReader.getOpenTime());
        this.closeTime = LocalTime.parse(ymlReader.getCloseTime());
    }

    /**
     * constructor for testing
     * @param url
     * @param userName
     * @param password
     * @param placeDao
     * @param openTine
     * @param closeTime
     */
    public PostgresBookingDao(String url, String userName, String password, PlaceDao placeDao, LocalTime openTine, LocalTime closeTime) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.placeDao = placeDao;
        this.openTime = openTine;
        this.closeTime = closeTime;
    }

    @Override
    public Map<Integer, Set<LocalTime>> getAvailableRoomDesksSlotsOnDate(LocalDate date, String roomName) {
        Set<Integer> desksInRoom = placeDao.getSetOfAllDesksInRoom(roomName);
        Set<LocalTime> allSlots = new HashSet<>();
        Map<Integer, Set<LocalTime>> availableDeskSlots = new HashMap<>();
        LocalTime currentTime = openTime;
        while (currentTime.isBefore(closeTime)) {
            allSlots.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        for (int desk : desksInRoom) {
            availableDeskSlots.put(desk, new HashSet<>(allSlots));
        }
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_SLOTS_FOR_DESKS_ON_DATE)) {
            ps.setString(1, roomName);
            ps.setDate(2, Date.valueOf(date));
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    int deskNumber = resultSet.getInt("desk_number");
                    LocalTime startTime = LocalTime.parse(String.valueOf(resultSet.getTime("start_time")));
                    LocalTime endTime = LocalTime.parse(String.valueOf(resultSet.getTime("end_time")));
                    currentTime = startTime;
                    while (currentTime.isBefore(endTime)) {
                        availableDeskSlots.get(deskNumber).remove(currentTime);
                        currentTime = currentTime.plusHours(1);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return availableDeskSlots;
    }

    @Override
    public Map<String, Set<LocalTime>> getAvailableHallsSlotsOnDate(LocalDate date) {
        Set<String> allHalls = placeDao.getAllHalls();
        Set<LocalTime> allSlots = new HashSet<>();
        Map<String, Set<LocalTime>> availableHallSlots = new HashMap<>();
        LocalTime currentTime = openTime;
        while (currentTime.isBefore(closeTime)) {
            allSlots.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }
        for (String hall : allHalls) {
            availableHallSlots.put(hall, new HashSet<>(allSlots));
        }
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_SLOTS_FOR_HALLS_ON_DATE)) {
            ps.setDate(1, Date.valueOf(date));
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    String hallName = resultSet.getString("place_name");
                    LocalTime startTime = LocalTime.parse(String.valueOf(resultSet.getTime("start_time")));
                    LocalTime endTime = LocalTime.parse(String.valueOf(resultSet.getTime("end_time")));
                    LocalTime currTime = startTime;
                    System.out.println(hallName + " " + startTime + " " + endTime);
                    while (currTime.isBefore(endTime)) {
                        Set<LocalTime> set = availableHallSlots.get(hallName);
                        set.remove(currTime);
                        currTime = currTime.plusHours(1);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return availableHallSlots;
    }

    @Override
    public String addNewHallBooking(BookingDto bookingDto) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
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

    @Override
    public String addNewDeskBooking(BookingDto bookingDto) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_DESK_BOOKING)) {
            ps.setString(1, bookingDto.getUserLogin());
            ps.setString(2, bookingDto.getPlaceName());
            ps.setString(3, bookingDto.getPlaceName());
            ps.setInt(4, bookingDto.getDeskNumber());
            ps.setDate(5, Date.valueOf(bookingDto.getDate()));
            ps.setTime(6, Time.valueOf(bookingDto.getStartTime()));
            ps.setTime(7, Time.valueOf(bookingDto.getEndTime()));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Booking added successfully";
    }

    @Override
    public String deleteBooking(int bookingID) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.DELETE_BOOKING)) {
            ps.setInt(1, bookingID);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Deleted successfully";
    }

    @Override
    public List<BookingDto> getAllBookingsForUser(String userLogin) {
        List<BookingDto> userBookingDtos = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_BOOKINGS)) {
            ps.setString(1, userLogin);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    int bookingID = resultSet.getInt("booking_id");
                    String placeName = resultSet.getString("place_name");
                    int deskNumber = resultSet.getInt("desk_number");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    LocalTime startTime = resultSet.getTime("start_time").toLocalTime();
                    LocalTime endTime = resultSet.getTime("end_time").toLocalTime();
                    BookingDto bookingDto = new BookingDto(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
                    userBookingDtos.add(bookingDto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userBookingDtos;
    }

    @Override
    public List<BookingDto> getAllBookingsAllUsers() {
        List<BookingDto> userBookingDtos = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_ALL_BOOKINGS)) {
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    int bookingID = resultSet.getInt("booking_id");
                    String userLogin = resultSet.getString("user_login");
                    String placeName = resultSet.getString("place_name");
                    int deskNumber = resultSet.getInt("desk_number");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    LocalTime startTime = resultSet.getTime("start_time").toLocalTime();
                    LocalTime endTime = resultSet.getTime("end_time").toLocalTime();
                    BookingDto bookingDto = new BookingDto(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
                    userBookingDtos.add(bookingDto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userBookingDtos;
    }

    @Override
    public String changeBookingDateAndTime(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.UPDATE_BOOKING_DATE)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(startTime));
            ps.setTime(3, Time.valueOf(endTime));
            ps.setInt(4, bookingId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Changed successfully";
    }

    @Override
    public BookingDto getBookingById(int bookingId) {
        BookingDto bookingDto = null;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_BOOKING_BY_ID)) {
            ps.setInt(1, bookingId);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    int bookingID = resultSet.getInt("booking_id");
                    String userLogin = resultSet.getString("user_login");
                    String placeName = resultSet.getString("place_name");
                    int deskNumber = resultSet.getInt("desk_number");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    LocalTime startTime = resultSet.getTime("start_time").toLocalTime();
                    LocalTime endTime = resultSet.getTime("end_time").toLocalTime();
                    bookingDto = new BookingDto(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bookingDto;
    }

    private Set<String> getUsersWhoHaveBookings() {
        Set<String> allLogins = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_LOGINS_WHO_BOOKED)) {
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    String userLogin = resultSet.getString("user_login");
                    allLogins.add(userLogin);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allLogins;
    }

    @Override
    public boolean isUserHaveBookingWithId(String login, int bookingId) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_USER_HAVE_BOOKING_ID)) {
            ps.setString(1, login);
            ps.setInt(2, bookingId);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    exists = resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }
}
