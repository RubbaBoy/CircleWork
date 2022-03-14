package com.circlework.handlers;

import com.circlework.DataSource;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Circles extends BasicHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Circles.class);

    @Override
    public void registerPaths() {
//        registerPath(new String[]{"adduserbalance"}, "PUT", AddBalanceRequest.class, this::addUserBalance);
//        registerPath(new String[]{"addcirclebalance"}, "PUT", AddBalanceRequest.class, this::addCircleBalance);
        registerPath(new String[]{"leaderboard", "completion"}, "GET", Objects.Empty.class, this::completionLeaderboard);
        registerPath(new String[]{"leaderboard", "donation"}, "GET", Objects.Empty.class, this::donationLeaderboard);
        registerPath(new String[]{"users"}, "GET", UserRequest.class, this::users);
        registerPath(new String[]{"feed"}, "GET", Objects.Empty.class, this::feed);
        registerPath(new String[]{"donation"}, "GET", Objects.Empty.class, this::donation);
    }

    /*
        void addUserBalance(HttpExchange exchange, String[] path, String method, AddBalanceRequest request, String token) throws Exception {
            if (!authService.validateToken(token)) {
                setBody(exchange, Map.of("message", "invalid authtoken"), 418);
                return;
            }

            var userId = authService.getUserFromToken(token).orElseThrow();

            try {

                var balanceRow = SQLUtility.executeQuerySingle(
                        "UPDATE users SET approval_count=goals.approval_count+1 RETURNING approval_count");
            } catch (Exception e) {
                setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            }
        }

        void addCircleBalance(HttpExchange exchange, String[] path, String method, AddBalanceRequest request, String token) throws Exception {
            if (!authService.validateToken(token)) {
                setBody(exchange, Map.of("message", "invalid authtoken"), 418);
                return;
            }

            var userId = authService.getUserFromToken(token).orElseThrow();

            try {

                var circleRow = SQLUtility.executeQuerySingle("SELECT circle_id FROM users WHERE current"); // TODO: What is `current`??

                var balanceRow = SQLUtility.executeQuerySingle(
                        "UPDATE users SET approval_count=goals.approval_count+1 RETURNING approval_count");
            } catch (Exception e) {
                setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            }
        }
    */
    void completionLeaderboard(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws Exception {
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        List<Objects.Circle> circlesList = new LinkedList<>();
        var leaderBoardRow = SQLUtility.executeQuery("SELECT id, name, color, team_count, raised_monthly, raised_total," +
                " tasks_started, tasks_completed  from circles ORDER BY COALESCE(tasks_completed / NULLIF(tasks_started, 0), 0) LIMIT 5");

        for (Row curRow : leaderBoardRow) {
            circlesList.add(new Objects.Circle(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4),
                    curRow.get(5), curRow.get(6), curRow.get(7)));

        }

        setBody(exchange, circlesList, 200);
    }

    void donationLeaderboard(HttpExchange exchange, String[] path, String method, Objects.Empty request,
                             String token) throws Exception {

        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        List<Objects.Circle> circlesList = new LinkedList<>();
        var leaderBoardRow = SQLUtility.executeQuery("SELECT id, name, color, team_count, raised_monthly, raised_total," +
                " tasks_started, tasks_completed  from circles ORDER BY (raised_total) LIMIT 5");

        for (Row curRow : leaderBoardRow) {
            circlesList.add(new Objects.Circle(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4),
                    curRow.get(5), curRow.get(6), curRow.get(7)));

        }

        setBody(exchange, circlesList, 200);
    }


    void users(HttpExchange exchange, String[] path, String method, UserRequest request, String token) throws Exception {
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        setBody(exchange, getUsers(request.circle_id), 200);
    }

    void feed(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws Exception {
        // TODO: feed

        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var userSession = authService.getUserFromToken(token).orElseThrow();

        //var stmt = conn.prepareStatement("SELECT id, owner, private, category, goal_name, goal_body, approval_count  FROM goals WHERE circle_id = ?")){
        //.setString(1, ""+request.circle_id); //Turns circle id into a string to work with the statement

        var usersList = getUsers(userSession.circleId());

        List<Objects.Goal> goalList = new LinkedList<>();

        for (Objects.User userObject : usersList) {
            var feedRows = SQLUtility.executeQuery("SELECT id, private, category, goal_name, goal_body, due_date, approval_count FROM goals WHERE approval_count > 0 AND owner = ?", userObject.id());

            //var usernameRow = SQLUtility.executeQuerySingle("SELECT name FROM users WHERE id = ?");


            for (Row curRow : feedRows) {
                //note password is not retrieveda
                goalList.add(new Objects.Goal(curRow.get(0), userObject.id(), userObject.username(), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4), curRow.get(5), curRow.get(6)));
            }
        }

        goalList = goalList.parallelStream().filter(goal -> {
            try {
                return SQLUtility.executeQuerySingle("SELECT COUNT(*) FROM approved_goals WHERE user_id=? AND goal_id=?", userSession.id(), goal.id()).<Long>get(0) == 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }).toList();

        setBody(exchange, goalList, 200);


    }

    //gets total and monthly returns a body containing the exchange, a donationresponse, and a status code
    void donation(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws Exception {
        // TODO auth??

        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var userSession = authService.getUserFromToken(token).orElseThrow();

        try (var conn = DataSource.getConnection();
             var stmt = conn.prepareStatement("SELECT raised_total, raised_monthly FROM circles WHERE id = ?")) {
            stmt.setInt(1, userSession.circleId()); //Turns circle id into a string to work with the statement

            var query = stmt.executeQuery();
            if (query.next()) {
                var raised_total = query.getInt(1);
                var raised_monthly = query.getInt(2);

                setBody(exchange, new DonationResponse(raised_total, raised_monthly), 200);
                return;
            }
        }

        setBody(exchange, Map.of("message", "error"), 500);
    }

    public List<Objects.User> getUsers(int circle_id) throws Exception {
        List<Objects.User> usersList = new LinkedList<>();

        var userRows = SQLUtility.executeQuery("SELECT id, name, balance, circle_id FROM users WHERE circle_id=?", circle_id);
        for (Row curRow : userRows) {
            //note password is not retrieved
            usersList.add(new Objects.User(curRow.get(0), curRow.get(1), "", curRow.get(2), curRow.get(3)));
        }

        return usersList;
    }

    record DonationResponse(int total, int month) {}

    record UserRequest(int circle_id) {}
}
