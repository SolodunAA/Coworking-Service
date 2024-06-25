package app.services;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dao.inMemoryDao.InMemoryBookingDao;
import app.dao.inMemoryDao.InMemoryPlaceDao;
import app.in.ConsoleReader;
import app.services.implementation.AdminOperationsImpl;
import app.services.implementation.UserOperationsImpl;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class AdminOperationsImplTest {
    private final ConsoleReader crMock = Mockito.mock(ConsoleReader.class);
    @Test
    public void addNewRoomTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String roomName = "Red";
        Mockito.when(crMock.read()).thenReturn(roomName);
        adminOperations.addRoom();

        assertThat(placeDao.getAllRooms().contains(roomName)).isTrue();

    }
    @Test
    public void addNewDeskInRoomTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String roomName = "Red";
        Mockito.when(crMock.read()).thenReturn(roomName, roomName);
        adminOperations.addRoom();
        adminOperations.addDesk();

        assertThat(placeDao.getSetOfAllDesksInRoom(roomName).isEmpty()).isFalse();
    }

    @Test
    public void addNewHallTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String hallName = "GrandHall";
        Mockito.when(crMock.read()).thenReturn(hallName);
        adminOperations.addHall();

        assertThat(placeDao.getMapOfAllHalls().isEmpty()).isFalse();
    }
    @Test
    public void deleteRoomTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String roomName1 = "Red";
        String roomName2 = "Blue";
        Mockito.when(crMock.read()).thenReturn(roomName1, roomName2, roomName1);
        adminOperations.addRoom();
        adminOperations.addRoom();
        adminOperations.deleteRoom();

        assertThat(placeDao.getAllRooms().contains(roomName1)).isFalse();

    }

    @Test
    public void deleteDeskTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String roomName = "Red";
        String deskId = "1";
        Mockito.when(crMock.read()).thenReturn(roomName, roomName, roomName, deskId);
        adminOperations.addRoom();
        adminOperations.addDesk();
        adminOperations.addDesk();
        adminOperations.deleteDesk();

        assertThat(placeDao.getMapOfAllDesks().containsKey(Integer.parseInt(deskId))).isFalse();
    }
    @Test
    public void deleteHallTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);

        String hallName = "GrandHall";
        String hallName1 = "SmallHall";
        String hallIdForDeleting = "2";
        Mockito.when(crMock.read()).thenReturn(hallName, hallName1, hallIdForDeleting);
        adminOperations.addHall();
        adminOperations.addHall();
        adminOperations.deleteHall();

        assertThat(placeDao.getMapOfAllHalls().containsValue(hallName1)).isFalse();
    }
    @Test
    public void viewAllBookingsTest() {
        int openTime = 8;
        int closeTime = 22;
        PlaceDao placeDao = new InMemoryPlaceDao();
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, crMock, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, crMock, userOperations);
        String roomName = "Red";
        String date = "2024-06-23";
        String time = "10";
        String period = "5";
        String placeId = "1";
        String userLogin = "user";
        String sortingId = "1";

        Mockito.when(crMock.read()).thenReturn(date, time, period, placeId, sortingId);
        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        userOperations.bookDesk(userLogin);
        assertThatCode(adminOperations::viewAllBookings).doesNotThrowAnyException();
    }
}
