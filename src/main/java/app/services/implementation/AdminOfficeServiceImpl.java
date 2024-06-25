package app.services.implementation;

import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.AdminOfficeService;
import app.services.AdminOperations;
import app.services.UserOperations;
import jdk.dynalink.linker.GuardingDynamicLinker;

public class AdminOfficeServiceImpl implements AdminOfficeService {
    private final Reader reader;
    private final UserOperations userOperations;
    private final AdminOperations adminOperations;

    public AdminOfficeServiceImpl(Reader reader, UserOperations userOperations, AdminOperations adminOperations) {
        this.reader = reader;
        this.userOperations = userOperations;
        this.adminOperations = adminOperations;
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!Thread.currentThread().isInterrupted() && !exit) {
            ConsolePrinter.print("""
                    Choose your action
                    Enter 1 if you want to see all bookings
                    Enter 2 if you want to see all bookings for user
                    Enter 3 if you want to delete desk
                    Enter 4 if you want to delete hall
                    Enter 5 if you want to add desk
                    Enter 6 if you want to add hall
                    Enter 7 if you want to add room
                    Enter 8 if you want to delete room
                    Enter 9 to exit
                    """);
            String userAnswer = reader.read();
            switch (userAnswer) {
                case ("1") -> adminOperations.viewAllBookings();
                case ("2") -> adminOperations.viewAllBookingsForUser();
                case ("3") -> adminOperations.deleteDesk();
                case ("4") -> adminOperations.deleteHall();
                case ("5") -> adminOperations.addDesk();
                case ("6") -> adminOperations.addHall();
                case ("7") -> adminOperations.addRoom();
                case ("8") -> adminOperations.deleteRoom();
                case ("9") -> exit = true;
                default -> ConsolePrinter.print("Wrong enter. Try again.");
            }
        }
        ConsolePrinter.print("exiting");
    }
}
