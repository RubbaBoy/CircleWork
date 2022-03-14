package com.circlework.manager;

import java.util.Optional;

public interface AuthService {
    boolean validateToken(String token);

    Optional<Integer> getUserFromToken(String token);

    void addToken(String token, int userId);

    boolean removeToken(String token);
}
