package app.services.implementation;

import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.UserOfficeService;
import app.services.UserOperations;

public class UserOfficeServiceImpl implements UserOfficeService {
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
            ConsolePrinter.print("Choose your action");
            ConsolePrinter.print("Enter 1 if you want to see all places in coworking");
            ConsolePrinter.print("Enter 2 if you want to see all available slots on date");
            ConsolePrinter.print("Enter 3 if you want to see all available slots on date and time");
            ConsolePrinter.print("Enter 4 if you want to book a desk");
            ConsolePrinter.print("Enter 5 if you want to book a hall");
            ConsolePrinter.print("Enter 6 if you want to see all your bookings");
            ConsolePrinter.print("Enter 7 if you want to change your bookings");
            ConsolePrinter.print("Enter 8 if you want to delete your bookings");
            ConsolePrinter.print("Enter 9 to exit");
            String userAnswer = reader.read();
            switch (userAnswer) {
                case ("1") -> userOperations.viewAllPlaces();
                case ("2") -> userOperations.viewAllAvailableSlotsOnDate();
                case ("3") -> userOperations.viewAllAvailableSlotsOnDateAndTime();
                case ("4") -> userOperations.bookDesk(login);
                case ("5") -> userOperations.bookHall(login);
                case ("6") -> userOperations.viewAllMyBooking(login);
                case ("7") -> userOperations.changeBooking(login);
                case ("8") -> userOperations.deleteBookings(login);
                case ("9") -> exit = true;
                default -> ConsolePrinter.print("Wrong enter. Try again.");
            }
        }
        ConsolePrinter.print("exiting");
    }
}
