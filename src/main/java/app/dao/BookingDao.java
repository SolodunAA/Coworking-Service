package app.dao;

import app.dto.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public interface BookingDao {
    /**
     * showing all available slots for desks on this date
     * @param date selected date
     * @param roomName the name of room there checking available slots
     * @return map, key - is desk number, value - available slots
     */
    Map<Integer, Set<LocalTime>> getAvailableRoomDesksSlotsOnDate(LocalDate date, String roomName);
    /**
     * showing all available slots for halls on this date
     * @param date selected date
     * @return map, key - is hall name, value - available slots
     */
    Map<String, Set<LocalTime>> getAvailableHallsSlotsOnDate(LocalDate date);

    /**
     * add new hall booking
     * @param userLogin user's login
     * @param hallName hall name
     * @param date booking date
     * @param startTime booking start time
     * @param endTime booking end time
     * @return status
     */
    String addNewHallBooking(String userLogin, String hallName, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * add new desk booking
     * @param userLogin user's login
     * @param roomName room name
     * @param deskNumber desktop number
     * @param date booking date
     * @param startTime booking start time
     * @param endTime booking end time
     * @return status
     */
    String addNewDeskBooking(String userLogin, String roomName, int deskNumber, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * delete place booking
     * @param bookingId booking id to delete
     * @return status
     */
    String deleteBooking(int bookingId);

    /**
     * show all user bookings
     * @param userLogin login of user
     * @return set of user bookings
     */
    Set<Booking> getAllBookingsForUser(String userLogin);

    /**
     * show all bookings for all users
     * @return map: key is user's logins, value - set of their bookings
     */
    Map<String, Set<Booking>> getAllBookingsAllUsers();

    /**
     * change booking time
     * @param bookingId booking id for changing
     * @param startTime new start booking time
     * @param endTime new end booking time
     * @return status
     */
    String changeBookingTime(int bookingId, LocalTime startTime, LocalTime endTime);

    /**
     * change date time
     * @param bookingId id booking for changing
     * @param date new date
     * @param startTime new start time of booking
     * @param endTime new end time of booking
     * @return status
     */
    String changeBookingDate(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * get booking by id
     * @param bookingId id booking
     * @return Booking with this id
     */
    Booking getBookingById(int bookingId);

}
