package com.circlework;


import com.circlework.handlers.Auth;
import com.circlework.handlers.Categories;
import com.circlework.handlers.Circles;
import com.circlework.handlers.Donations;
import com.circlework.handlers.Goals;
import com.circlework.handlers.Meta;
import com.circlework.handlers.UserPayment;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {


        Date date=new Date();
        Timer timer = new Timer();

        timer.schedule(new TimerTask(){
            public void run(){
                if(date.getDate()==1){
                    try {

                        //Resets Monthly values for charities and circles.

                        var resetRow = SQLUtility.executeQuerySingle("UPDATE circles SET raised_monthly=0");

                        resetRow = SQLUtility.executeQuerySingle("UPDATE charities SET raised_monthly=0");

                        //next get a list of all users, use this to get all tasks. Check if each task is approved,
                        //if so, increment tasks completed for group of user, set approved to -1.
                        //If a task is past deadline but not fully approved, also set approved to -1

                        for( int)



                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        },date, 24*60*60*1000);

        try {

            var authManager = new AuthManager();
            var circleManager = new CircleManager();

            var server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/api/auth").setHandler(new Auth(authManager, circleManager).init());
            server.createContext("/api/circles").setHandler(new Circles(authManager, circleManager).init());
            server.createContext("/api/goals").setHandler(new Goals(authManager, circleManager).init());
            server.createContext("/api/categories").setHandler(new Categories(authManager, circleManager).init());
            server.createContext("/api/donations").setHandler(new Donations(authManager, circleManager).init());
            server.createContext("/api/meta").setHandler(new Meta(authManager, circleManager).init());
            server.createContext("/api/payment").setHandler(new UserPayment(authManager, circleManager).init());
            server.setExecutor(null);
            server.start();

            LOGGER.info("Started webserver");

            DataSource.init();

        } catch (Exception e) {
            LOGGER.error("Uh oh lmao", e);
        }
    }

}
