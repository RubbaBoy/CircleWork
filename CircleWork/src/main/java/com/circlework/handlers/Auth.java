package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.DataSource;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;
import java.util.Objects;
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
            e.printStackTrace();
            setBody(exchange, Map.of("message", "error creating user"), 418);
        }




//        setBody(exchange, new LoginResponse("token-1", 2));
    }


    static final class LoginRequest {
        private final String username;
        private final String password;

        LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String username() {return username;}

        public String password() {return password;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LoginRequest) obj;
            return Objects.equals(this.username, that.username) &&
                    Objects.equals(this.password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, password);
        }

        @Override
        public String toString() {
            return "LoginRequest[" +
                    "username=" + username + ", " +
                    "password=" + password + ']';
        }
    }

    static final class LoginResponse {
        private final String token;
        private final int circle_id;

        LoginResponse(String token, int circle_id) {
            this.token = token;
            this.circle_id = circle_id;
        }

        public String token() {return token;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LoginResponse) obj;
            return Objects.equals(this.token, that.token) &&
                    this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(token, circle_id);
        }

        @Override
        public String toString() {
            return "LoginResponse[" +
                    "token=" + token + ", " +
                    "circle_id=" + circle_id + ']';
        }
    }

    static final class RegisterRequest {
        private final String username;
        private final String password;
        private final int circle_id;

        RegisterRequest(String username, String password, int circle_id) {
            this.username = username;
            this.password = password;
            this.circle_id = circle_id;
        }

        public String username() {return username;}

        public String password() {return password;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (RegisterRequest) obj;
            return Objects.equals(this.username, that.username) &&
                    Objects.equals(this.password, that.password) &&
                    this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, password, circle_id);
        }

        @Override
        public String toString() {
            return "RegisterRequest[" +
                    "username=" + username + ", " +
                    "password=" + password + ", " +
                    "circle_id=" + circle_id + ']';
        }
    }

    static final class RegisterResponse {
        private final String token;
        private final int circle_id;

        RegisterResponse(String token, int circle_id) {
            this.token = token;
            this.circle_id = circle_id;
        }

        public String token() {return token;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (RegisterResponse) obj;
            return Objects.equals(this.token, that.token) &&
                    this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(token, circle_id);
        }

        @Override
        public String toString() {
            return "RegisterResponse[" +
                    "token=" + token + ", " +
                    "circle_id=" + circle_id + ']';
        }
    }
}
