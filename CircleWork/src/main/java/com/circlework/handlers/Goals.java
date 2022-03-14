package com.circlework.handlers;

import com.circlework.manager.AuthService;
import com.circlework.manager.CircleService;
import com.circlework.DataSource;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Goals extends BasicHandler {

    @Override
    public void registerPaths() {
        registerPath(new String[] {"add"}, "POST", AddRequest.class, this::add);
        registerPath(new String[] {"id"}, "GET", GetRequest.class, this::get);
        registerPath(new String[] {"approve"}, "POST", ApproveRequest.class, this::approve);
        registerPath(new String[] {"list"}, "GET", Objects.Empty.class, this::list);
    }

    void add(HttpExchange exchange, String[] path, String method, AddRequest request, String token) throws Exception {
//        var userIdOptional = authService.validateToken(token);

        if(!authService.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var user = authService.getUserFromToken(token).orElseThrow();

        try{
            //add the goal
            var idRow = SQLUtility.executeQuerySingle("INSERT into goals(owner, private, category, goal_name," +
                            " goal_body, approval_count) VALUES(?, ? , ?, ?, ?, ?) RETURNING id", user,
                    request.is_private, request.category, request.goal_name, request.goal_body, 1);
            int goalId = idRow.get(0);

            //increment user's tasks started
            SQLUtility.executeQuery("UPDATE users SET tasks_started = tasks_started + 1 WHERE id=?", user);

            setBody(exchange, new AddResponse(goalId), 200);
        }catch (Exception e){
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    void get(HttpExchange exchange, String[] path, String method, GetRequest body, String token) throws Exception{
        if(!authService.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try{
            var goalRow = SQLUtility.executeQuerySingle("SELECT id, owner, private, goal_name, goal_body, " +
                    "approval_count, category, due_date FROM goals WHERE id=?", body.goal_id);

            int id = goalRow.get(0);
            int ownerId = goalRow.get(1);
            boolean isPrivate = goalRow.get(2);
            String goalName = goalRow.get(3);
            String goalBody = goalRow.get(4);
            int approvalCount = goalRow.get(5);
            int category = goalRow.get(6);
            Timestamp due_date = goalRow.get(7);

            var userRow = SQLUtility.executeQuerySingle("SELECT name FROM users WHERE id=?", ownerId);

            setBody(exchange, new GetResponse(new Objects.Goal(id, ownerId, userRow.<String>get(0), isPrivate, category, goalName, goalBody, due_date, approvalCount)), 200);
        }catch (SQLException e){
            setBody(exchange, Map.of("message", "error"), 500);
        }

    }

    void approve(HttpExchange exchange, String[] path, String method, ApproveRequest body, String token) throws Exception{
        if(!authService.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var userId = authService.getUserFromToken(token).orElseThrow();

        try {

            var checkRow = SQLUtility.executeQuery("SELECT user_id FROM approved_goals WHERE user_id=? AND goal_id=?", userId, body.goal_id);

            if(!checkRow.isEmpty()){
                setBody(exchange, Map.of("message", "No Cheating"), 418);
                return;
            }

            //If reached this point, this approval hasn't been made before. Register approval with approved_goals table
            //And then increment approval

            //registering
            var conn = DataSource.getConnection();

            var stmt = conn.prepareStatement("INSERT INTO approved_goals (user_id, goal_id) VALUES (?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, body.goal_id);

            stmt.executeUpdate();

            var approvedRow = SQLUtility.executeQuerySingle(
                    "UPDATE goals set approval_count=goals.approval_count+1 RETURNING approval_count WHERE id=?", body.goal_id);

            int approvalCount = approvedRow.get(0);
            setBody(exchange, new ApproveResponse(approvalCount), 200);

            // TODO: Evan: insert value into `approved_goals` table\
            // also check if they have approved aslrteady

        } catch (Exception e){
            e.printStackTrace();
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    /*
       if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var userId = authService.getUserFromToken(token).orElseThrow();
     */

    void list(HttpExchange exchange, String[] path, String method, Objects.Empty empty, String token ) throws Exception{
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        List<Objects.Goal> goalList = new LinkedList<>();

        var userId = authService.getUserFromToken(token).orElseThrow();

        try {
            var goalRow = SQLUtility.executeQuery("SELECT id, owner, private, category, goal_name, goal_body, " +
                    "due_date, approval_count FROM goals WHERE owner=?", userId);

            var userRow = SQLUtility.executeQuerySingle("SELECT name FROM users WHERE id=?", userId);

            for (Row curRow : goalRow) {
                goalList.add(new Objects.Goal(curRow.get(0), curRow.get(1), userRow.get(0), curRow.get(2), curRow.get(3), curRow.get(4), curRow.get(5), curRow.get(6), curRow.get(7)));
            }

            setBody(exchange, new ListResponse(goalList), 200);
        }
        catch (Exception e){
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    static final class AddRequest {
        private final boolean is_private;
        private final String goal_name;
        private final String goal_body;
        private final int category;

        AddRequest(boolean is_private, String goal_name, String goal_body, int category) {
            this.is_private = is_private;
            this.goal_name = goal_name;
            this.goal_body = goal_body;
            this.category = category;
        }

        public boolean is_private() {return is_private;}

        public String goal_name() {return goal_name;}

        public String goal_body() {return goal_body;}

        public int category() {return category;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (AddRequest) obj;
            return this.is_private == that.is_private &&
                    java.util.Objects.equals(this.goal_name, that.goal_name) &&
                    java.util.Objects.equals(this.goal_body, that.goal_body) &&
                    this.category == that.category;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(is_private, goal_name, goal_body, category);
        }

        @Override
        public String toString() {
            return "AddRequest[" +
                    "is_private=" + is_private + ", " +
                    "goal_name=" + goal_name + ", " +
                    "goal_body=" + goal_body + ", " +
                    "category=" + category + ']';
        }
    }

    static final class AddResponse {
        private final int goal_id;

        AddResponse(int goal_id) {this.goal_id = goal_id;}

        public int goal_id() {return goal_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (AddResponse) obj;
            return this.goal_id == that.goal_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goal_id);
        }

        @Override
        public String toString() {
            return "AddResponse[" +
                    "goal_id=" + goal_id + ']';
        }
    }

    static final class GetRequest {
        private final int goal_id;

        GetRequest(int goal_id) {this.goal_id = goal_id;}

        public int goal_id() {return goal_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (GetRequest) obj;
            return this.goal_id == that.goal_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goal_id);
        }

        @Override
        public String toString() {
            return "GetRequest[" +
                    "goal_id=" + goal_id + ']';
        }
    }

    static final class GetResponse {
        private final Objects.Goal goal;

        GetResponse(Objects.Goal goal) {this.goal = goal;}

        public Objects.Goal goal() {return goal;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (GetResponse) obj;
            return java.util.Objects.equals(this.goal, that.goal);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goal);
        }

        @Override
        public String toString() {
            return "GetResponse[" +
                    "goal=" + goal + ']';
        }
    }

    static final class ApproveRequest {
        private final int goal_id;

        ApproveRequest(int goal_id) {this.goal_id = goal_id;}

        public int goal_id() {return goal_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ApproveRequest) obj;
            return this.goal_id == that.goal_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goal_id);
        }

        @Override
        public String toString() {
            return "ApproveRequest[" +
                    "goal_id=" + goal_id + ']';
        }
    }

    static final class ApproveResponse {
        private final int approval_count;

        ApproveResponse(int approval_count) {this.approval_count = approval_count;}

        public int approval_count() {return approval_count;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ApproveResponse) obj;
            return this.approval_count == that.approval_count;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(approval_count);
        }

        @Override
        public String toString() {
            return "ApproveResponse[" +
                    "approval_count=" + approval_count + ']';
        }
    }

    static final class ListResponse {
        private final List<Objects.Goal> goalList;

        ListResponse(List<Objects.Goal> goalList) {this.goalList = goalList;}

        public List<Objects.Goal> goalList() {return goalList;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ListResponse) obj;
            return java.util.Objects.equals(this.goalList, that.goalList);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(goalList);
        }

        @Override
        public String toString() {
            return "ListResponse[" +
                    "goalList=" + goalList + ']';
        }
    }





}
