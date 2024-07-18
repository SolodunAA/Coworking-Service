package app.config;

import app.auxiliaryfunctions.HashEncoder;
import app.auxiliaryfunctions.PasswordEncoder;
import app.dao.AuditDao;
import app.dao.BookingDao;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dao.postgresDao.PostgresAuditDao;
import app.dao.postgresDao.PostgresBookingDao;
import app.dao.postgresDao.PostgresLoginDao;
import app.dao.postgresDao.PostgresPlaceDao;
import app.services.AdminOperations;
import app.services.AuthenticationService;
import app.services.RegistrationService;
import app.services.UserOperations;
import app.services.implementation.AdminOperationsImpl;
import app.services.implementation.AuthenticationServiceImpl;
import app.services.implementation.RegistrationServiceImpl;
import app.services.implementation.UserOperationsImpl;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    @Scope("singleton")
    public YmlReader ymlReader() {
        return new YmlReader();
    }

    @Bean
    @Scope("singleton")
    public LiquibaseConfig liquibaseConfig() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("can not create class for jdbc");
            throw new RuntimeException(e);
        }
        return new LiquibaseConfig(ymlReader());
    }

    @Bean
    public LoginDao loginDao() {
        return new PostgresLoginDao(ymlReader());
    }

    @Bean
    public AuditDao auditDao() {
        return new PostgresAuditDao(ymlReader());
    }

    @Bean
    public PlaceDao placeDao() {
        return new PostgresPlaceDao(ymlReader());
    }

    @Bean
    public BookingDao bookingDao() {
        return new PostgresBookingDao(ymlReader(), placeDao());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new HashEncoder();
    }

    @Bean
    public RegistrationService registrationService() {
        return new RegistrationServiceImpl(passwordEncoder(), loginDao());
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationServiceImpl(loginDao(), passwordEncoder());
    }

    @Bean
    public UserOperations userOperations() {
        return new UserOperationsImpl(placeDao(), bookingDao(), ymlReader());
    }

    @Bean
    public AdminOperations adminOperations() {
        return new AdminOperationsImpl(placeDao(), bookingDao());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() throws IOException {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        configurer.setProperties(yaml.getObject());
        return configurer;
    }

}

