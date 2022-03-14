package com.circlework.manager;

import com.circlework.Objects;
import com.circlework.SQLUtility;
import com.circlework.UncheckedSQLException;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Singleton
public class SQLUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLUserService.class);

    @Override
    public CompletableFuture<Objects.User> getUser(int userId) {
        LOGGER.debug("Getting user by id: {}", userId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                var row = SQLUtility.executeQuerySingle("SELECT (name, balance), circle_id FROM users WHERE id = ?", userId);
                return new Objects.User(userId, row.get(0), "", row.get(1), row.get(2));
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        });
    }

}
