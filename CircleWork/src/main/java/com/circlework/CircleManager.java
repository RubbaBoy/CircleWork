package com.circlework;

import com.circlework.handlers.Auth;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.crypto.Data;
import java.util.Map;
import java.util.UUID;

public class CircleManager {

    /**
     * create a new circle
     *
     * @return the id of the newly created circle
     * */
    public int createCircle() throws Exception{
        int circleId = -1;
        var conn = DataSource.getConnection();
        try(var stmt = conn.prepareStatement("INSERT into circles DEFAULT VALUES")){
            stmt.executeUpdate();
        }

        try(var stmt = conn.prepareStatement(
                "SELECT id FROM circles where id=(SELECT max(id) FROM circles)")){
            var query = stmt.executeQuery();
            if(query.next()){
                circleId = query.getInt(1);
            }
        }

        System.out.println("circleId");
        System.out.println(circleId);

        try(var stmt = conn.prepareStatement(
                "INSERT into circles " +
                        "(name, color, team_count, tasks_started, tasks_completed, raised_monthly, raised_total) " +
                        "VALUES('Circle " + circleId + "', 0, 1, 0, 0, 0, 0)")){
//            stmt.setString(1, "Circle " + circleId);
//            stmt.setInt(2, 0);//color 0
//            stmt.setInt(3, 1);//teamcount of 1
//            stmt.setInt(4, 0);//no tasks started
//            stmt.setInt(5, 0);//no tasks completed
//            stmt.setInt(6, 0);//nothing raised this month
//            stmt.setInt(7, 0);//nothing raised total
//            stmt.setInt(8, circleId);//at the circle id
            stmt.executeUpdate();
        }

        return circleId;
    }


    public int getTotalDonations(int circleId) throws Exception{
        var conn = DataSource.getConnection();
        try(var stmt = conn.prepareStatement("SELECT raised_total FROM circles where id=?")){
            stmt.setInt(1, circleId);
            var query = stmt.executeQuery();
            if(query.next()){
                int total = query.getInt(1);
                return total;
            }
        }
        return 0;
    }

}