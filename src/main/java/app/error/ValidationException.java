package app.error;

public class ValidationException extends RuntimeException{
    public ValidationException(String msg) {
        super(msg);
    }
}
