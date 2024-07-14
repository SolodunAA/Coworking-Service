package app.dto;

public class OperationResult {
    private final String message;
    private final int errCode;

    public OperationResult(String message, int errCode) {
        this.message = message;
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public int getErrCode() {
        return errCode;
    }
}
