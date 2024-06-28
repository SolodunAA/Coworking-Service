package app.factory;

import app.dao.BookingDao;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dao.postgresDao.PostgresBookingDao;
import app.dao.postgresDao.PostgresLoginDao;
import app.dao.postgresDao.PostgresPlaceDao;

import java.time.LocalTime;

public class DaoFactory {
    private final BookingDao bookingDao;
    private final LoginDao loginDao;
    private final PlaceDao placeDao;

    public DaoFactory(String url, String user, String pswd, LocalTime openTime, LocalTime closeTime) {
        this.loginDao = new PostgresLoginDao(url, user, pswd);
        this.placeDao = new PostgresPlaceDao(url, user, pswd);
        this.bookingDao = new PostgresBookingDao(url, user, pswd, placeDao, openTime, closeTime);

    }

    public BookingDao getBookingDao() {
        return bookingDao;
    }

    public LoginDao getLoginDao() {
        return loginDao;
    }

    public PlaceDao getPlaceDao() {
        return placeDao;
    }

}
