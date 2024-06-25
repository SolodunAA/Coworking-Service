package app.dao;

import app.dao.inMemoryDao.InMemoryBookingDao;
import app.dao.inMemoryDao.InMemoryPlaceDao;
import app.dto.Booking;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryBookingDaoTest {
    @Test
    public void addNewBooking(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;
        Booking booking = new Booking(userLogin, placeId, date, time, period);

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        var resultMessage = bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        var expectedMessage = "Booking added";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var resultUserBookings = bookingDao.showAllBookingsForUser(userLogin);
        var expectedUserBookings = Set.of(booking);
        assertThat(resultUserBookings).containsOnlyOnceElementsOf(expectedUserBookings);

        var resultPlaceBookings = bookingDao.showAllBookingsForPlace(placeId);
        var expectedPlaceBookings = Map.of(date, Set.of(12, 13, 14));
        assertThat(resultPlaceBookings).containsAllEntriesOf(expectedPlaceBookings);
    }
    @Test
    public void addNewBookingWrongPlace(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 2;

        var resultMessage = bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        var expectedMessage = "Booking failed. Invalid place id " + placeId;
        assertThat(resultMessage).isEqualTo(expectedMessage);

    }
    @Test
    public void addNewBookingWrongOpenCloseTime() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 3;
        int period = 3;
        int placeId = 1;

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        var resultMessage = bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        var expectedMessage = "Booking failed. Booking time should be between " + openTime + " and " + closeTime;
        assertThat(resultMessage).isEqualTo(expectedMessage);
    }
    @Test
    public void addExistingBooking() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time, period, placeId);

        Set<Integer> bookingSlots = bookingDao.showAllBookingsForPlace(placeId).computeIfAbsent(date, ignored -> new HashSet<>());

        var resultMessage = bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        var expectedMessage = "Booking failed. There are booked slots conflicting with yours. Already booked " + bookingSlots;
        assertThat(resultMessage).isEqualTo(expectedMessage);
    }
    @Test
    public void getAvailableSlotsOnDate() {
        int openTime = 8;
        int closeTime = 10;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String hallName= "GrandHall";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time1 = 8;
        int period1 = 1;
        int placeId1 = 1;
        int time2 = 9;
        int period2 = 1;
        int placeId2 = 2;
        int time3 = 8;
        int period3 = 2;
        int placeId3 = 3;

        placeDao.addNewHall(hallName);
        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);
        placeDao.addNewDesk(nameRoom);

        var resultAvailableBookingSlots = bookingDao.getAvailableSlotsOnDate(date);
        var expectedAvailableBookingSlots = Map.of(1, Set.of(8, 9), 2, Set.of(8, 9), 3, Set.of(8,9));
        assertThat(resultAvailableBookingSlots).containsAllEntriesOf(expectedAvailableBookingSlots);

        bookingDao.addNewBooking(userLogin, date, time1, period1, placeId1);
        bookingDao.addNewBooking(userLogin, date, time2, period2, placeId2);
        bookingDao.addNewBooking(userLogin, date, time3, period3, placeId3);

        var resultAvailableBookingSlots1 = bookingDao.getAvailableSlotsOnDate(date);
        var expectedAvailableBookingSlots1 = Map.of(1, Set.of(9), 2, Set.of(8));
        assertThat(resultAvailableBookingSlots1).containsAllEntriesOf(expectedAvailableBookingSlots1);

    }

    @Test
    public void getAvailableSlotsOnDateAndTime() {
        int openTime = 8;
        int closeTime = 22;

        int startTimeBooking = 9;
        int periodTimeBooking = 5;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String hallName= "GrandHall";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time1 = 8;
        int period1 = 3;
        int placeId1 = 1;
        int time2 = 12;
        int period2 = 1;
        int placeId2 = 2;

        placeDao.addNewHall(hallName);
        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time1, period1, placeId1);
        bookingDao.addNewBooking(userLogin, date, time2, period2, placeId2);

        var resultAvailableBookingSlots1 = bookingDao.getAvailableSlotsOnDateAndAtTimes(date, startTimeBooking, periodTimeBooking);
        var expectedAvailableBookingSlots1 = Map.of(1, Set.of(11, 12, 13), 2, Set.of(9, 10, 11, 13));
        assertThat(resultAvailableBookingSlots1).containsAllEntriesOf(expectedAvailableBookingSlots1);
    }
    @Test
    public void deleteBooking() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;
        Booking booking = new Booking(userLogin, placeId, date, time, period);

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time, period, placeId);

        var resultMessage = bookingDao.deleteBooking(booking);
        var expectedMessage = "Deleted successfully";
        assertThat(resultMessage).isEqualTo(expectedMessage);
        assertThat(bookingDao.showAllBookingsForUser(userLogin).isEmpty()).isTrue();


        var resultEmptySlots = bookingDao.getAvailableSlotsOnDateAndAtTimes(date, time, period);
        var expectedEmptySlots = Map.of(1, Set.of(12, 13, 14));
        assertThat(resultEmptySlots).containsAllEntriesOf(expectedEmptySlots);
    }
    @Test
    public void showAllBookingsForUser() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;
        Booking booking = new Booking(userLogin, placeId, date, time, period);

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time, period, placeId);

        var resultUserBookings = bookingDao.showAllBookingsForUser(userLogin);
        var expectedUserBookings = Set.of(booking);
        assertThat(resultUserBookings).containsOnlyOnceElementsOf(expectedUserBookings);
    }
    @Test
    public void showAllBookingsForAllUsers() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;
        Booking booking = new Booking(userLogin, placeId, date, time, period);

        String userLogin1 = "user2";
        LocalDate date1 = LocalDate.parse("2024-06-20");
        int time1 = 17;
        int period1 = 2;
        int placeId1 = 1;
        Booking booking1 = new Booking(userLogin1, placeId1, date1, time1, period1);

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        bookingDao.addNewBooking(userLogin1, date1, time1, period1, placeId1);

        var resultBookings = bookingDao.getAllBookingsAllUsers();
        var expectedBookings = Map.of(userLogin, Set.of(booking), userLogin1, Set.of(booking1));
        assertThat(resultBookings).containsAllEntriesOf(expectedBookings);
    }
    @Test
    public void showAllBookingsForPlace() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

        String nameRoom = "Red";
        String userLogin = "user";
        LocalDate date = LocalDate.parse("2024-06-21");
        int time = 12;
        int period = 3;
        int placeId = 1;
        Booking booking = new Booking(userLogin, placeId, date, time, period);

        String userLogin1 = "user2";
        LocalDate date1 = LocalDate.parse("2024-06-20");
        int time1 = 17;
        int period1 = 2;
        int placeId1 = 1;
        Booking booking1 = new Booking(userLogin1, placeId1, date1, time1, period1);

        placeDao.addNewRoom(nameRoom);
        placeDao.addNewDesk(nameRoom);

        bookingDao.addNewBooking(userLogin, date, time, period, placeId);
        bookingDao.addNewBooking(userLogin1, date1, time1, period1, placeId1);

        var resultBookings = bookingDao.showAllBookingsForPlace(placeId);
        var expectedBookings = Map.of(date, Set.of(12, 13, 14), date1, Set.of(17, 18));
        assertThat(resultBookings).containsAllEntriesOf(expectedBookings);
    }
}
