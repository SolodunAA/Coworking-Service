package app.services.implementation;

import app.annotation.Auditable;
import app.annotation.Loggable;
import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.BookingDto;
import app.dto.OperationResult;
import app.services.AdminOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Loggable
@Service
public class AdminOperationsImpl implements AdminOperations {
    private final PlaceDao placeDao;
    private final BookingDao bookingDao;
    @Autowired
    public AdminOperationsImpl(PlaceDao placeDao, BookingDao bookingDao) {
        this.placeDao = placeDao;
        this.bookingDao = bookingDao;
    }

    @Override
    public OperationResult addRoom(String roomName) {
        if (placeDao.isPlaceExists(roomName)) {
            return new OperationResult("Room already exists", 400);
        }
        return new OperationResult(placeDao.addNewPlace(roomName, "room"), 200);
    }

    @Override
    public OperationResult addHall(String hallName) {
        if (placeDao.isPlaceExists(hallName)) {
            return new OperationResult("Hall already exists", 400);
        }
        return new OperationResult(placeDao.addNewPlace(hallName, "hall"), 200);
    }

    @Override
    public OperationResult addDesk(String roomName) {
        if (!placeDao.isPlaceExists(roomName)) {
            return new OperationResult("This room doesn't exist", 404);
        }
        return new OperationResult(placeDao.addNewDesk(roomName), 200);
    }

    @Override
    public OperationResult deleteRoom(String roomName) {
        if (!placeDao.isPlaceExists(roomName)) {
            return new OperationResult("This room doesn't exist", 404);
        }
        return new OperationResult(placeDao.deletePlace(roomName), 200);
    }
    @Override
    public OperationResult deleteHall(String hallName) {
        if (!placeDao.isPlaceExists(hallName)) {
            return new OperationResult("This hall doesn't exist", 404);
        }
        return new OperationResult(placeDao.deletePlace(hallName), 200);
    }

    @Override
    public OperationResult deleteDesk(String roomName, int deskNumber) {
        if (!placeDao.isPlaceExists(roomName)) {
            return new OperationResult("This room doesn't exist", 404);
        }
        if (!placeDao.isDeskExistsInRoom(roomName, deskNumber)) {
            return new OperationResult("This desk doesn't exist", 404);
        }
        return new OperationResult(placeDao.deleteDesk(deskNumber, roomName), 200);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingDao.getAllBookingsAllUsers();
    }

    @Override
    public List<BookingDto> getAllBookingsForUser(String login) {
        return bookingDao.getAllBookingsForUser(login);
    }
}
