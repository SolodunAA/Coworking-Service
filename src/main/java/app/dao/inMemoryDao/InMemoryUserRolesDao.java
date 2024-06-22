package app.dao.inMemoryDao;

import app.dao.UserRoleDao;
import app.dto.Role;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRolesDao implements UserRoleDao {
    private final Map<String, Role> rolesCache = new HashMap<>();

    @Override
    public void addRoleForUser(String login, Role role) {
        rolesCache.put(login, role);
    }

    @Override
    public Role getUserRole(String login) {
        return rolesCache.get(login);
    }
}