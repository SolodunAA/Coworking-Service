package app.factory;

import app.auxiliaryfunctions.PasswordEncoder;
import app.in.Reader;
import app.services.*;
import app.services.implementation.*;

import java.time.LocalTime;

public class ServicesFactory {
    private final AdminOfficeService adminOfficeService;
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final UserOfficeService userOfficeService;

    public ServicesFactory(DaoFactory daoFactory, Reader reader, PasswordEncoder passwordEncoder, LocalTime openTime, LocalTime closeTime) {
        this.authenticationService = new AuthenticationServiceImpl(daoFactory.getLoginDao(), passwordEncoder, reader);
        this.registrationService = new RegistrationServiceImpl(passwordEncoder, daoFactory.getLoginDao(), reader);
        UserOperations userOperations = new UserOperationsImpl(daoFactory.getPlaceDao(), daoFactory.getBookingDao(), reader, openTime, closeTime);
        AdminOperations adminOperations = new AdminOperationsImpl(daoFactory.getPlaceDao(), daoFactory.getBookingDao(), reader, userOperations);
        this.userOfficeService = new UserOfficeServiceImpl(reader, userOperations);
        this.adminOfficeService = new AdminOfficeServiceImpl(reader, adminOperations);
    }

    public AdminOfficeService getAdminOfficeService() {
        return adminOfficeService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }

    public UserOfficeService getUserOfficeService() {
        return userOfficeService;
    }

}
