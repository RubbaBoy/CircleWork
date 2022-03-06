package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Donations extends BasicHandler {

    public Donations(AuthManager authManager, CircleManager circleManager) {
        super(authManager, circleManager);
    }

    @Override
    public void registerPaths() {
        registerPath(new String[] {""}, "GET", Objects.Empty.class, this::get);
    }

    void get(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws IOException {
        if(!authManager.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        int raisedTotal = 0;
        int monthlyTotal = 0;

        try{
            var goalRows = SQLUtility.executeQuery("SELECT rasied_total, raised_monthly from charities");
            for (Row curRow: goalRows){
                raisedTotal += (int)curRow.get(0);
                monthlyTotal += (int)curRow.get(1);
            }


            setBody(exchange, new DonationResponse(raisedTotal, monthlyTotal), 200);
        }catch (SQLException e){
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    record DonationResponse(int total, int month) {}



}
