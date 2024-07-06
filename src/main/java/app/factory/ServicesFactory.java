package app.factory;

import app.auxiliaryfunctions.PasswordEncoder;
import app.services.*;
import app.services.implementation.*;

import java.time.LocalTime;

public class ServicesFactory {
    private final DaoFactory daoFactory;
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final UserOperations userOperations;
    private final AdminOperations adminOperations;

    public ServicesFactory(DaoFactory daoFactory, PasswordEncoder passwordEncoder, LocalTime openTime, LocalTime closeTime) {
        this.daoFactory = daoFactory;
        this.authenticationService = new AuthenticationServiceImpl(daoFactory.getLoginDao(), passwordEncoder);
        this.registrationService = new RegistrationServiceImpl(passwordEncoder, daoFactory.getLoginDao());
        this.userOperations = new UserOperationsImpl(daoFactory.getPlaceDao(), daoFactory.getBookingDao(), openTime, closeTime);
        this.adminOperations = new AdminOperationsImpl(daoFactory.getPlaceDao(), daoFactory.getBookingDao());
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }
    public UserOperations getUserOperations() {
        return userOperations;
    }
    public AdminOperations getAdminOperations() {
        return adminOperations;
    }
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }




}
