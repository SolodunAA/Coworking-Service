package app.services.implementation;

import app.annotation.Loggable;
import app.config.YmlReader;
import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.BookingDto;
import app.dto.OperationResult;
import app.services.UserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Loggable
@Service
public class UserOperationsImpl implements UserOperations {
    private final PlaceDao placeDao;
    private final BookingDao bookingDao;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    @Autowired
    public UserOperationsImpl(PlaceDao placeDao, BookingDao bookingDao, YmlReader ymlReader) {
        this.placeDao = placeDao;
        this.bookingDao = bookingDao;
        this.openTime = LocalTime.parse(ymlReader.getOpenTime());
        this.closeTime = LocalTime.parse(ymlReader.getCloseTime());
    }

    @Override
    public OperationResult bookDesk(BookingDto bookingDto) {
        if ((bookingDto.getStartTime().isBefore(openTime)
                || bookingDto.getStartTime().equals(openTime))
                || bookingDto.getStartTime().isAfter(closeTime)) {
            return new OperationResult("The booking start time does not coincide with working hours", 400);
        }

        if (bookingDto.getEndTime().isBefore(openTime)
                || (bookingDto.getEndTime().isAfter(closeTime)
                || bookingDto.getEndTime().equals(closeTime))) {
            return new OperationResult("The booking end time does not coincide with working hours", 400);
        }

        if (!placeDao.isPlaceExists(bookingDto.getPlaceName())) {
            return new OperationResult("This room doesn't exist", 404);
        }

        if (!placeDao.isDeskExistsInRoom(bookingDto.getPlaceName(), bookingDto.getDeskNumber())) {
            return new OperationResult("This desk doesn't exist", 404);
        }
        Set<LocalTime> availableSlotsForDesk = bookingDao.
                getAvailableRoomDesksSlotsOnDate(bookingDto.getDate(), bookingDto.getPlaceName()).get(bookingDto.getDeskNumber());

        LocalTime currentTime = bookingDto.getStartTime();
        while (currentTime.isBefore(bookingDto.getEndTime())) {
            if (!availableSlotsForDesk.contains(currentTime)) {
                return new OperationResult("Time slot already reserved", 400);
            }
            currentTime = currentTime.plusHours(1);
        }

        return new OperationResult(bookingDao.addNewDeskBooking(bookingDto), 200);
    }

    @Override
    public OperationResult bookHall(BookingDto bookingDto) {
        if ((bookingDto.getStartTime().isBefore(openTime)
                || bookingDto.getStartTime().equals(openTime))
                || bookingDto.getStartTime().isAfter(closeTime)) {
            return new OperationResult("The booking start time does not coincide with working hours", 400);
        }

        if (bookingDto.getEndTime().isBefore(openTime)
                || (bookingDto.getEndTime().isAfter(closeTime)
                || bookingDto.getEndTime().equals(closeTime))) {
            return new OperationResult("The booking end time does not coincide with working hours", 400);
        }
        if (!placeDao.isPlaceExists(bookingDto.getPlaceName())) {
            return new OperationResult("This hall doesn't exist", 404);
        }

        Set<LocalTime> availableSlotsForHall = bookingDao.
                getAvailableHallsSlotsOnDate(bookingDto.getDate()).get(bookingDto.getPlaceName());

        LocalTime currentTime = bookingDto.getStartTime();
        while (currentTime.isBefore(bookingDto.getEndTime())) {
            if (!availableSlotsForHall.contains(currentTime)) {
                return new OperationResult("Time slot already reserved", 400);
            }
            currentTime = currentTime.plusHours(1);
        }

        return new OperationResult(bookingDao.addNewHallBooking(bookingDto), 200);
    }


    @Override
    public OperationResult deleteBookings(String login, int bookingId) {
        if (!bookingDao.isUserHaveBookingWithId(login, bookingId)) {
            return new OperationResult("This booking doesn't exist", 404);
        }
        return new OperationResult(bookingDao.deleteBooking(bookingId), 200);
    }


    @Override
    public OperationResult changeBookingDateAndTime(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        BookingDto bookingDto = bookingDao.getBookingById(bookingId);
        String placeType;
        if (placeDao.getAllRooms().contains(bookingDto.getPlaceName())) {
            placeType = "room";
        } else {
            placeType = "hall";
        }
        if ((startTime.isBefore(openTime) || startTime.equals(openTime)) || startTime.isAfter(closeTime)) {
            return new OperationResult("The booking start time does not coincide with working hours", 400);
        }

        if (endTime.isBefore(openTime) || (endTime.isAfter(closeTime) || endTime.equals(closeTime))) {
            return new OperationResult("The booking end time does not coincide with working hours", 400);
        }
        if (placeType.equals("room")) {
            Set<LocalTime> availableSlotsForDesk = bookingDao.
                    getAvailableRoomDesksSlotsOnDate(date, bookingDto.getPlaceName()).get(bookingDto.getDeskNumber());

            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                if (!availableSlotsForDesk.contains(currentTime)) {
                    return new OperationResult("Time slot already reserved", 400);
                }
                currentTime = currentTime.plusHours(1);
            }

        } else {
            Set<LocalTime> availableSlotsForHall = bookingDao.
                    getAvailableHallsSlotsOnDate(date).get(bookingDto.getPlaceName());

            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                if (!availableSlotsForHall.contains(currentTime)) {
                    return new OperationResult("Time slot already reserved", 400);
                }
                currentTime = currentTime.plusHours(1);
            }

        }
        return new OperationResult(bookingDao.changeBookingDateAndTime(bookingId, date, startTime, endTime), 200);
    }


    @Override
    public List<BookingDto> getAllUserBooking(String login) {
        return bookingDao.getAllBookingsForUser(login);
    }

    @Override
    public Map<String, Set<Integer>> getAllRoomsAndDesks() {
        Set<String> allRooms = placeDao.getAllRooms();
        Map<String, Set<Integer>> roomsAndDesks = new HashMap<>();
        for (String room : allRooms) {
            roomsAndDesks.put(room, placeDao.getSetOfAllDesksInRoom(room));
        }
        return roomsAndDesks;
    }

    @Override
    public Set<String> getAllHalls() {
        return placeDao.getAllHalls();
    }

    @Override
    public Map<Integer, Set<LocalTime>> getAllAvailableDeskInRoomSlotsOnDate(LocalDate date, String roomName) {
        return bookingDao.getAvailableRoomDesksSlotsOnDate(date, roomName);
    }

    @Override
    public Map<String, Set<LocalTime>> getAllAvailableHallSlotsOnDate(LocalDate date) {
        return bookingDao.getAvailableHallsSlotsOnDate(date);
    }
}
