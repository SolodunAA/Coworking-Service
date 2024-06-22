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
    void viewAllMyBooking(String login);

    /**
     * view all places in coworking
     */
    void viewAllPlaces();

    /**
     * view all available slots on date
     */
    void viewAllAvailableSlotsOnDate();

    /**
     * view all available slots on date and time
     */
    void viewAllAvailableSlotsOnDateAndTime();

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
