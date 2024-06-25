package app.dao;

import app.dto.Booking;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface BookingDao {
    /**
     * showing all available desks on this date
     * @param date selected date
     * @return map, key - is place id, value - available slots
     */
    Map<Integer, Set<Integer>> getAvailableSlotsOnDate(LocalDate date);
    /**
     * get all available desks on this date and time
     * @param date selected date
     * @param time selected time
     * @param period booking period
     * @return map, key - is place id, value - available slots
     */
    Map<Integer, Set<Integer>> getAvailableSlotsOnDateAndAtTimes(LocalDate date, int time, int period);

    /**
     * add new place booking
     * @param userLogin user's login
     * @param date booking date
     * @param time booking time
     * @param period booking period
     * @param placeId place id
     * @return status
     */
    String addNewBooking(String userLogin, LocalDate date, int time, int period, int placeId);

    /**
     * delete place booking
     * @param booking booking to delete
     * @return status
     */
    String deleteBooking(Booking booking);

    /**
     * show all user bookings
     * @param userLogin login of user
     * @return list of user bookings
     */
    Collection<Booking> showAllBookingsForUser(String userLogin);

    /**
     * show all bookings for all users
     * @return map: key is user's logins, value - set of their bookings
     */
    Map<String, Set<Booking>> getAllBookingsAllUsers();

    /**
     * update booking of hall
     * @param bookingToChange booking to change
     * @param newDate
     * @param newTime
     * @param newPeriod
     * @return status
     */
    String changeBooking(Booking bookingToChange, LocalDate newDate, int newTime, int newPeriod);

    /**
     * show all bookings for place
     * @param placeID place id
     * @return map, key is date, velue - set of available slots
     */
    Map<LocalDate, Set<Integer>> showAllBookingsForPlace(Integer placeID);


}
