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
import java.util.Map;
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
    public void viewAllBookings() {
        ConsolePrinter.print("Select sort by: 1-by date, 2-by user, 3-place id");
        int comparatorId = Integer.parseInt(reader.read());
        Comparator<Booking> bookingComparator;
        switch (comparatorId) {
            case (1) -> bookingComparator = Comparator.comparing(Booking::getDate);
            case (2) -> bookingComparator = Comparator.comparing(Booking::getUserLogin);
            case (3) -> bookingComparator = Comparator.comparing(Booking::getPlaceID);
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
        userOperations.viewAllMyBooking(adminAnswerLogin);
    }

    @Override
    public void deleteDesk() {
        String allDesksByRooms = placeDao.getMapOfAllDesks().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(Map.Entry::getKey).collect(Collectors.toList())
                ))
                .toString();
        ConsolePrinter.print("all desks by rooms: " + allDesksByRooms);
        ConsolePrinter.print("Choose deskId for deleting");
        String adminAnswerPlaceId = reader.read();
        ConsolePrinter.print(placeDao.deleteDesk(Integer.parseInt(adminAnswerPlaceId)));
    }

    @Override
    public void deleteHall() {
        String allHalls = placeDao.getMapOfAllHalls().entrySet().stream()
                .map(entry -> "hall name: " + entry.getValue() + ", id: " + entry.getKey())
                .collect(Collectors.joining("\n"));
        ConsolePrinter.print("all conference halls: " + allHalls);
        ConsolePrinter.print("Choose hall id for deleting");
        String adminAnswerPlaceId = reader.read();
        ConsolePrinter.print(placeDao.deleteHall(Integer.parseInt(adminAnswerPlaceId)));
    }

    @Override
    public void addDesk() {
        ConsolePrinter.print("There are following rooms: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room: allRooms){
            ConsolePrinter.print(room);
        }
        ConsolePrinter.print("Select the room where you want to add a desk");
        String adminAnswerRoom = reader.read();
        ConsolePrinter.print(placeDao.addNewDesk(adminAnswerRoom));
    }

    @Override
    public void addHall() {
        ConsolePrinter.print("Enter new hall name");
        String adminAnswerHallName = reader.read();
        ConsolePrinter.print(placeDao.addNewHall(adminAnswerHallName));
    }

    @Override
    public void addRoom() {
        ConsolePrinter.print("Enter new room name");
        String adminAnswerRoomName = reader.read();
        ConsolePrinter.print(placeDao.addNewRoom(adminAnswerRoomName));
    }

    @Override
    public void deleteRoom() {
        ConsolePrinter.print("There are following rooms: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room: allRooms){
            ConsolePrinter.print(room);
        }
        ConsolePrinter.print("Select room name for deleting");
        String adminAnswerRoomName = reader.read();
        ConsolePrinter.print(placeDao.deleteRoom(adminAnswerRoomName));
    }
}
