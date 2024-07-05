package app.services;

import app.dto.BookingDto;
import app.dto.OperationResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * class with operations for user
 */
public interface UserOperations {
    /**
     * book the desk
     * @param bookingDto booking dto
     * @return status
     * */
    OperationResult bookDesk(BookingDto bookingDto) ;
    /**
     * book the hall
     * @param bookingDto booking dto
     * @return status
     */
    OperationResult bookHall(BookingDto bookingDto);

    /**
     * delete bookings
     * @param login user login
     * @param bookingId id booking for deleting
     * @return status
     */
    OperationResult deleteBookings(String login, int bookingId);

    /**
     * view all my bookings
     * @param login user login
     * @return all user bookings
     */
    List<BookingDto> getAllUserBooking(String login);

    /**
     * view all rooms and desks in coworking
     */
    Map<String, Set<Integer>> getAllRoomsAndDesks();

    /**
     * view all halls in coworking
     */
    Set<String> getAllHalls();

    /**
     * all available desk slots on date
     * @param date date for checking
     * @param roomName name of room
     * @return map, key is number of desk, value - all available slots
     */
    Map<Integer, Set<LocalTime>> getAllAvailableDeskInRoomSlotsOnDate(LocalDate date, String roomName);
    /**
     * all available hall slots on date
     * @param date date for checking
     * @return map, key is hall name, value - all available slots
     */
    Map<String, Set<LocalTime>> getAllAvailableHallSlotsOnDate(LocalDate date);

    /**
     * change date and time
     * @param bookingId id booking for changing
     * @param date new date
     * @param startTime new start time of booking
     * @param endTime new end time of booking
     * @return status
     */
    OperationResult changeBookingDateAndTime(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime);
}
