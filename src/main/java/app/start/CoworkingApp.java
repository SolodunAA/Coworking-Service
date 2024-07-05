package app.start;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.config.ConfigKeys;
import app.config.ConfigReader;
import app.factory.DaoFactory;
import app.factory.ServicesFactory;
import java.time.LocalTime;
import java.util.Properties;


public class CoworkingApp {

    public static final ServicesFactory SERVICES_FACTORY;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("can not create class for jdbc");
            throw new RuntimeException(e);
        }

        Properties configs = new ConfigReader().readProperties();

        String dbUrl = configs.getProperty(ConfigKeys.DB_URL);
        String dbUser = configs.getProperty(ConfigKeys.DB_USER);
        String dbPassword = configs.getProperty(ConfigKeys.DB_PASSWORD);
        String pathToChangeLog = configs.getProperty(ConfigKeys.PATH_CHANGELOG);
        InitialConfigProvider.runMigrations(dbUrl, dbUser ,dbPassword, pathToChangeLog);

        String openTime = configs.getProperty(ConfigKeys.OPEN_TIME);
        String closeTime = configs.getProperty(ConfigKeys.CLOSE_TIME);

        PasswordEncoder passwordEncoder = new HashEncoder();

        DaoFactory daoFactory = new DaoFactory(dbUrl, dbUser, dbPassword, LocalTime.parse(openTime), LocalTime.parse(closeTime));

        SERVICES_FACTORY = new ServicesFactory(daoFactory, passwordEncoder, LocalTime.parse(openTime), LocalTime.parse(closeTime));

    }

}
