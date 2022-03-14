package com.circlework;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.Objects;

public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException(String message, IOException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    public UncheckedSQLException(SQLException cause) {
        super(Objects.requireNonNull(cause));
    }

    @Override
    public synchronized SQLException getCause() {
        return (SQLException) super.getCause();
    }
}
