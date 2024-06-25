package app.services.implementation;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.Booking;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.UserOperations;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserOperationsImpl implements UserOperations {
    private final PlaceDao placeDao;
    private final BookingDao bookingDao;
    private final Reader reader;
    private final int openTime;
    private final int closeTime;

    public UserOperationsImpl(PlaceDao placeDao, BookingDao bookingDao, Reader reader, int openTime, int closeTime) {
        this.placeDao = placeDao;
        this.bookingDao = bookingDao;
        this.reader = reader;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    @Override
    public void bookDesk(String login) {
        ConsolePrinter.print("What day would you like to book your desk for? Date format YYYY-MM-DD");
        String userAnswerDate = reader.read();
        ConsolePrinter.print("Enter start period of your booking. Example format for 13:00 is 13. Working hours 8:00 - 22:00");
        String userAnswerTime = reader.read();
        if(Integer.parseInt(userAnswerTime) < openTime && Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        ConsolePrinter.print("Enter period of your booking. Minimum period is 1 hour. Example format for 2 hour is 2");
        String userAnswerPeriod = reader.read();
        if(Integer.parseInt(userAnswerPeriod) + Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        Map<Integer, String> deskIdToRoomName = placeDao.getMapOfAllDesks();
        Map<Integer, Set<Integer>> availableDeskOnTime = bookingDao.getAvailableSlotsOnDateAndAtTimes(LocalDate.parse(userAnswerDate),
                Integer.parseInt(userAnswerTime),
                Integer.parseInt(userAnswerPeriod));
        ConsolePrinter.print("Select from below slots");
        availableDeskOnTime.forEach((deskId, slots) -> {
            String roomName = deskIdToRoomName.get(deskId);
            if (roomName != null) {
                ConsolePrinter.print("Room: " + roomName + " deskId: " + deskId + " - available slots:" +
                        slots.stream().sorted().collect(Collectors.toList()));
            }
        });
        ConsolePrinter.print("Select placeId");
        Integer userAnswerPlaceId = Integer.parseInt(reader.read());
        if (!deskIdToRoomName.containsKey(userAnswerPlaceId)) {
            ConsolePrinter.print("Invalid desk id");
            return;
        }
        String statusBooking = bookingDao.addNewBooking(login, LocalDate.parse(userAnswerDate),
                Integer.parseInt(userAnswerTime),
                Integer.parseInt(userAnswerPeriod),
                userAnswerPlaceId);
        ConsolePrinter.print(statusBooking);
    }

    @Override
    public void bookHall(String login) {
        ConsolePrinter.print("What day would you like to book your hall for? Date format is YYYY-MM-DD");
        String userAnswerDate = reader.read();
        ConsolePrinter.print("Enter start period of your booking. Example format for 13:00 is 13. Working hours 8:00 - 22:00");
        String userAnswerTime = reader.read();
        if(Integer.parseInt(userAnswerTime) < openTime && Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        ConsolePrinter.print("Enter period of your booking. Minimum period is 1 hour. Example format for 2 hour is 2");
        String userAnswerPeriod = reader.read();
        if(Integer.parseInt(userAnswerPeriod) + Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        Map<Integer, String> hallIdToName = placeDao.getMapOfAllHalls();
        Map<Integer, Set<Integer>> availableDeskOnTime = bookingDao.getAvailableSlotsOnDateAndAtTimes(LocalDate.parse(userAnswerDate),
                Integer.parseInt(userAnswerTime),
                Integer.parseInt(userAnswerPeriod));
        ConsolePrinter.print("Select from below slots");
        availableDeskOnTime.forEach((roomId, slots) -> {
            String hallName = hallIdToName.get(roomId);
            if (hallName != null) {
                ConsolePrinter.print(hallName + " hall (id: " + roomId + ") - available slots: " + slots.stream().sorted().collect(Collectors.toList()));
            }
        });
        ConsolePrinter.print("Select placeId");
        Integer userAnswerPlaceId = Integer.parseInt(reader.read());
        if (!hallIdToName.containsKey(userAnswerPlaceId)) {
            ConsolePrinter.print("Invalid hall id");
            return;
        }
        String statusBooking = bookingDao.addNewBooking(login, LocalDate.parse(userAnswerDate),
                Integer.parseInt(userAnswerTime),
                Integer.parseInt(userAnswerPeriod),
                userAnswerPlaceId);
        ConsolePrinter.print(statusBooking);
    }

    @Override
    public void viewAllMyBooking(String login) {
        ConsolePrinter.print("User " + login + " bookings:");
        List<Booking> userBookings = bookingDao.showAllBookingsForUser(login).stream()
                .sorted(Comparator.comparing(Booking::getDate))
                .collect(Collectors.toList());
        ConsolePrinter.print(userBookings.toString());
    }

    @Override
    public void viewAllPlaces() {
        String allHalls = placeDao.getMapOfAllHalls().entrySet().stream()
                .map(entry -> "hall name: " + entry.getValue() + ", id: " + entry.getKey())
                .collect(Collectors.joining("\n"));
        String allDesksByRooms = placeDao.getMapOfAllDesks().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(Map.Entry::getKey).collect(Collectors.toList())
                ))
                .toString();
        ConsolePrinter.print("all conference halls: " + allHalls);
        ConsolePrinter.print("all desks by rooms: " + allDesksByRooms);
    }

    @Override
    public void viewAllAvailableSlotsOnDate() {
        ConsolePrinter.print("What day would you like to see available places? Date format YYYY-MM-DD");
        String userAnswerDate = reader.read();
        ConsolePrinter.print("The following desks are available on this date");
        Map<Integer, Set<Integer>> availableDesksAndHallsOnDate = bookingDao.getAvailableSlotsOnDate(LocalDate.parse(userAnswerDate));
        Map<Integer, String> deskIdToRoomName = placeDao.getMapOfAllDesks();
        ConsolePrinter.print("ROOMS: ");
        availableDesksAndHallsOnDate.forEach((deskId, slots) -> {
            String roomName = deskIdToRoomName.get(deskId);
            if (roomName != null) {
                ConsolePrinter.print("Room: " + roomName + " deskId: " + deskId + " - available slots:" +
                        slots.stream().sorted().collect(Collectors.toList()));
            }
        });
        ConsolePrinter.print("The following hall are available on this date");
        Map<Integer, Set<Integer>> availableSlotsOnDate = bookingDao.getAvailableSlotsOnDate(LocalDate.parse(userAnswerDate));
        Map<Integer, String> hallIdToName = placeDao.getMapOfAllHalls();
        ConsolePrinter.print("HALLS: ");
        availableSlotsOnDate.forEach((roomId, slots) -> {
            String hallName = hallIdToName.get(roomId);
            if (hallName != null) {
                ConsolePrinter.print(hallName + " hall (id: " + roomId + ") - available slots: " + slots.stream().sorted().collect(Collectors.toList()));
            }
        });
    }

    @Override
    public void viewAllAvailableSlotsOnDateAndTime() {
        Map<Integer, String> hallIdToName = placeDao.getMapOfAllHalls();
        Map<Integer, String> deskIdToRoomName = placeDao.getMapOfAllDesks();
        ConsolePrinter.print("What day would you like to see available places? Date format YYYY-MM-DD");
        String userAnswerDate = reader.read();
        ConsolePrinter.print("Enter start period of your watching. Example format for 13:00 is 13. Working hours 8:00 - 22:00");
        String userAnswerTime = reader.read();
        if(Integer.parseInt(userAnswerTime) < openTime && Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        ConsolePrinter.print("Enter period of your booking. Minimum period is 1 hour. Example format for 2 hour is 2");
        String userAnswerPeriod = reader.read();
        if(Integer.parseInt(userAnswerPeriod) + Integer.parseInt(userAnswerTime) >= closeTime){
            ConsolePrinter.print("The time should be between working hours");
            return;
        }
        Map<Integer, Set<Integer>> availableDeskOnTime = bookingDao.getAvailableSlotsOnDateAndAtTimes(LocalDate.parse(userAnswerDate),
                Integer.parseInt(userAnswerTime),
                Integer.parseInt(userAnswerPeriod));
        ConsolePrinter.print("HALLS: ");
        availableDeskOnTime.forEach((roomId, slots) -> {
            String hallName = hallIdToName.get(roomId);
            if (hallName != null) {
                ConsolePrinter.print(hallName + " hall (id: " + roomId + ") - available slots: " + slots.stream().sorted().collect(Collectors.toList()));
            }
        });
        ConsolePrinter.print("ROOMS: ");
        availableDeskOnTime.forEach((deskId, slots) -> {
            String roomName = deskIdToRoomName.get(deskId);
            if (roomName != null) {
                ConsolePrinter.print("Room: " + roomName + " deskId: " + deskId + " - available slots:" +
                        slots.stream().sorted().collect(Collectors.toList()));
            }
        });
    }

    @Override
    public void deleteBookings(String login) {
        AtomicInteger bookingOrdinal = new AtomicInteger(0);
        Map<Integer, Booking> userBookings = bookingDao.showAllBookingsForUser(login).stream()
                .collect(Collectors.toMap(
                        ignored -> bookingOrdinal.incrementAndGet(),
                        Function.identity()
                ));
        ConsolePrinter.print("select booking from below map by id");
        ConsolePrinter.print(userBookings.toString());
        ConsolePrinter.print("booking id to delete");
        Integer bookingIdToDelete = Integer.parseInt(reader.read());
        Booking bookingToDelete = userBookings.get(bookingIdToDelete);
        if (bookingToDelete == null) {
            ConsolePrinter.print("invalid booking id inserted");
            return;
        }
        String status = bookingDao.deleteBooking(bookingToDelete);
        ConsolePrinter.print("deletion status: " + status);
    }

    @Override
    public void changeBooking(String login) {
        AtomicInteger bookingOrdinal = new AtomicInteger(0);
        Map<Integer, Booking> userBookings = bookingDao.showAllBookingsForUser(login).stream()
                .collect(Collectors.toMap(
                        ignored -> bookingOrdinal.incrementAndGet(),
                        Function.identity()
                ));
        ConsolePrinter.print("select booking from below map by id");
        ConsolePrinter.print(userBookings.toString());
        ConsolePrinter.print("booking id to change");
        Integer bookingIdToChange = Integer.parseInt(reader.read());
        Booking bookingToChange = userBookings.get(bookingIdToChange);
        if (bookingToChange == null) {
            ConsolePrinter.print("invalid booking id inserted");
            return;
        }
        ConsolePrinter.print("What day would you like to book your desk for? Date format YYYY-MM-DD");
        String userAnswerDate = reader.read();
        ConsolePrinter.print("Enter start period of your booking. Example format for 1 pm: 13");
        String userAnswerTime = reader.read();
        ConsolePrinter.print("Enter period of your booking. Minimum period is 1 hour. Example format for 2 hour: 2");
        String userAnswerPeriod = reader.read();
        String status = bookingDao.changeBooking(bookingToChange, LocalDate.parse(userAnswerDate), Integer.parseInt(userAnswerTime), Integer.parseInt(userAnswerPeriod));
        ConsolePrinter.print(" changing status: " + status);
    }
}
