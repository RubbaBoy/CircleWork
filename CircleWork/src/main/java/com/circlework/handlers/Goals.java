package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.DataSource;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Goals extends BasicHandler {

    public Goals(AuthManager authManager, CircleManager circleManager) {
        super(authManager, circleManager);
    }

    @Override
    public void registerPaths() {
        registerPath(new String[] {"add"}, "POST", AddRequest.class, this::add);
        registerPath(new String[] {"id"}, "GET", GetRequest.class, this::get);
        registerPath(new String[] {"approve"}, "POST", ApproveRequest.class, this::approve);
        registerPath(new String[] {"list"}, "GET", Objects.Empty.class, this::list);
    }

    void add(HttpExchange exchange, String[] path, String method, AddRequest request, String token) throws Exception {
//        var userIdOptional = authManager.validateToken(token);

        if(!authManager.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var user = authManager.getUserFromToken(token).orElseThrow();

        try{
            var idRow = SQLUtility.executeQuerySingle("INSERT into goals(owner, private, category, goal_name," +
                            " goal_body, approval_count) VALUES(?, ? , ?, ?, ?, ?) RETURNING id", user,
                    request.is_private, request.category, request.goal_name, request.goal_body, 1);
            int goalId = idRow.get(0);
            setBody(exchange, new AddResponse(goalId), 200);
        }catch (Exception e){
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    void get(HttpExchange exchange, String[] path, String method, GetRequest body, String token) throws Exception{
        if(!authManager.validateToken(token)){
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
        if(!authManager.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var userId = authManager.getUserFromToken(token).orElseThrow();

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
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    /*
       if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        var userId = authManager.getUserFromToken(token).orElseThrow();
     */

    void list(HttpExchange exchange, String[] path, String method, Objects.Empty empty, String token ) throws Exception{
        if (!authManager.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid token"), 418);//give teapot for invalid token
            return;
        }

        List<Objects.Goal> goalList = new LinkedList<>();

        var userId = authManager.getUserFromToken(token).orElseThrow();

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

    record AddRequest(boolean is_private, String goal_name, String goal_body, int category){}

    record AddResponse(int goal_id){}

    record GetRequest(int goal_id){}

    record GetResponse(Objects.Goal goal){}

    record ApproveRequest(int goal_id){}

    record ApproveResponse(int approval_count){}

    record ListResponse(List<Objects.Goal> goalList){}





}
