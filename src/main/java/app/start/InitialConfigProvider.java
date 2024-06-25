package app.start;

import app.config.ConfigKeys;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dao.UserRoleDao;
import app.dto.Role;

import java.util.Properties;

public class InitialConfigProvider {

    public static void setInitialConfigs(Properties configs, LoginDao loginDao, UserRoleDao userRoleDao, PlaceDao placeDao) {
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
