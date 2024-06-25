package app.services.implementation;

import app.dao.UserRoleDao;
import app.dto.Role;
import app.in.Reader;
import app.out.ConsolePrinter;
import app.services.AdminOfficeService;
import app.services.AuthenticationService;
import app.services.RegistrationService;
import app.services.UserOfficeService;

public class CoworkingService {

    private final AdminOfficeService adminOfficeService;
    private final UserOfficeService userOfficeService;
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final Reader reader;
    private final UserRoleDao userRoleDao;

    public CoworkingService(AdminOfficeService adminOfficeService,
                            UserOfficeService userOfficeService,
                            RegistrationService registrationService,
                            AuthenticationService authenticationService,
                            Reader reader,
                            UserRoleDao userRoleDao) {
        this.adminOfficeService = adminOfficeService;
        this.userOfficeService = userOfficeService;
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.reader = reader;
        this.userRoleDao = userRoleDao;
    }


    public void run() {
        ConsolePrinter.print("Hello! Welcome to our app!");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                runUserInteractions();
            } catch (Exception e) {
                ConsolePrinter.print("Exception happened. User logged out." + e.getMessage());
            }
        }
        ConsolePrinter.print("Shut down app");
    }

    private void runUserInteractions() {
        ConsolePrinter.print("""
                Print 1 if you want to register
                Print 2 if you want to login
                Print 3 if you want to stop server
                """);
        String input = reader.read();
        switch (input) {
            case "1" -> registrationService.register();
            case "2" -> {
                String login = authenticationService.auth();
                if (login != null) {
                    Role role = userRoleDao.getUserRole(login);
                    if (role == Role.ADMIN) {
                        adminOfficeService.run();
                    } else {
                        userOfficeService.run(login);
                    }

                } else {
                    ConsolePrinter.print("Authentication failed. Try again.");
                }
            }
            case "3" -> Thread.currentThread().interrupt();
            default -> ConsolePrinter.print("Error, try again");
        }
    }
}
