package com.circlework.handlers;

import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Categories extends BasicHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Categories.class);

    @Override
    public void registerPaths() {
        registerPath(new String[]{"list"}, "GET", Objects.Empty.class, this::list);
        registerPath(new String[]{"resources"}, "GET", ResourcesRequest.class, this::resources);
    }

    void list(HttpExchange exchange, String[] path, String method, Objects.Empty body, String token) throws Exception {
        LOGGER.info("listing herre!!!!");
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        List<Objects.GoalCategory> categories = new LinkedList<>();
        var categoryRows = SQLUtility.executeQuery("SELECT * from goal_categories");

        for (Row curRow : categoryRows) {
            categories.add(new Objects.GoalCategory(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3)));
        }

        LOGGER.info("cats = {}", categories);
        setBody(exchange, categories, 200);
    }

    void resources(HttpExchange exchange, String[] path, String method, ResourcesRequest body, String token) throws Exception {
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        List<Objects.CategoryResources> resourcesList = new LinkedList<>();

        var resourceRows = SQLUtility.executeQuery("SELECT * from category_resources where category=?", body.id);

        for (Row curRow : resourceRows) {
            resourcesList.add(new Objects.CategoryResources(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4)));
        }

        setBody(exchange, resourcesList, 200);
    }

    static final class ResourcesRequest {
        private final int id;

        ResourcesRequest(int id) {this.id = id;}

        public int id() {return id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ResourcesRequest) obj;
            return this.id == that.id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id);
        }

        @Override
        public String toString() {
            return "ResourcesRequest[" +
                    "id=" + id + ']';
        }
    }
}
