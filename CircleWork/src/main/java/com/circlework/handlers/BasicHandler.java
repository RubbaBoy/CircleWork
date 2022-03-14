package com.circlework.handlers;

import com.circlework.manager.AuthService;
import com.circlework.manager.CircleService;
import com.circlework.manager.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicHandler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicHandler.class);

    AuthService authService;
    CircleService circleService;
    UserService userService;

    @Inject
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Inject
    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    final Map<Path<?>, Handler> paths = new HashMap<>();

//    @Inject
//    BasicHandler(AuthService authService, CircleService circleService) {
//        this.authService = authService;
//        this.circleService = circleService;
//    }

    public HttpHandler init() {
        registerPaths();
        return this;
    }

    abstract void registerPaths();

    private static final Gson gsonBuilder = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    private static final Gson queryGsonBuilder = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        var result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                LOGGER.debug("before: {} after: {}", entry[1], URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
                result.put(entry[0], URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else if (entry.length == 1) {
                result.put(entry[0], "");
            }
        }

        return result;
    }

    /**
     * Handles a request, false if bad
     *
     * @param exchange
     * @return
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization,token");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            var method = exchange.getRequestMethod().toUpperCase();
            var path = exchange.getRequestURI().getPath().split("/");
            var strippedPath = new String[path.length - 3];
            System.arraycopy(path, 3, strippedPath, 0, path.length - 3); // strip /api/x
            System.out.println(Arrays.toString(strippedPath));

            var entryOptional = paths.entrySet().stream().filter(entry -> {
                var entryPath = entry.getKey();
//                System.out.println("requested method: " + method + " entry path: " + entryPath.method);
//                System.out.println(Arrays.toString(entryPath.path));
//                System.out.println(Arrays.toString(strippedPath));
//                System.out.println(entryPath.path.length);
//                System.out.println(strippedPath.length);
//                LOGGER.info("{} && {}", entryPath.method.equalsIgnoreCase(method), Arrays.equals(entryPath.path, strippedPath));
                return entryPath.method.equalsIgnoreCase(method) && Arrays.equals(entryPath.path, strippedPath);
            }).findFirst();

            if (entryOptional.isEmpty()) {
//                System.out.println("Empty!");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                setBody(exchange, Map.of("message", "ur bad"), 404);
                return;
            }

//            System.out.println("ok");

            try {
            var entry = entryOptional.get();

            String bodyString;


            if (method.equalsIgnoreCase("GET")) {
                var map = queryToMap(exchange.getRequestURI().getRawQuery());
//                LOGGER.debug("map = {}", map);
                bodyString = queryGsonBuilder.toJson(map);
            } else {
                bodyString = new String(exchange.getRequestBody().readAllBytes());
            }

//                LOGGER.debug("bodyString = {}", bodyString);

            var stuff = entry.getValue();
//            LOGGER.debug("111 {}", entry.getKey().clazz().getCanonicalName());
            var json = gsonBuilder.fromJson(bodyString, entry.getKey().clazz());
//                LOGGER.debug("222");


                var tokenHeader = exchange.getRequestHeaders().get("token");
//                LOGGER.debug("ayyy");
                var token = (tokenHeader != null && !tokenHeader.isEmpty()) ? tokenHeader.get(0) : null;

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                stuff.handle(exchange, path, method, json, token);
            } catch (Exception e) {
                e.printStackTrace();
                setBody(exchange, Map.of("error", e.getMessage()), 500);
            }

        } catch (Exception e) {
            // 500
            e.printStackTrace();
        }
    }

    /**
     * Adds a path. If this was bound to /api/auth, you would register "login" to read /api/auth/login
     *
     * @param path   The LOWERCASE path, with a length of >= 1
     * @param method The method in caps
     */
    <T> void registerPath(String[] path, String method, Class<T> bodyType, Handler<T> handler) {
        paths.put(new Path<>(path, method, bodyType), handler);
    }

    /**
     * turns the accepted data into json then populates response body and headerss
     *
     * @param exchange the HTTPExchange request and response
     * @param data     the data used to populate the body and headers
     * @throws IOException
     */
    public static void setBody(HttpExchange exchange, Object data) throws IOException {
        setBody(exchange, data, 200);
    }

    /**
     * turns the accepted data into json then populates response body and headerss
     *
     * @param exchange the HTTPExchange request and response
     * @param data     the data used to populate the body and headers
     * @param status   The status code
     * @throws IOException
     */
    public static void setBody(HttpExchange exchange, Object data, int status) throws IOException {
        var jsonData = gsonBuilder.toJson(data);
        var os = exchange.getResponseBody();
        exchange.sendResponseHeaders(status, jsonData.length());
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        os.write(jsonData.getBytes());
        os.close();
    }


    public interface Handler<T> {
        void handle(HttpExchange exchange, String[] path, String method, T body, String token) throws Exception;
    }

    record Path<T>(String[] path, String method, Class<T> clazz) {
    }
}
