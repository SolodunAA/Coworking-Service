package app.services.implementation;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.Booking;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.AdminOperations;
import app.services.UserOperations;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminOperationsImpl implements AdminOperations {
    private final PlaceDao placeDao;
    private final BookingDao bookingDao;
    private final Reader reader;
    private final UserOperations userOperations;

    public AdminOperationsImpl(PlaceDao placeDao, BookingDao bookingDao, Reader reader, UserOperations userOperations) {
        this.placeDao = placeDao;
        this.bookingDao = bookingDao;
        this.reader = reader;
        this.userOperations = userOperations;
    }

    @Override
    public void addRoom() {
        ConsolePrinter.print("Enter new room name");
        String adminAnswerRoomName = reader.read();
        Set<String> allRooms = placeDao.getAllRooms();
        if (allRooms.contains(adminAnswerRoomName)) {
            ConsolePrinter.print("This room already exists");
            return;
        }
        ConsolePrinter.print(placeDao.addNewPlace(adminAnswerRoomName, "room"));
    }

    @Override
    public void addHall() {
        ConsolePrinter.print("Enter new hall name");
        String adminAnswerHallName = reader.read();
        Set<String> allHalls = placeDao.getAllHalls();
        if (!allHalls.contains(adminAnswerHallName)) {
            ConsolePrinter.print("This hall already exists");
            return;
        }
        ConsolePrinter.print(placeDao.addNewPlace(adminAnswerHallName, "hall"));
    }

    @Override
    public void addDesk() {
        ConsolePrinter.print("There are following rooms: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room : allRooms) {
            ConsolePrinter.print(room);
        }
        ConsolePrinter.print("Select the room where you want to add a desk");
        String adminAnswerRoom = reader.read();
        if (!allRooms.contains(adminAnswerRoom)) {
            ConsolePrinter.print("This room doesn't exist");
            return;
        }
        ConsolePrinter.print(placeDao.addNewDesk(adminAnswerRoom));
    }

    @Override
    public void deleteRoom() {
        ConsolePrinter.print("There are following rooms: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room : allRooms) {
            ConsolePrinter.print(room);
        }
        ConsolePrinter.print("Select room name for deleting");
        String adminAnswerRoomName = reader.read();
        if (!allRooms.contains(adminAnswerRoomName)) {
            ConsolePrinter.print("This room doesn't exist");
            return;
        }
        ConsolePrinter.print(placeDao.deletePlace(adminAnswerRoomName));
    }

    @Override
    public void deleteDesk() {
        ConsolePrinter.print("There are following rooms: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room : allRooms) {
            ConsolePrinter.print(room);
        }
        ConsolePrinter.print("Select in which room you want to remove the desktop");
        String adminAnswerRoomName = reader.read();
        if (!allRooms.contains(adminAnswerRoomName)) {
            ConsolePrinter.print("This room doesn't exist");
            return;
        }

        ConsolePrinter.print("There are following desks: ");
        Set<Integer> allDesksInRoom = placeDao.getSetOfAllDesksInRoom(adminAnswerRoomName);
        for (int desk : allDesksInRoom) {
            ConsolePrinter.print("Desk № " + desk);
        }
        ConsolePrinter.print("Select the table number you want to delete");
        String adminAnswerDeskNumber = reader.read();
        if (!allDesksInRoom.contains(Integer.parseInt(adminAnswerDeskNumber))) {
            ConsolePrinter.print("This desk doesn't exist");
            return;
        }

        ConsolePrinter.print(placeDao.deleteDesk(Integer.parseInt(adminAnswerDeskNumber), adminAnswerRoomName));
    }

    @Override
    public void deleteHall() {
        ConsolePrinter.print("There are following hall: ");
        Set<String> allHalls = placeDao.getAllHalls();
        for (String hall : allHalls) {
            ConsolePrinter.print(hall);
        }
        ConsolePrinter.print("Select hall name for deleting");
        String adminAnswerHallName = reader.read();
        if (!allHalls.contains(adminAnswerHallName)) {
            ConsolePrinter.print("This hall doesn't exist");
            return;
        }
        ConsolePrinter.print(placeDao.deletePlace(adminAnswerHallName));
    }

    @Override
    public void viewAllBookings() {
        ConsolePrinter.print("Select sort by: 1-by date, 2-by user, 3-place name");
        int comparatorId = Integer.parseInt(reader.read());
        Comparator<Booking> bookingComparator;
        switch (comparatorId) {
            case (1) -> bookingComparator = Comparator.comparing(Booking::getDate);
            case (2) -> bookingComparator = Comparator.comparing(Booking::getUserLogin);
            case (3) -> bookingComparator = Comparator.comparing(Booking::getPlaceName);
            default -> {
                ConsolePrinter.print("invalid sorting id");
                return;
            }
        }
        var sortedBookings = bookingDao.getAllBookingsAllUsers().values().stream()
                .flatMap(Collection::stream)
                .sorted(bookingComparator)
                .collect(Collectors.toList());
        ConsolePrinter.print("sorted bookings: " + sortedBookings);
    }

    @Override
    public void viewAllBookingsForUser() {
        ConsolePrinter.print("Enter user login");
        String adminAnswerLogin = reader.read();
        userOperations.getAllUserBooking(adminAnswerLogin);
    }

}
