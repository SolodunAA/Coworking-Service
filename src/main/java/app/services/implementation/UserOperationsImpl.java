package app.services.implementation;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.Booking;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.UserOperations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserOperationsImpl implements UserOperations {
    private final PlaceDao placeDao;
    private final BookingDao bookingDao;
    private final Reader reader;
    private final LocalTime openTime;
    private final LocalTime closeTime;

    public UserOperationsImpl(PlaceDao placeDao, BookingDao bookingDao, Reader reader, LocalTime openTime, LocalTime closeTime) {
        this.placeDao = placeDao;
        this.bookingDao = bookingDao;
        this.reader = reader;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    @Override
    public void bookDesk(String login) {
        ConsolePrinter.print("What day would you like to book your desk for? Date format YYYY-MM-DD");
        LocalDate userAnswerDate = LocalDate.parse(reader.read());

        printAvailableDeskSlots(userAnswerDate);

        ConsolePrinter.print("Enter start time of your booking. Example format: 09:00. Remember that working hours " + openTime + " - " + closeTime);
        LocalTime userAnswerStartTime = LocalTime.parse(reader.read());
        if ((userAnswerStartTime.isBefore(openTime) || userAnswerStartTime.equals(openTime)) && userAnswerStartTime.isAfter(closeTime)) {
            ConsolePrinter.print("The start time should be between working hours");
            return;
        }
        ConsolePrinter.print("Enter end time of your booking. Example format: 15:00");
        LocalTime userAnswerEndTime = LocalTime.parse(reader.read());
        if (userAnswerEndTime.isBefore(openTime) && (userAnswerEndTime.isAfter(closeTime) || userAnswerEndTime.equals(closeTime))) {
            ConsolePrinter.print("The end time should be between working hours");
            return;
        }
        ConsolePrinter.print("Select room");
        String userAnswerRoomName = reader.read();
        if (!placeDao.isPlaceExists(userAnswerRoomName)) {
            ConsolePrinter.print("There is not room with this name");
            return;
        }
        ConsolePrinter.print("Select desk №");
        int userAnswerDeskNumber = Integer.parseInt(reader.read());
        if (!placeDao.isDeskExistsInRoom(userAnswerRoomName, userAnswerDeskNumber)) {
            ConsolePrinter.print("There is not desk with this number in the room");
            return;
        }
        Set<LocalTime> availableSlotsForDesk = bookingDao.
                getAvailableRoomDesksSlotsOnDate(userAnswerDate, userAnswerRoomName).get(userAnswerDeskNumber);

        LocalTime currentTime = userAnswerStartTime;
        while (currentTime.isBefore(userAnswerEndTime)) {
            if (!availableSlotsForDesk.contains(currentTime)) {
                ConsolePrinter.print("This desk is already booked for time " + currentTime);
                return;
            }
            currentTime = currentTime.plusHours(1);
        }

        String statusBooking = bookingDao.addNewDeskBooking(
                login,
                userAnswerRoomName,
                userAnswerDeskNumber,
                userAnswerDate,
                userAnswerStartTime,
                userAnswerEndTime);
        ConsolePrinter.print(statusBooking);
    }

    @Override
    public void bookHall(String login) {
        ConsolePrinter.print("What day would you like to book your hall for? Date format YYYY-MM-DD");
        LocalDate userAnswerDate = LocalDate.parse(reader.read());
        printAvailableHallSlots(userAnswerDate);
        ConsolePrinter.print("Enter start time of your booking. Example format: 09:00. Remember that working hours " + openTime + " - " + closeTime);
        LocalTime userAnswerStartTime = LocalTime.parse(reader.read());
        if ((userAnswerStartTime.isBefore(openTime) || userAnswerStartTime.equals(openTime)) && userAnswerStartTime.isAfter(closeTime)) {
            ConsolePrinter.print("The start time should be between working hours");
            return;
        }
        ConsolePrinter.print("Enter end time of your booking. Example format: 15:00");
        LocalTime userAnswerEndTime = LocalTime.parse(reader.read());
        if (userAnswerEndTime.isBefore(openTime) && (userAnswerEndTime.isAfter(closeTime) || userAnswerEndTime.equals(closeTime))) {
            ConsolePrinter.print("The end time should be between working hours");
            return;
        }
        ConsolePrinter.print("Select hall");
        String userAnswerHallName = reader.read();
        if (!placeDao.isPlaceExists(userAnswerHallName)) {
            ConsolePrinter.print("There is not hall with this name");
            return;
        }

        Set<LocalTime> availableSlotsForHall = bookingDao.
                getAvailableHallsSlotsOnDate(userAnswerDate).get(userAnswerHallName);

        LocalTime currentTime = userAnswerStartTime;
        while (currentTime.isBefore(userAnswerEndTime)) {
            if (!availableSlotsForHall.contains(currentTime)) {
                ConsolePrinter.print("This hall is already booked for time " + currentTime);
                return;
            }
            currentTime = currentTime.plusHours(1);
        }

        String statusBooking = bookingDao.addNewHallBooking(
                login,
                userAnswerHallName,
                userAnswerDate,
                userAnswerStartTime,
                userAnswerEndTime);
        ConsolePrinter.print(statusBooking);
    }


    @Override
    public void deleteBookings(String login) {
        ConsolePrinter.print("Your bookings:");
        getAllUserBooking(login);
        ConsolePrinter.print("Please select the booking ID you want to delete");
        int userAnswerBookingId = Integer.parseInt(reader.read());
        ConsolePrinter.print(bookingDao.deleteBooking(userAnswerBookingId));
    }

    @Override
    public void changeBooking(String login) {

        ConsolePrinter.print("Your bookings:");
        getAllUserBooking(login);
        ConsolePrinter.print("You can change date or time of your bookings");
        ConsolePrinter.print("Please select the booking ID you want to change");
        int userAnswerBookingId = Integer.parseInt(reader.read());
        Booking booking = bookingDao.getBookingById(userAnswerBookingId);
        ConsolePrinter.print("Enter 1 if you want change the date and time");
        ConsolePrinter.print("Enter 2 if you want change the time");
        String userAnswerDateOrTime = reader.read();
        String placeType;
        if (placeDao.getAllRooms().contains(booking.getPlaceName())) {
            placeType = "room";
        } else {
            placeType = "hall";
        }
        switch (userAnswerDateOrTime) {
            case ("1") -> {
                ConsolePrinter.print("What day would you like move your booking? Date format YYYY-MM-DD");
                LocalDate userAnswerDate = LocalDate.parse(reader.read());

                printAvailableDeskSlots(userAnswerDate);

                ConsolePrinter.print("Enter new start time of your booking. Example format: 09:00. Remember that working hours " + openTime + " - " + closeTime);
                LocalTime userAnswerStartTime = LocalTime.parse(reader.read());
                if ((userAnswerStartTime.isBefore(openTime) || userAnswerStartTime.equals(openTime)) && userAnswerStartTime.isAfter(closeTime)) {
                    ConsolePrinter.print("The start time should be between working hours");
                    return;
                }
                ConsolePrinter.print("Enter new end time of your booking. Example format: 15:00");
                LocalTime userAnswerEndTime = LocalTime.parse(reader.read());
                if (userAnswerEndTime.isBefore(openTime) && (userAnswerEndTime.isAfter(closeTime) || userAnswerEndTime.equals(closeTime))) {
                    ConsolePrinter.print("The end time should be between working hours");
                    return;
                }
                if (placeType.equals("room")) {
                    Set<LocalTime> availableSlotsForDesk = bookingDao.
                            getAvailableRoomDesksSlotsOnDate(userAnswerDate, booking.getPlaceName()).get(booking.getDeskNumber());

                    LocalTime currentTime = userAnswerStartTime;
                    while (currentTime.isBefore(userAnswerEndTime)) {
                        if (!availableSlotsForDesk.contains(currentTime)) {
                            ConsolePrinter.print("This desk is already booked for time " + currentTime);
                            return;
                        }
                        currentTime = currentTime.plusHours(1);
                    }

                } else {
                    Set<LocalTime> availableSlotsForHall = bookingDao.
                            getAvailableHallsSlotsOnDate(userAnswerDate).get(booking.getPlaceName());

                    LocalTime currentTime = userAnswerStartTime;
                    while (currentTime.isBefore(userAnswerEndTime)) {
                        if (!availableSlotsForHall.contains(currentTime)) {
                            ConsolePrinter.print("This hall is already booked for time " + currentTime);
                            return;
                        }
                        currentTime = currentTime.plusHours(1);
                    }

                }
                ConsolePrinter.print(bookingDao.changeBookingDate(userAnswerBookingId, userAnswerDate, userAnswerStartTime, userAnswerEndTime));
            }
            case ("2") -> {
                ConsolePrinter.print("For this date available this slots: ");
                printAvailableAllSlots(booking.getDate());
                ConsolePrinter.print("Enter new start time of your booking. Example format: 09:00. Remember that working hours " + openTime + " - " + closeTime);
                LocalTime userAnswerStartTime = LocalTime.parse(reader.read());
                if ((userAnswerStartTime.isBefore(openTime) || userAnswerStartTime.equals(openTime)) && userAnswerStartTime.isAfter(closeTime)) {
                    ConsolePrinter.print("The start time should be between working hours");
                    return;
                }
                ConsolePrinter.print("Enter new end time of your booking. Example format: 15:00");
                LocalTime userAnswerEndTime = LocalTime.parse(reader.read());
                if (userAnswerEndTime.isBefore(openTime) && (userAnswerEndTime.isAfter(closeTime) || userAnswerEndTime.equals(closeTime))) {
                    ConsolePrinter.print("The end time should be between working hours");
                    return;
                }
                if (placeType.equals("room")) {
                    Set<LocalTime> availableSlotsForDesk = bookingDao
                            .getAvailableRoomDesksSlotsOnDate(booking.getDate(), booking.getPlaceName())
                            .get(booking.getDeskNumber());

                    LocalTime currentTime = userAnswerStartTime;
                    while (currentTime.isBefore(userAnswerEndTime)) {
                        if (!availableSlotsForDesk.contains(currentTime)) {
                            ConsolePrinter.print("This desk is already booked for time " + currentTime);
                            return;
                        }
                        currentTime = currentTime.plusHours(1);
                    }

                } else {
                    Set<LocalTime> availableSlotsForHall = bookingDao
                            .getAvailableHallsSlotsOnDate(booking.getDate())
                            .get(booking.getPlaceName());

                    LocalTime currentTime = userAnswerStartTime;
                    while (currentTime.isBefore(userAnswerEndTime)) {
                        if (!availableSlotsForHall.contains(currentTime)) {
                            ConsolePrinter.print("This hall is already booked for time " + currentTime);
                            return;
                        }
                        currentTime = currentTime.plusHours(1);
                    }

                }
                ConsolePrinter.print(bookingDao.changeBookingTime(userAnswerBookingId, userAnswerStartTime, userAnswerEndTime));
            }
            default -> ConsolePrinter.print("Wrong enter. Try again.");
        }
    }

    @Override
    public void getAllUserBooking(String login) {
        ConsolePrinter.print("User " + login + " bookings:");
        List<Booking> userBookings = bookingDao.getAllBookingsForUser(login).stream()
                .sorted(Comparator.comparing(Booking::getDate))
                .collect(Collectors.toList());
        for (Booking booking : userBookings) {
            ConsolePrinter.print(
                    "Booking Id: " + booking.getBookingID() +
                            " Date: " + booking.getDate() +
                            " Start time: " + booking.getStartTime() +
                            " End time: " + booking.getEndTime() +
                            " Place name: " + booking.getPlaceName() +
                            " Desk number: " + booking.getDeskNumber()
            );
        }
    }

    @Override
    public void getAllPlaces() {
        ConsolePrinter.print("There are following halls: ");
        Set<String> allHalls = placeDao.getAllHalls();
        for (String hall : allHalls) {
            ConsolePrinter.print(hall);
        }

        ConsolePrinter.print("There are following rooms and desks: ");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String room : allRooms) {
            ConsolePrinter.print(room + " room:");
            Set<Integer> allDesksInRoom = placeDao.getSetOfAllDesksInRoom(room);
            for (int desk : allDesksInRoom) {
                ConsolePrinter.print("Desk №" + desk);
            }
        }
    }

    @Override
    public void getAllAvailableSlotsOnDate() {
        ConsolePrinter.print("What day would you like to check available slots for? Date format YYYY-MM-DD");
        LocalDate userAnswerDate = LocalDate.parse(reader.read());
        printAvailableAllSlots(userAnswerDate);
    }

    private void printAvailableHallSlots(LocalDate date) {
        ConsolePrinter.print("The following hall slots are available for this date:");
        Set<String> allHalls = placeDao.getAllHalls();
        Map<String, Set<LocalTime>> availableHallSlots = bookingDao.getAvailableHallsSlotsOnDate(date);
        for (String hallName : allHalls) {
            ConsolePrinter.print(hallName + ":");
            ConsolePrinter.print("available slots: " + availableHallSlots.get(hallName).stream().sorted().collect(Collectors.toList()));
        }
    }

    private void printAvailableDeskSlots(LocalDate date) {
        ConsolePrinter.print("The following desks slots are available for this date:");
        Set<String> allRooms = placeDao.getAllRooms();
        for (String roomName : allRooms) {
            Map<Integer, Set<LocalTime>> availableDeskSlots = bookingDao.getAvailableRoomDesksSlotsOnDate(date, roomName);
            Set<Integer> desksInRoom = placeDao.getSetOfAllDesksInRoom(roomName);
            ConsolePrinter.print(roomName + " room:");
            for (Integer desk : desksInRoom) {
                ConsolePrinter.print("Desk №" + desk + ": available slots: " + availableDeskSlots.get(desk).stream().sorted().collect(Collectors.toList()));
            }
        }
    }

    private void printAvailableAllSlots(LocalDate date) {
        printAvailableHallSlots(date);
        printAvailableDeskSlots(date);
    }
}
