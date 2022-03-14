package com.circlework.handlers;

import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.util.LinkedList;
import java.util.List;

public class Charities extends BasicHandler {

    @Override
    public void registerPaths() {
        registerPath(new String[]{"list"}, "GET", Objects.Empty.class, this::list);
    }

    void list(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws Exception {
        var feedRows = SQLUtility.executeQuery("SELECT id, name, image, link, raised_monthly, raised_total FROM charities");

        List<Objects.Charity> charityList = new LinkedList<>();

        for (Row curRow : feedRows) {
            charityList.add(new Objects.Charity(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4), curRow.get(5)));
        }

//            int id = goalRow.get(0);
//            int ownerId = goalRow.get(1);
//            boolean isPrivate = goalRow.get(2);
//            String goalName = goalRow.get(3);
//            String goalBody = goalRow.get(4);
//            int approvalCount = goalRow.get(5);
//            int category = goalRow.get(6);

        setBody(exchange, charityList, 200);
    }
}
