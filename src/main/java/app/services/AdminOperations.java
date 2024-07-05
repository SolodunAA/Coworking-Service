package app.services;

import app.dto.BookingDto;
import app.dto.OperationResult;

import java.util.List;

/**
 * admin actions
 */
public interface AdminOperations {
    /**
     * add room
     * @return class OperationResult with status code and message
     */
    OperationResult addRoom(String roomName);
    /**
     * add hall
     * @return class OperationResult with status code and message
     */
    OperationResult addHall(String hallName);
    /**
     * add desk
     * @return class OperationResult with error code and message
     */
    OperationResult addDesk(String roomName);
    /**
     * delete the room
     * @param roomName room for deleting
     * @return class OperationResult with error code and message
     */
    OperationResult deleteRoom(String roomName);
    /**
     * delete hall
     * @param hallName hall for deleting
     * @return class OperationResult with error code and message
     */
    OperationResult deleteHall(String hallName);
    /**
     * delete desk
     * @param roomName name of room
     * @param deskNumber desk for deleting
     * @return class OperationResult with error code and message
     */
    OperationResult deleteDesk(String roomName, int deskNumber);

    /**
     * view all bookings in coworking
     * @return class OperationResult with error code and message
     */
    List<BookingDto> getAllBookings();

    /**
     * view all user's bookings for select user
     * @return user login
     * @return class OperationResult with error code and message
     */
    List<BookingDto> getAllBookingsForUser(String login);

}
