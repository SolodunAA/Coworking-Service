package app.start;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigKeys;
import app.config.ConfigReader;
import app.dao.BookingDao;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dao.UserRoleDao;
import app.dao.inMemoryDao.InMemoryBookingDao;
import app.dao.inMemoryDao.InMemoryLoginDao;
import app.dao.inMemoryDao.InMemoryPlaceDao;
import app.dao.inMemoryDao.InMemoryUserRolesDao;
import app.dto.Role;
import app.in.ConsoleReader;
import app.in.Reader;
import app.services.*;
import app.services.implementation.*;

import java.util.Properties;

public class CoworkingApp {

    public static void main(String[] args) {
        runApplication();
    }

    public static void runApplication() {
        Properties configs = new ConfigReader().readProperties();

        Reader reader = new ConsoleReader();
        UserRoleDao userRoleDao = new InMemoryUserRolesDao();
        PasswordEncoder passwordEncoder = new HashEncoder();
        LoginDao loginDao = new InMemoryLoginDao();
        AuthenticationService authenticationService = new AuthenticationServiceImpl(loginDao, passwordEncoder, reader);
        RegistrationService registrationService = new RegistrationServiceImpl(passwordEncoder, loginDao, reader);
        PlaceDao placeDao = new InMemoryPlaceDao();
        int openTime = Integer.parseInt(configs.getProperty(ConfigKeys.OPEN_TIME));
        int closeTime = Integer.parseInt(configs.getProperty(ConfigKeys.CLOSE_TIME));
        BookingDao bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);
        UserOperations userOperations = new UserOperationsImpl(placeDao, bookingDao, reader);
        AdminOperations adminOperations = new AdminOperationsImpl(placeDao, bookingDao, reader, userOperations);
        AdminOfficeService adminOfficeService = new AdminOfficeServiceImpl(reader, userOperations, adminOperations);
        UserOfficeService userOfficeService = new UserOfficeServiceImpl(reader, userOperations);
        CoworkingService coworkingService = new CoworkingService(
                adminOfficeService,
                userOfficeService,
                registrationService,
                authenticationService,
                reader,
                userRoleDao
        );

        setInitialConfigs(configs, loginDao, userRoleDao, placeDao);
        coworkingService.run();
    }

    private static void setInitialConfigs(Properties configs, LoginDao loginDao, UserRoleDao userRoleDao, PlaceDao placeDao) {
        String adminLogin = configs.getProperty(ConfigKeys.ADMIN_LOGIN);
        loginDao.addNewUser(
                adminLogin,
                Integer.parseInt(configs.getProperty(ConfigKeys.ADMIN_ENCRYPTED_PASSWORD))
        );
        userRoleDao.addRoleForUser(adminLogin, Role.ADMIN);

        for (String hall : configs.getProperty(ConfigKeys.DEFAULT_HALLS).split(",")) {
            placeDao.addNewHall(hall);
        }

        int desksInRoom = Integer.parseInt(configs.getProperty(ConfigKeys.DEFAULT_DESKS_NUM_IN_ROOM));
        for (String room : configs.getProperty(ConfigKeys.DEFAULT_ROOMS).split(",")) {
            placeDao.addNewRoom(room);
            for (int i = 0; i <= desksInRoom; i++) {
                placeDao.addNewDesk(room);
            }
        }
    }
}
