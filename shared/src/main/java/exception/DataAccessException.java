package exception;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public Integer getCode() {
        return code;
    }

    public DataAccessException(String message, int code) {
        super(message);
        this.code = code;
    }
    public DataAccessException(String message, Throwable ex, int code) {
        super(message, ex);
        this.code = code;
    }

    public int getStatusCode() {
        return this.code;
    }

    public enum Code {
        ClientError,
        ServerError,
        AuthError,
    }

    final private Integer code;
}

