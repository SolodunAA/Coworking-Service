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

import java.util.Properties;


public class CoworkingApp {

    public static void main(String[] args) {
        runApplication();
    }
    public static void runApplication() {
        Properties configs = new ConfigReader().readProperties();
        int openTime = Integer.parseInt(configs.getProperty(ConfigKeys.OPEN_TIME));
        int closeTime = Integer.parseInt(configs.getProperty(ConfigKeys.CLOSE_TIME));
        Reader reader = new ConsoleReader();
        PasswordEncoder passwordEncoder = new HashEncoder();
        DaoFactory daoFactory = new DaoFactory(openTime , closeTime);
        ServicesFactory servicesFactory = new ServicesFactory(daoFactory, reader, passwordEncoder, openTime, closeTime);

        CoworkingService coworkingService = new CoworkingService(
                servicesFactory.getAdminOfficeService(),
                servicesFactory.getUserOfficeService(),
                servicesFactory.getRegistrationService(),
                servicesFactory.getAuthenticationService(),
                reader,
                daoFactory.getUserRoleDao()
        );

        InitialConfigProvider.setInitialConfigs(
                configs,
                daoFactory.getLoginDao(),
                daoFactory.getUserRoleDao(),
                daoFactory.getPlaceDao()
        );

        coworkingService.run();
    }

}
