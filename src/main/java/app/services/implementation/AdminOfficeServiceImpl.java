package app.services.implementation;

import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.AdminOfficeService;
import app.services.AdminOperations;
import app.services.UserOperations;
import jdk.dynalink.linker.GuardingDynamicLinker;

public class AdminOfficeServiceImpl implements AdminOfficeService {
    private final Reader reader;
    private final AdminOperations adminOperations;
    public static final String ADMIN_OPTIONS = """
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
                    """;

    public AdminOfficeServiceImpl(Reader reader, AdminOperations adminOperations) {
        this.reader = reader;
        this.adminOperations = adminOperations;
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!Thread.currentThread().isInterrupted() && !exit) {
            ConsolePrinter.print(ADMIN_OPTIONS);
            try {
                String adminAnswer = reader.read();
                exit = runAdminOperation(adminAnswer);
            } catch (Exception e) {
                ConsolePrinter.print("Something went wrong, try again");
            }

        }
        ConsolePrinter.print("exiting");
    }
    /**
     * run admin operations
     * @param adminAnswer admin choose
     * @return true if admin want to exit
     */
    private boolean runAdminOperation(String adminAnswer) {
        boolean exit = false;
        switch (adminAnswer) {
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
        return exit;
    }
}
