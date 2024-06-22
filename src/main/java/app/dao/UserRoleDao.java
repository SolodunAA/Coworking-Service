package app.dao;

import app.dto.Role;

/**
 * users roles storage
 */
public interface UserRoleDao {
    /**
     * add user role to storage
     * @param login user login
     * @param role user role
     */
    void addRoleForUser(String login, Role role);

    /**
     * get user role
     * @param login user login
     * @return user role
     */
    Role getUserRole(String login);
}