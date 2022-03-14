package com.circlework;


import com.circlework.handlers.Auth;
import com.circlework.handlers.BasicHandler;
import com.circlework.handlers.Categories;
import com.circlework.handlers.Charities;
import com.circlework.handlers.Circles;
import com.circlework.handlers.Donations;
import com.circlework.handlers.Goals;
import com.circlework.handlers.Meta;
import com.circlework.handlers.UserPayment;
import com.circlework.manager.AppInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;

public class ClientApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

    private final Injector injector;

    public ClientApplication(Injector injector) {
        this.injector = injector;
    }

    private void start() {
        try {
            setLoggingLevel(ch.qos.logback.classic.Level.DEBUG);

            DataSource.init();

            var server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/api/auth", createHandler(Auth.class));
            server.createContext("/api/circles", createHandler(Circles.class));
            server.createContext("/api/goals", createHandler(Goals.class));
            server.createContext("/api/category", createHandler(Categories.class));
            server.createContext("/api/donations", createHandler(Donations.class));
            server.createContext("/api/meta", createHandler(Meta.class));
            server.createContext("/api/payment", createHandler(UserPayment.class));
            server.createContext("/api/charities", createHandler(Charities.class));
            server.setExecutor(null);
            server.start();

            LOGGER.info("Started webserver");


            Date date = new Date();
            Timer timer = new Timer();

//            timer.schedule(new TimerTask() {
//                public void run() {
//                    try {
//                        if (date.getDate() == 1) {
//
//                            //Resets Monthly values for charities and circles.
//
//                            var resetRow = executeQuerySingle("UPDATE circles SET raised_monthly=0");
//
//                            resetRow = executeQuerySingle("UPDATE charities SET raised_monthly=0");
//
//                            var userBalances = SQLUtility.executeQuery("UPDATE user SET balance = 500 * (tasks_completed / tasks_started) RETURNING balance");
//                            //TODO iterate through each of the balances and refund the users that amount
//
//                            //reset their tasks back to 0
//                            SQLUtility.executeQuerySingle("UPDATE users SET tasks_started, tasks_completed, balance = 0");
//                        }
//                        //next get a list of all users, use this to get all tasks. Check if each task is approved,
//                        //if so, increment tasks completed for group of user, set approved to -1.
//                        //If a task is past deadline but not fully approved, also set approved to -1
//
//                        //start by getting list of all circle_ids
//
//                        // <id, team count>
//                        Map<Integer, Integer> circles = SQLUtility.executeQuery("SELECT id FROM circles")
//                                .stream()
//                                .map(row -> new AbstractMap.SimpleEntry<Integer, Integer>(row.get(0), row.get(3)))
//                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//
//                        //Break feed (method in circle handler) into helper method, run it on the list of IDs, append all lists of goals together
//
//
//
//                        //Run through each
//
//                        var goals = SQLUtility.executeQuery("SELECT * FROM goals WHERE approval_count > 0")
//                                .stream()
//                                .map(row -> new Objects.Goal(row.get(0), row.get(1), null, row.get(2),row.get(3), row.get(4), row.get(5), row.get(6), row.get(7)))
//                                .toList();
//
//                        // user id, circle id
//                        var map = new HashMap<Integer, Integer>();
//
//                        for (var goal : goals) {
//                            var circleId = map.computeIfAbsent(goal.owner_id(), $ -> {
//                                try {
//                                    return executeQuerySingle("SELECT circle_id FROM users WHERE id = ?", goal.owner_id()).get(0);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                    return null;
//                                }
//                            });
//
//                            if (goal.due_date().after(new Timestamp(System.currentTimeMillis())) && goal.approval_count() > circles.get(circleId) * 0.5) {//if both
//                                // has enough votes increment circle
//                                SQLUtility.executeQuery("UPDATE circles SET completed_tasks = completed_tasks + 1 where id=?", circleId); // add to task_completed
//                                SQLUtility.executeQuery("UPDATE users SET completed_tasks = completed_tasks + 1 where id=?", goal.owner_id());//update the users completed tasks
//                                int userBalance = SQLUtility.executeQuerySingle("UPDATE user SET balance = 500 * (tasks_completed / tasks_started) where id=? RETURNING balance", goal.owner_id()).get(0);
//                            }
//
//                            if(!goal.due_date().after(new Timestamp(System.currentTimeMillis()))){                            //if due date hasnt passed
//                                continue;
//                            }else{//if time passed but didnt reach enough approvals
//                                SQLUtility.executeQuery("UPDATE goals SET approval_count = -1 where id=?", goal.id());
//                            }
//                        }
//
//
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, date, 24 * 60 * 60 * 1000);

        } catch (Exception e) {
            LOGGER.error("Uh oh lmao", e);
        }
    }

    public static void setLoggingLevel(ch.qos.logback.classic.Level level) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }

    private <T extends BasicHandler> T createHandler(Class<T> handlerClass) {
        var handler = injector.getInstance(handlerClass);
        handler.init();
        return handler;
    }

    public static void main(String[] args) {
        new ClientApplication(Guice.createInjector(new AppInjector())).start();
    }

}
