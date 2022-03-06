package com.circlework.handlers;

import com.circlework.AuthManager;
import com.circlework.CircleManager;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Categories extends BasicHandler {

    public Categories(AuthManager authManager, CircleManager circleManager) {
        super(authManager, circleManager);
    }

    @Override
    public void registerPaths() {
        registerPath(new String[] {"list"}, "GET", Objects.Empty.class, this::list);
        registerPath(new String[] {"resources"}, "GET", ResourcesRequest.class, this::resources);
    }

    void list(HttpExchange exchange, String[] path, String method, Objects.Empty body, String token) throws Exception{
        if(!authManager.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try{
            List<Objects.GoalCategory> categories = new LinkedList<>();
            var categoryRows = SQLUtility.executeQuery("SELECT * from goal_categories");

            for(Row curRow: categoryRows){
                categories.add(new Objects.GoalCategory(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3)));
            }

            setBody(exchange, new ListResponse(categories), 200);

        } catch(Exception e){
            setBody(exchange, Map.of("message", "error"), 500);
        }
    }

    void resources(HttpExchange exchange, String[] path, String method, ResourcesRequest body, String token) throws Exception{
        if(!authManager.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        try{

            List<Objects.CategoryResources> resourcesList = new LinkedList<>();

            var resourceRows = SQLUtility.executeQuery("SELECT * from category_resources where category=?", body.id);

            for(Row curRow: resourceRows){
                resourcesList.add(new Objects.CategoryResources(curRow.get(0), curRow.get(1), curRow.get(2), curRow.get(3), curRow.get(4)));
            }

            setBody(exchange, new ResourcesResponse(resourcesList), 200);

        } catch(Exception e){
            setBody(exchange, Map.of("message", "error"), 500);
        }

    }

    static final class ListResponse {
        private final List<Objects.GoalCategory> category_list;

        ListResponse(List<Objects.GoalCategory> category_list) {this.category_list = category_list;}

        public List<Objects.GoalCategory> category_list() {return category_list;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ListResponse) obj;
            return java.util.Objects.equals(this.category_list, that.category_list);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(category_list);
        }

        @Override
        public String toString() {
            return "ListResponse[" +
                    "category_list=" + category_list + ']';
        }
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

    static final class ResourcesResponse {
        private final List<Objects.CategoryResources> resources;

        ResourcesResponse(List<Objects.CategoryResources> resources) {this.resources = resources;}

        public List<Objects.CategoryResources> resources() {return resources;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ResourcesResponse) obj;
            return java.util.Objects.equals(this.resources, that.resources);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(resources);
        }

        @Override
        public String toString() {
            return "ResourcesResponse[" +
                    "resources=" + resources + ']';
        }
    }

}
