package app.start;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigKeys;
import app.config.ConfigReader;
import app.factory.DaoFactory;
import app.factory.ServicesFactory;
import app.in.ConsoleReader;
import app.in.Reader;
import app.services.implementation.CoworkingService;

import java.time.LocalTime;
import java.util.Properties;


public class CoworkingApp {

    public static void main(String[] args) {
        runApplication();
    }
    public static void runApplication() {
        Properties configs = new ConfigReader().readProperties();

        String dbUrl = configs.getProperty(ConfigKeys.DB_URL);
        String dbUser = configs.getProperty(ConfigKeys.DB_USER);
        String dbPassword = configs.getProperty(ConfigKeys.DB_PASSWORD);
        InitialConfigProvider.runMigrations(dbUrl, dbUser ,dbPassword);

        String openTime = configs.getProperty(ConfigKeys.OPEN_TIME);
        String closeTime = configs.getProperty(ConfigKeys.CLOSE_TIME);

        Reader reader = new ConsoleReader();
        PasswordEncoder passwordEncoder = new HashEncoder();

        DaoFactory daoFactory = new DaoFactory(dbUrl, dbUser, dbPassword, LocalTime.parse(openTime), LocalTime.parse(closeTime));

        ServicesFactory servicesFactory = new ServicesFactory(daoFactory, reader, passwordEncoder, LocalTime.parse(openTime), LocalTime.parse(closeTime));

        CoworkingService coworkingService = new CoworkingService(
                servicesFactory.getAdminOfficeService(),
                servicesFactory.getUserOfficeService(),
                servicesFactory.getRegistrationService(),
                servicesFactory.getAuthenticationService(),
                reader,
                daoFactory.getLoginDao()
        );

        coworkingService.run();
    }


}
