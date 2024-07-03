package app.dao.postgresDao;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.Booking;

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
            ResultSet resultSet = ps.executeQuery();
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
            ResultSet resultSet = ps.executeQuery();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return availableHallSlots;
    }

    @Override
    public String addNewHallBooking(String userLogin, String hallName, LocalDate date, LocalTime startTime, LocalTime endTime) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_HALL_BOOKING)) {
            ps.setString(1, userLogin);
            ps.setString(2, hallName);
            ps.setDate(3, Date.valueOf(date));
            ps.setTime(4, Time.valueOf(startTime));
            ps.setTime(5, Time.valueOf(endTime));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Booking added successfully";
    }

    @Override
    public String addNewDeskBooking(String userLogin, String roomName, int deskNumber, LocalDate date, LocalTime startTime, LocalTime endTime) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_DESK_BOOKING)) {
            ps.setString(1, userLogin);
            ps.setString(2, roomName);
            ps.setString(3, roomName);
            ps.setInt(4, deskNumber);
            ps.setDate(5, Date.valueOf(date));
            ps.setTime(6, Time.valueOf(startTime));
            ps.setTime(7, Time.valueOf(endTime));
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
    public Set<Booking> getAllBookingsForUser(String userLogin) {
        Set<Booking> userBookings = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_BOOKINGS)) {
            ps.setString(1, userLogin);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int bookingID = resultSet.getInt("booking_id");
                String placeName = resultSet.getString("place_name");
                int deskNumber = resultSet.getInt("desk_number");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                LocalTime startTime = resultSet.getTime("start_time").toLocalTime();
                LocalTime endTime = resultSet.getTime("end_time").toLocalTime();
                Booking booking = new Booking(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
                userBookings.add(booking);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userBookings;
    }

    @Override
    public Map<String, Set<Booking>> getAllBookingsAllUsers() {
        Map<String, Set<Booking>> allUserBookings = new HashMap<>();
        Set<String> allUserLogins = getUsersWhoHaveBookings();
        for (String user : allUserLogins) {
            allUserBookings.put(user, getAllBookingsForUser(user));
        }
        return allUserBookings;
    }

    @Override
    public String changeBookingTime(int bookingId, LocalTime startTime, LocalTime endTime) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.UPDATE_BOOKING_TIME)) {
            ps.setTime(1, Time.valueOf(startTime));
            ps.setTime(2, Time.valueOf(endTime));
            ps.setInt(3, bookingId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Changed successfully";
    }

    @Override
    public String changeBookingDate(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime) {
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
    public Booking getBookingById(int bookingId) {
        Booking booking = null;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_BOOKING_BY_ID)) {
            ps.setInt(1, bookingId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int bookingID = resultSet.getInt("booking_id");
                String userLogin = resultSet.getString("user_login");
                String placeName = resultSet.getString("place_name");
                int deskNumber = resultSet.getInt("desk_number");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                LocalTime startTime = resultSet.getTime("start_time").toLocalTime();
                LocalTime endTime = resultSet.getTime("end_time").toLocalTime();
                booking = new Booking(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return booking;
    }

    private Set<String> getUsersWhoHaveBookings() {
        Set<String> allLogins = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_LOGINS_WHO_BOOKED)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String userLogin = resultSet.getString("user_login");
                allLogins.add(userLogin);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allLogins;
    }
}
