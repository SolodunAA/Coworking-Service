package app.services;

/**
 * admin actions
 */
public interface AdminOperations {
    /**
     * view all bookings in coworking
     */
    void viewAllBookings();

    /**
     * view all user's bookings for select user
     */
    void viewAllBookingsForUser();

    /**
     * delete desk
     */
    void deleteDesk();

    /**
     * delete hall
     */
    void deleteHall();

    /**
     * add desk
     */
    void addDesk();

    /**
     * add hall
     */
    void addHall();

    /**
     * add room
     */
    void addRoom();

    /**
     * delete the room
     */
    void deleteRoom();
}
