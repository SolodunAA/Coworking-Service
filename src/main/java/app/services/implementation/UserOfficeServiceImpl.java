package app.services.implementation;

import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.UserOfficeService;
import app.services.UserOperations;

public class UserOfficeServiceImpl implements UserOfficeService {
    public static final String USER_OPTIONS = """
            Choose your action
            Enter 1 if you want to see all places in coworking
            Enter 2 if you want to see all available slots on date
            Enter 3 if you want to book a desk
            Enter 4 if you want to book a hall
            Enter 5 if you want to see all your bookings
            Enter 6 if you want to change your bookings
            Enter 7 if you want to delete your bookings
            Enter 8 to exit
            """;
    private final Reader reader;
    private final UserOperations userOperations;

    public UserOfficeServiceImpl(Reader reader, UserOperations userOperations) {
        this.reader = reader;
        this.userOperations = userOperations;
    }

    @Override
    public void run(String login) {
        boolean exit = false;
        while (!Thread.currentThread().isInterrupted() && !exit) {
            ConsolePrinter.print(USER_OPTIONS);
            try {
                String userAnswer = reader.read();
                exit = runUserOperation(login, userAnswer);
            } catch (Exception e) {
                ConsolePrinter.print("Something went wrong, try again");
            }
        }
        ConsolePrinter.print("exiting");
    }

    /**
     * run user operations
     * @param login user login
     * @param userAnswer user choose
     * @return true if user want to exit
     */
    private boolean runUserOperation(String login, String userAnswer) {
        boolean exit = false;
        switch (userAnswer) {
            case ("1") -> userOperations.getAllPlaces();
            case ("2") -> userOperations.getAllAvailableSlotsOnDate();
            case ("3") -> userOperations.bookDesk(login);
            case ("4") -> userOperations.bookHall(login);
            case ("5") -> userOperations.getAllUserBooking(login);
            case ("6") -> userOperations.changeBooking(login);
            case ("7") -> userOperations.deleteBookings(login);
            case ("8") -> exit = true;
            default -> ConsolePrinter.print("Wrong enter. Try again.");
        }
        return exit;
    }
}
