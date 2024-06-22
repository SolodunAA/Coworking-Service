package app.services;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dao.inMemoryDao.InMemoryBookingDao;
import app.dao.inMemoryDao.InMemoryPlaceDao;
import app.dto.Booking;
import app.in.ConsoleReader;
import app.services.implementation.UserOperationsImpl;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class UserOperationsImplTest {
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    @Test
    public void bookDeskTest(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";

        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId);
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        userOperations.bookDesk(userLogin);
        assertThat(bookingDao.getAllBookingsAllUsers().isEmpty()).isFalse();
    }
    @Test
    public void bookHallTest(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String hallName = "GrandHall";
        String hallName1 = "SmallHall";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";
        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId);
        placeDao.addNewHall(hallName1);
        placeDao.addNewHall(hallName);
        userOperations.bookHall(userLogin);
        assertThat(bookingDao.getAllBookingsAllUsers().isEmpty()).isFalse();
    }
    @Test
    public void deleteBookingsTest(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";
        String bookingId = "1";

        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId, bookingId);
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        userOperations.bookDesk(userLogin);
        userOperations.deleteBookings(userLogin);
        assertThat(bookingDao.showAllBookingsForUser(userLogin).isEmpty()).isTrue();
    }
    @Test
    public void changeBookingTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";
        String bookingId = "1";
        String date1 = "2024-06-23";
        String time1 = "10";
        String period1 = "5";
        Booking booking1 = new Booking(userLogin, Integer.parseInt(placeId), LocalDate.parse(date1),
                Integer.parseInt(time1), Integer.parseInt(period1));

        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId, bookingId,
                date1, time1, period1);
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        userOperations.bookDesk(userLogin);
        userOperations.changeBooking(userLogin);
        assertThat(bookingDao.showAllBookingsForUser(userLogin).contains(booking1)).isTrue();
    }
    @Test
    public void viewAllUserBooking() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";

        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId);
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        userOperations.bookDesk(userLogin);
        assertThatCode(() -> userOperations.viewAllMyBooking(userLogin)).doesNotThrowAnyException();
    }
    @Test
    public void viewAllPlacesTest(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String hallName = "GrandHall";
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewHall(hallName);
        assertThatCode(userOperations::viewAllPlaces).doesNotThrowAnyException();
    }
    @Test
    public void viewAllAvailableSlotsOnDateTest(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String hallName = "GrandHall";
        String date = "2024-06-21";
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewHall(hallName);
        Mockito.when(crMock.read()).thenReturn(date);
        assertThatCode(userOperations::viewAllAvailableSlotsOnDate).doesNotThrowAnyException();
    }
    @Test
    public void viewAllAvailableSlotsOnDateAndTime(){
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        String roomName = "Red";
        String hallName = "GrandHall";
        String date = "2024-06-21";
        String time = "10";
        String period = "3";
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewHall(hallName);
        Mockito.when(crMock.read()).thenReturn(date, time, period);
        assertThatCode(userOperations::viewAllAvailableSlotsOnDateAndTime).doesNotThrowAnyException();
    }
}
