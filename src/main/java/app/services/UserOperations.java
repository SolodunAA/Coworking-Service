package app.services;

/**
 * class with operations for user
 */
public interface UserOperations {
    /**
     * book the desk
     * @param login user login
     */
    void bookDesk(String login);

    /**
     * book the hall
     * @param login user login
     */
    void bookHall(String login);

    /**
     * view all my bookings
     * @param login user login
     */
    void getAllUserBooking(String login);

    /**
     * view all places in coworking
     */
    void getAllPlaces();

    /**
     * view all available slots on date
     */
    void getAllAvailableSlotsOnDate();


    /**
     * delete bookings
     * @param login user login
     */
    void deleteBookings(String login);

    /**
     * delete all user booking
     *@param login user login
     */
    void changeBooking(String login);
}
