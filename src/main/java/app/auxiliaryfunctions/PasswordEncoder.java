package app.auxiliaryfunctions;

public interface PasswordEncoder {
    /**
     *method encrypts the password
     * @param pswd - password that must be encrypted
     * @return encrypted password
     */
    int encode(String pswd);
}