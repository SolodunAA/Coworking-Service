package app.dao;

import app.dto.Role;

import java.util.Set;

/**
 * storing user's logins and passwords
 */
public interface LoginDao {
    /**
     * check has this user already registered in the system
     *
     * @param login user's login
     * @return if user exist in cache - true, else - false
     */
    boolean checkIfUserExist(String login);

    /**
     * add new user to the system
     *
     * @param login       registered user
     * @param encodedPswd encode password of this user
     */
    void addNewUser(String login, int encodedPswd);

    /**
     * get encoded password
     *
     * @param login - user login
     * @return encoded password
     */
    int getEncodedPassword(String login);

    /**
     * get user role
     *
     * @param login user login
     */
    Role getUserRole(String login);

}
