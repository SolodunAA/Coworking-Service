package app.dao;

import app.dao.inMemoryDao.InMemoryUserRolesDao;
import app.dto.Role;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryUserRolesDaoTest {
    @Test
    public void addAndGetRoleForUserTest(){
        UserRoleDao userRolesDao = new InMemoryUserRolesDao();
        String user = "user";
        Role role = Role.USER;
        userRolesDao.addRoleForUser(user, role);

        assertThat(userRolesDao.getUserRole(user)).isEqualTo(role);
    }

    @Test
    public void getUnknownUserRole() {
        UserRoleDao userRolesDao = new InMemoryUserRolesDao();
        assertThat(userRolesDao.getUserRole("user")).isNull();
    }
}
