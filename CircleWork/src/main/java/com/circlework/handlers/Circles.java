package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.DataSource;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            goalList = goalList.parallelStream().filter(goal -> {
                try {
                    return SQLUtility.executeQuerySingle("SELECT COUNT(*) FROM approved_goals WHERE user_id=? AND goal_id=?", userId, goal.id()).<Integer>get(0) == 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }).toList();

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

    public List<Objects.User> getUsers(int circle_id) throws Exception {
        List<Objects.User> usersList = new LinkedList<>();


        var userRows = SQLUtility.executeQuery("SELECT id, username, balance, circle_id FROM users WHERE circle_id=?", circle_id);
        for (Row curRow : userRows) {
            //note password is not retrieved
            usersList.add(new Objects.User(curRow.get(0), curRow.get(1), "pass", curRow.get(2), curRow.get(3)));
        }
        return usersList;
    }

    static final class DonationRequest {
        private final int circle_id;

        DonationRequest(int circle_id) {this.circle_id = circle_id;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DonationRequest) obj;
            return this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(circle_id);
        }

        @Override
        public String toString() {
            return "DonationRequest[" +
                    "circle_id=" + circle_id + ']';
        }
    }

    static final class DonationResponse {
        private final int raised_total;
        private final int raised_monthly;

        DonationResponse(int raised_total, int raised_monthly) {
            this.raised_total = raised_total;
            this.raised_monthly = raised_monthly;
        }

        public int raised_total() {return raised_total;}

        public int raised_monthly() {return raised_monthly;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DonationResponse) obj;
            return this.raised_total == that.raised_total &&
                    this.raised_monthly == that.raised_monthly;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(raised_total, raised_monthly);
        }

        @Override
        public String toString() {
            return "DonationResponse[" +
                    "raised_total=" + raised_total + ", " +
                    "raised_monthly=" + raised_monthly + ']';
        }
    }

    static final class LeaderboardRequest {
        LeaderboardRequest() {}

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj != null && obj.getClass() == this.getClass();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "LeaderboardRequest[]";
        }
    }

    static final class LeaderboardResponse {
        private final List<Objects.Circle> circles;

        LeaderboardResponse(List<Objects.Circle> circles) {this.circles = circles;}

        public List<Objects.Circle> circles() {return circles;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LeaderboardResponse) obj;
            return java.util.Objects.equals(this.circles, that.circles);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(circles);
        }

        @Override
        public String toString() {
            return "LeaderboardResponse[" +
                    "circles=" + circles + ']';
        }
    }

    static final class UserRequest {
        private final int circle_id;

        UserRequest(int circle_id) {this.circle_id = circle_id;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (UserRequest) obj;
            return this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(circle_id);
        }

        @Override
        public String toString() {
            return "UserRequest[" +
                    "circle_id=" + circle_id + ']';
        }
    }

    static final class UserResponse {
        private final List<Objects.User> user_list;

        UserResponse(List<Objects.User> user_list) {this.user_list = user_list;}

        public List<Objects.User> user_list() {return user_list;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (UserResponse) obj;
            return java.util.Objects.equals(this.user_list, that.user_list);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(user_list);
        }

        @Override
        public String toString() {
            return "UserResponse[" +
                    "user_list=" + user_list + ']';
        }
    }

    static final class FeedRequest {
        private final int circle_id;

        FeedRequest(int circle_id) {this.circle_id = circle_id;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FeedRequest) obj;
            return this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(circle_id);
        }

        @Override
        public String toString() {
            return "FeedRequest[" +
                    "circle_id=" + circle_id + ']';
        }
    }

    static final class FeedResponse {
        private final List<Objects.Goal> goal_list;

        FeedResponse(List<Objects.Goal> goal_list) {this.goal_list = goal_list;}

        public List<Objects.Goal> goal_list() {return goal_list;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FeedResponse) obj;
            return java.util.Objects.equals(this.goal_list, that.goal_list);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goal_list);
        }

        @Override
        public String toString() {
            return "FeedResponse[" +
                    "goal_list=" + goal_list + ']';
        }
    }

    static final class AddBalanceRequest {
        private final int cents;

        AddBalanceRequest(int cents) {this.cents = cents;}

        public int cents() {return cents;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (AddBalanceRequest) obj;
            return this.cents == that.cents;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(cents);
        }

        @Override
        public String toString() {
            return "AddBalanceRequest[" +
                    "cents=" + cents + ']';
        }
    }
}
