package app.auxiliaryfunctions;

/**
 * class that is encoding password using simple hash function.
 */
public class HashEncoder implements PasswordEncoder {

    /**
     * @param pswd - password to encode
     * @return hash code of password as it's encoding
     */
    @Override
    public int encode(String pswd) {
        return pswd.hashCode();
    }
}