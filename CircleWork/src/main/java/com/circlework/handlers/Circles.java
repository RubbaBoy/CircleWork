package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.DataSource;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Circles extends BasicHandler {

    public Circles(AuthManager authManager, CircleManager circleManager) {
        super(authManager, circleManager);
    }

    @Override
    public void registerPaths() {
        registerPath(new String[]{"adduserbalance"}, "PUT", AddBalanceRequest.class, this::addUserBalance);
        registerPath(new String[]{"addcirclebalance"}, "PUT", AddBalanceRequest.class, this::addCircleBalance);
        registerPath(new String[]{"leaderboard", "completion"}, "GET", LeaderboardRequest.class, this::completionLeaderboard);
        registerPath(new String[]{"leaderboard", "donation"}, "GET", LeaderboardRequest.class, this::donationLeaderboard);
        registerPath(new String[]{"users"}, "GET", UserRequest.class, this::users);
        registerPath(new String[]{"feed"}, "GET", FeedRequest.class, this::feed);
        registerPath(new String[]{"donation"}, "GET", DonationRequest.class, this::donation);
    }

    void addUserBalance(HttpExchange exchange, String[] path, String method, AddBalanceRequest request, String token) throws Exception {
        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var userId = authManager.getUserFromToken(token).orElseThrow();

        try {

            var balanceRow = SQLUtility.executeQuerySingle(
                    "UPDATE users SET approval_count=goals.approval_count+1 RETURNING approval_count");
            return;

        } catch (Exception e) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }


    }

    void addCircleBalance(HttpExchange exchange, String[] path, String method, AddBalanceRequest request, String token) throws Exception {
        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var userId = authManager.getUserFromToken(token).orElseThrow();

        try {

            var circleRow = SQLUtility.executeQuerySingle("SELECT circle_id FROM users WHERE current");

            var balanceRow = SQLUtility.executeQuerySingle(
                    "UPDATE users SET approval_count=goals.approval_count+1 RETURNING approval_count");
            return;

        } catch (Exception e) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }


    }

    void completionLeaderboard(HttpExchange exchange, String[] path, String method, LeaderboardRequest request, String token) throws Exception {
        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try {

            List<Objects.Circle> circlesList = new LinkedList<>();
            var leaderBoardRow = SQLUtility.executeQuery("SELECT id, name, color, team_count, raised_monthly, raised_total," +
                    " tasks_started, tasks_completed  from circles ORDER BY (tasks_completed / tasks_started) LIMIT 5");

            for (Row curRow : leaderBoardRow) {
                circlesList.add(new Objects.Circle(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4),
                        curRow.get(5), curRow.get(6), curRow.get(7)));

            }

            setBody(exchange, circlesList, 200);
        } catch (Exception e) {
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    void donationLeaderboard(HttpExchange exchange, String[] path, String method, LeaderboardRequest request,
                             String token) throws Exception {

        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try {

            List<Objects.Circle> circlesList = new LinkedList<>();
            var leaderBoardRow = SQLUtility.executeQuery("SELECT id, name, color, team_count, raised_monthly, raised_total," +
                    " tasks_started, tasks_completed  from circles ORDER BY (raised_total) LIMIT 5");

            for (Row curRow : leaderBoardRow) {
                circlesList.add(new Objects.Circle(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4),
                        curRow.get(5), curRow.get(6), curRow.get(7)));

            }

            setBody(exchange, circlesList, 200);
        } catch (Exception e) {
            setBody(exchange, Map.of("message", "error"), 500);
        }

    }


    void users(HttpExchange exchange, String[] path, String method, UserRequest request, String token) throws IOException {
        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try {
            // TODO max what the fuck this returns all users

            setBody(exchange, new UserResponse(getUsers(request.circle_id)), 200);

        } catch (Exception e) {
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    void feed(HttpExchange exchange, String[] path, String method, FeedRequest request, String token) throws Exception {
        // TODO: feed

        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var userId = authManager.getUserFromToken(token).orElseThrow();

        try {
            //var stmt = conn.prepareStatement("SELECT id, owner, private, category, goal_name, goal_body, approval_count  FROM goals WHERE circle_id = ?")){
            //.setString(1, ""+request.circle_id); //Turns circle id into a string to work with the statement

            var usersList = getUsers(request.circle_id);

            List<Objects.Goal> goalList = new LinkedList<>();

            for (Objects.User user : usersList) {

                var feedRows = SQLUtility.executeQuery("SELECT id, private, category, goal_name, goal_body, due_date, approval_count FROM goals WHERE approval_count > 0 AND owner = ?", user.id());

                //var usernameRow = SQLUtility.executeQuerySingle("SELECT name FROM users WHERE id = ?");


                for (Row curRow : feedRows) {
                    //note password is not retrieveda
                    goalList.add(new Objects.Goal(curRow.get(0), user.id(), user.username(), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4), curRow.get(5), curRow.get(6)));
                }
            }
            // make a request to uysers in the circle & count
            // filter goal list

            //    SELECT COUNT(*) FROM users WHERE circle_id = ?

            // TODO Evan: filter goals with user ID from `approved` table

            setBody(exchange, new FeedResponse(goalList), 200);

        } catch (Exception E) {
            setBody(exchange, Map.of("message", "error"), 500);
        }


    }

    //gets total and monthly returns a body containing the exchange, a donationresponse, and a status code
    void donation(HttpExchange exchange, String[] path, String method, DonationRequest request, String token) throws Exception {
        // TODO auth??

        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var conn = DataSource.getConnection();
        try (var stmt = conn.prepareStatement("SELECT raised_total, raised_monthly FROM circles WHERE circle_id = ?")) {
            stmt.setString(1, "" + request.circle_id); //Turns circle id into a string to work with the statement

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

    private List<Objects.User> getUsers(int circle_id) throws Exception {
        List<Objects.User> usersList = new LinkedList<>();


        var userRows = SQLUtility.executeQuery("SELECT id, username, balance, circle_id FROM users WHERE circle_id=?", circle_id);
        for (Row curRow : userRows) {
            //note password is not retrieved
            usersList.add(new Objects.User(curRow.get(0), curRow.get(1), "pass", curRow.get(2), curRow.get(3)));
        }
        return usersList;
    }

    record DonationRequest(int circle_id) {}

    record DonationResponse(int raised_total, int raised_monthly) {}

    record LeaderboardRequest() {}

    record LeaderboardResponse(List<Objects.Circle> circles) {}

    record UserRequest(int circle_id) {}

    record UserResponse(List<Objects.User> user_list) {}

    record FeedRequest(int circle_id) {}

    record FeedResponse(List<Objects.Goal> goal_list) {}

    record AddBalanceRequest(int cents) {}
}
