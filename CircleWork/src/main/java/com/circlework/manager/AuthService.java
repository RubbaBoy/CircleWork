package com.circlework.manager;

import java.util.Optional;

public interface AuthService {
    boolean validateToken(String token);

    Optional<UserSession> getUserFromToken(String token);

    void addToken(String token, UserSession session);

    boolean removeToken(String token);

    /**
     * Constant data for a user's session
     */
    record UserSession(int id, int circleId) {}
}
