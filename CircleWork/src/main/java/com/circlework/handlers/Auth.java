package com.circlework.handlers;

import com.circlework.SQLUtility;
import com.circlework.manager.AuthService;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class Auth extends BasicHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auth.class);

    @Override
    public void registerPaths() {
        registerPath(new String[]{"login"}, "POST", LoginRequest.class, this::login);
        registerPath(new String[]{"register"}, "POST", RegisterRequest.class, this::register);
    }

    void login(HttpExchange exchange, String[] path, String method, LoginRequest request, String token) throws Exception {
        LOGGER.info("login request: {}", request);

        var userResult = SQLUtility.executeQuery("SELECT id, circle_id FROM users WHERE name = ? AND password = ?", request.username(), request.password());

        if (userResult.isEmpty()) {
            setBody(exchange, Map.of("message", "invalid credentials"), 403);
            return;
        }

        var row = userResult.get(0);
        int id = row.get(0);
        int circleId = row.get(1);
        var uuid = UUID.randomUUID().toString();
        authService.addToken(uuid, new AuthService.UserSession(id, circleId));
        setBody(exchange, new LoginResponse(uuid, circleId), 200);
    }

    void register(HttpExchange exchange, String[] path, String method, RegisterRequest request, String token) throws Exception {
        var circleId = request.circle_id();

        if (circleId == -1) {//if the user provides no preexisting circle
            circleId = circleService.createCircle();//create a new circle for them
        }

        var added = SQLUtility.executeQuerySingle("INSERT into users (name, password, balance, circle_id) VALUES (?, ?, ?, ?) RETURNING id", request.username(), request.password(), 0, circleId);
        var userId = added.<Integer>get(0);

        var uuid = UUID.randomUUID().toString();
        authService.addToken(uuid, new AuthService.UserSession(userId, circleId));//add the user to the authenticator

        setBody(exchange, new RegisterResponse(uuid, circleId), 200);
    }

    record LoginRequest(String username, String password) {}

    record LoginResponse(String token, int circle_id) {}

    record RegisterRequest(String username, String password, int circle_id) {}

    record RegisterResponse(String token, int circle_id) {}
}
