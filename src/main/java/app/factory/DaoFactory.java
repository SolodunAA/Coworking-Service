package app.factory;

import app.dao.BookingDao;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dao.UserRoleDao;
import app.dao.inMemoryDao.InMemoryBookingDao;
import app.dao.inMemoryDao.InMemoryLoginDao;
import app.dao.inMemoryDao.InMemoryPlaceDao;
import app.dao.inMemoryDao.InMemoryUserRolesDao;

public class DaoFactory {
    private final BookingDao bookingDao;
    private final LoginDao loginDao;
    private final PlaceDao placeDao;
    private final UserRoleDao userRoleDao;

    public DaoFactory(Integer openTime, Integer closeTime) {
        this.loginDao = new InMemoryLoginDao();
        this.placeDao = new InMemoryPlaceDao();
        this.userRoleDao = new InMemoryUserRolesDao();
        this.bookingDao = new InMemoryBookingDao(placeDao, openTime, closeTime);

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

    public UserRoleDao getUserRoleDao() {
        return userRoleDao;
    }
}
