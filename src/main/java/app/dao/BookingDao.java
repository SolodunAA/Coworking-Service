package app.dao;

import app.dto.BookingDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
     * @param bookingDto booking dto
     * @return status
     */
    String addNewHallBooking(BookingDto bookingDto);

    /**
     * add new desk booking
     * @param bookingDto booking dto
     * @return status
     */
    String addNewDeskBooking(BookingDto bookingDto);

    /**
     * delete place booking
     * @param bookingId booking id to delete
     * @return status
     */
    String deleteBooking(int bookingId);

    /**
     * show all user bookings
     * @param userLogin login of user
     * @return list of user bookings
     */
    List<BookingDto> getAllBookingsForUser(String userLogin);

    /**
     * show all bookings for all users
     * @return list of all bookings
     */
    List<BookingDto> getAllBookingsAllUsers();

    /**
     * change date time
     * @param bookingId id booking for changing
     * @param date new date
     * @param startTime new start time of booking
     * @param endTime new end time of booking
     * @return status
     */
    String changeBookingDateAndTime(int bookingId, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * get booking by id
     * @param bookingId id booking
     * @return Booking with this id
     */
    BookingDto getBookingById(int bookingId);
    /**
     * get booking by id
     * @param login user login
     * @param bookingId id booking
     * @return Booking with this id
     */
    boolean isUserHaveBookingWithId(String login, int bookingId);

}
