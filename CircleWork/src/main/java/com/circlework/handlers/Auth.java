package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.DataSource;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;
import java.util.UUID;

public class Auth extends BasicHandler {

    public Auth(AuthManager authManager, CircleManager circleManager){
        super(authManager, circleManager);
    }

    @Override
    public void registerPaths() {
        registerPath(new String[] {"login"}, "POST", LoginRequest.class, this::login);
        registerPath(new String[] {"register"}, "POST", RegisterRequest.class, this::register);
    }

    void login(HttpExchange exchange, String[] path, String method, LoginRequest request, String token) throws Exception {

            var conn = DataSource.getConnection();
            try(var stmt = conn.prepareStatement("SELECT circle_id WHERE username = ?, password = ?")){
                stmt.setString(1, request.username);
                stmt.setString(2,request.password);
                var query = stmt.executeQuery();
                if(query.next()){
                    var circleId = query.getInt(1);
                    var uuid = UUID.randomUUID().toString();
                    authManager.addToken(uuid, circleId);
                    setBody(exchange, new LoginResponse(uuid, circleId), 200);
                    return;
                }
            }
            setBody(exchange, Map.of("message", "invalid credentials"), 403);
    }

    void register(HttpExchange exchange, String[] path, String method, RegisterRequest request, String token) throws Exception {
        var conn = DataSource.getConnection();
        var circleId =request.circle_id;

        try(var stmt = conn.prepareStatement("INSERT into users (name, password, balance, circle_id) VALUES (?, ?, ?, ?)")){
            stmt.setString(1, request.username);
            stmt.setString(2, request.password);
            stmt.setInt(3, 0);

            if(circleId == -1){//if the user provides no preexisting circle
                circleId = circleManager.createCircle();//create a new circle for them
            }

            stmt.setInt(3, circleId);
            stmt.executeUpdate();
            var uuid = UUID.randomUUID().toString();
            authManager.addToken(uuid, circleId);//add the user to the authenticator

            setBody(exchange, new RegisterResponse(uuid, circleId), 200);
            return;

        }
        catch(Exception e){
            setBody(exchange, Map.of("message", "error creating user"), 418);
        }




//        setBody(exchange, new LoginResponse("token-1", 2));
    }




    record LoginRequest(String username, String password) {}

    record LoginResponse(String token, int circle_id) {}

    record RegisterRequest(String username, String password, int circle_id){}

    record RegisterResponse(String token, int circle_id){}
}
