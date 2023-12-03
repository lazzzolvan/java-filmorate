package ru.yandex.practicum.filmorate.exception;

public class SqlQueryException extends RuntimeException {
    public SqlQueryException() {
    }

    public SqlQueryException(String message) {
        super(message);
    }

    public SqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlQueryException(Throwable cause) {
        super(cause);
    }

    public SqlQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
