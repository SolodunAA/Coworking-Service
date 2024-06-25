package app.dao.inMemoryDao;

import app.dao.BookingDao;
import app.dao.PlaceDao;
import app.dto.Booking;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryBookingDao implements BookingDao {
    private final int openTime;
    private final int closeTime;
    private final Map<Integer, Map<LocalDate, Set<Integer>>> bookingStorage = new HashMap<>();
    private final Map<String, Set<Booking>> userBookingStorage = new HashMap<>();
    private final PlaceDao placeStorage;

    public InMemoryBookingDao(PlaceDao placeStorage, int openTime, int closeTime) {
        this.placeStorage = placeStorage;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    @Override
    public Map<Integer, Set<Integer>> getAvailableSlotsOnDate(LocalDate date) {
        return getAvailableSlotsWithFilter(date, openTime, closeTime);
    }

    @Override
    public Map<Integer, Set<Integer>> getAvailableSlotsOnDateAndAtTimes(LocalDate date, int time, int period) {
        return getAvailableSlotsWithFilter(date, time, time + period);
    }

    private Map<Integer, Set<Integer>> getAvailableSlotsWithFilter(LocalDate date,
                                                                   int startTimeInclusive,
                                                                   int endTimeExclusive) {
        Map<Integer, Set<Integer>> availablePlaceAndSlots = new HashMap<>();
        Set<Integer> validAvailableSlots = IntStream.range(startTimeInclusive, endTimeExclusive)
                .boxed()
                .collect(Collectors.toSet());
        Set<Integer> placeIds = placeStorage.getSetAllPlacesId();

        for (Integer placeId : placeIds) {
            Set<Integer> availableSlots = new HashSet<>(validAvailableSlots);
            Set<Integer> bookedSlots = bookingStorage
                    .getOrDefault(placeId, Map.of())
                    .getOrDefault(date, Set.of());
            availableSlots.removeAll(bookedSlots);
            if (!availableSlots.isEmpty()) {
                availablePlaceAndSlots.put(placeId, Set.copyOf(availableSlots));
            }
        }
        return Map.copyOf(availablePlaceAndSlots);
    }

    @Override
    public String addNewBooking(String userLogin, LocalDate date, int time, int period, int placeId) {
        if (!placeStorage.getSetAllPlacesId().contains(placeId)) {
            return "Booking failed. Invalid place id " + placeId;
        }
        int endTime = time + period;
        if (time < openTime || endTime > closeTime) {
            return "Booking failed. Booking time should be between " + openTime + " and " + closeTime;
        }
        Set<Integer> bookingSlots = bookingStorage.computeIfAbsent(placeId, ignored -> new HashMap<>())
                .computeIfAbsent(date, ignored -> new HashSet<>());
        boolean hasConflictWithOther = bookingSlots.stream()
                .anyMatch(slot -> slot >= time && slot < endTime);
        if (hasConflictWithOther) {
            return "Booking failed. There are booked slots conflicting with yours. Already booked " + bookingSlots;
        }
        for (int i = time; i < endTime; i++) {
            bookingSlots.add(i);
        }
        Booking booking = new Booking(userLogin, placeId, date, time, period);
        userBookingStorage.computeIfAbsent(userLogin, ignored -> new HashSet<>())
                .add(booking);
        return "Booking added";
    }

    @Override
    public String deleteBooking(Booking booking) {
        var bookingExisted = userBookingStorage.get(booking.getUserLogin()).remove(booking);
        if (!bookingExisted) {
            return "Failed to delete. Booking did not exist";
        }
        Map<LocalDate, Set<Integer>> placeBookings = bookingStorage.get(booking.getPlaceID());
        Set<Integer> placeBookingsOnDate = placeBookings.get(booking.getDate());
        placeBookingsOnDate.removeAll(booking.getBookedSlots());
        if (placeBookingsOnDate.isEmpty()) {
            placeBookings.remove(booking.getDate());
            if (placeBookings.isEmpty()) {
                bookingStorage.remove(booking.getPlaceID());
            }
        }
        return "Deleted successfully";
    }

    @Override
    public Collection<Booking> showAllBookingsForUser(String userLogin) {
        return List.copyOf(userBookingStorage.get(userLogin));
    }

    @Override
    public Map<String, Set<Booking>> getAllBookingsAllUsers() {
        return userBookingStorage;
    }

    @Override
    public String changeBooking(Booking bookingToChange, LocalDate newDate, int newTime, int newPeriod) {
        deleteBooking(bookingToChange);
        return addNewBooking(bookingToChange.getUserLogin(), newDate, newTime, newPeriod, bookingToChange.getPlaceID());
    }

    public Map<LocalDate, Set<Integer>> showAllBookingsForPlace(Integer placeID){
        return bookingStorage.get(placeID);
    }

}
