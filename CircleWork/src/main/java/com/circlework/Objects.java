package com.circlework;

import java.sql.Timestamp;

public class Objects {

    //
    // IF YOU CHANGE ANY OF THESE, UPDATE THE objects.ts FILE TOO!!!!!!!!
    //

    public static final class Empty {
        public Empty() {}

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
            return "Empty[]";
        }
    }

    public static final class User {
        private final int id;
        private final String username;
        private final String password;
        private final int balance;
        private final int circle_id;

        public User(
                int id,
                String username,
                String password,
                int balance,
                int circle_id) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.balance = balance;
            this.circle_id = circle_id;
        }

        public int id() {return id;}

        public String username() {return username;}

        public String password() {return password;}

        public int balance() {return balance;}

        public int circle_id() {return circle_id;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (User) obj;
            return this.id == that.id &&
                    java.util.Objects.equals(this.username, that.username) &&
                    java.util.Objects.equals(this.password, that.password) &&
                    this.balance == that.balance &&
                    this.circle_id == that.circle_id;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, username, password, balance, circle_id);
        }

        @Override
        public String toString() {
            return "User[" +
                    "id=" + id + ", " +
                    "username=" + username + ", " +
                    "password=" + password + ", " +
                    "balance=" + balance + ", " +
                    "circle_id=" + circle_id + ']';
        }
    }

//1078

    public static final class Goal {
        private final int id;
        private final int owner_id;
        private final String owner_name;
        private final boolean is_private;
        private final int categoryId;
        private final String goal_name;
        private final String goal_body;
        private final Timestamp due_date;
        private final int approval_count;

        public Goal(
                int id,
                int owner_id,
                String owner_name,
                boolean is_private,
                int categoryId,
                String goal_name,
                String goal_body,
                Timestamp due_date,
                int approval_count
        ) {
            this.id = id;
            this.owner_id = owner_id;
            this.owner_name = owner_name;
            this.is_private = is_private;
            this.categoryId = categoryId;
            this.goal_name = goal_name;
            this.goal_body = goal_body;
            this.due_date = due_date;
            this.approval_count = approval_count;
        }

        public int id() {return id;}

        public int owner_id() {return owner_id;}

        public String owner_name() {return owner_name;}

        public boolean is_private() {return is_private;}

        public int categoryId() {return categoryId;}

        public String goal_name() {return goal_name;}

        public String goal_body() {return goal_body;}

        public Timestamp due_date() {return due_date;}

        public int approval_count() {return approval_count;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Goal) obj;
            return this.id == that.id &&
                    this.owner_id == that.owner_id &&
                    java.util.Objects.equals(this.owner_name, that.owner_name) &&
                    this.is_private == that.is_private &&
                    this.categoryId == that.categoryId &&
                    java.util.Objects.equals(this.goal_name, that.goal_name) &&
                    java.util.Objects.equals(this.goal_body, that.goal_body) &&
                    java.util.Objects.equals(this.due_date, that.due_date) &&
                    this.approval_count == that.approval_count;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, owner_id, owner_name, is_private, categoryId, goal_name, goal_body, due_date, approval_count);
        }

        @Override
        public String toString() {
            return "Goal[" +
                    "id=" + id + ", " +
                    "owner_id=" + owner_id + ", " +
                    "owner_name=" + owner_name + ", " +
                    "is_private=" + is_private + ", " +
                    "categoryId=" + categoryId + ", " +
                    "goal_name=" + goal_name + ", " +
                    "goal_body=" + goal_body + ", " +
                    "due_date=" + due_date + ", " +
                    "approval_count=" + approval_count + ']';
        }
    }

    public static final class Circle {
        private final int id;
        private final String name;
        private final int color;
        private final int team_count;
        private final int monthly_donation;
        private final int total_donation;
        private final int tasks_started;
        private final int tasks_completed;

        public Circle(
                int id,
                String name,
                int color,
                int team_count,
                int monthly_donation,
                int total_donation,
                int tasks_started,
                int tasks_completed) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.team_count = team_count;
            this.monthly_donation = monthly_donation;
            this.total_donation = total_donation;
            this.tasks_started = tasks_started;
            this.tasks_completed = tasks_completed;
        }

        public int id() {return id;}

        public String name() {return name;}

        public int color() {return color;}

        public int team_count() {return team_count;}

        public int monthly_donation() {return monthly_donation;}

        public int total_donation() {return total_donation;}

        public int tasks_started() {return tasks_started;}

        public int tasks_completed() {return tasks_completed;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Circle) obj;
            return this.id == that.id &&
                    java.util.Objects.equals(this.name, that.name) &&
                    this.color == that.color &&
                    this.team_count == that.team_count &&
                    this.monthly_donation == that.monthly_donation &&
                    this.total_donation == that.total_donation &&
                    this.tasks_started == that.tasks_started &&
                    this.tasks_completed == that.tasks_completed;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, name, color, team_count, monthly_donation, total_donation, tasks_started, tasks_completed);
        }

        @Override
        public String toString() {
            return "Circle[" +
                    "id=" + id + ", " +
                    "name=" + name + ", " +
                    "color=" + color + ", " +
                    "team_count=" + team_count + ", " +
                    "monthly_donation=" + monthly_donation + ", " +
                    "total_donation=" + total_donation + ", " +
                    "tasks_started=" + tasks_started + ", " +
                    "tasks_completed=" + tasks_completed + ']';
        }
    }

    public static final class GoalCategory {
        private final int id;
        private final String name;
        private final String description;
        private final int color;

        public GoalCategory(
                int id,
                String name,
                String description,
                int color
        ) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.color = color;
        }

        public int id() {return id;}

        public String name() {return name;}

        public String description() {return description;}

        public int color() {return color;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (GoalCategory) obj;
            return this.id == that.id &&
                    java.util.Objects.equals(this.name, that.name) &&
                    java.util.Objects.equals(this.description, that.description) &&
                    this.color == that.color;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, name, description, color);
        }

        @Override
        public String toString() {
            return "GoalCategory[" +
                    "id=" + id + ", " +
                    "name=" + name + ", " +
                    "description=" + description + ", " +
                    "color=" + color + ']';
        }
    }

    public static final class Charity {
        private final int id;
        private final String name;
        private final String image;
        private final String link;
        private final int raised_monthly;
        private final int raised_total;

        public Charity(
                int id,
                String name,
                String image,
                String link,
                int raised_monthly,
                int raised_total
        ) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.link = link;
            this.raised_monthly = raised_monthly;
            this.raised_total = raised_total;
        }

        public int id() {return id;}

        public String name() {return name;}

        public String image() {return image;}

        public String link() {return link;}

        public int raised_monthly() {return raised_monthly;}

        public int raised_total() {return raised_total;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Charity) obj;
            return this.id == that.id &&
                    java.util.Objects.equals(this.name, that.name) &&
                    java.util.Objects.equals(this.image, that.image) &&
                    java.util.Objects.equals(this.link, that.link) &&
                    this.raised_monthly == that.raised_monthly &&
                    this.raised_total == that.raised_total;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, name, image, link, raised_monthly, raised_total);
        }

        @Override
        public String toString() {
            return "Charity[" +
                    "id=" + id + ", " +
                    "name=" + name + ", " +
                    "image=" + image + ", " +
                    "link=" + link + ", " +
                    "raised_monthly=" + raised_monthly + ", " +
                    "raised_total=" + raised_total + ']';
        }
    }

    public static final class Payment {
        private final int id;
        private final int user_id;
        private final int amount;

        public Payment(
                int id,
                int user_id,
                int amount
        ) {
            this.id = id;
            this.user_id = user_id;
            this.amount = amount;
        }

        public int id() {return id;}

        public int user_id() {return user_id;}

        public int amount() {return amount;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Payment) obj;
            return this.id == that.id &&
                    this.user_id == that.user_id &&
                    this.amount == that.amount;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, user_id, amount);
        }

        @Override
        public String toString() {
            return "Payment[" +
                    "id=" + id + ", " +
                    "user_id=" + user_id + ", " +
                    "amount=" + amount + ']';
        }
    }

    public static final class CategoryResources {
        private final int id;
        private final int category;
        private final String title;
        private final String link;
        private final int type;

        public CategoryResources(
                int id,
                int category,
                String title,
                String link,
                int type
        ) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.link = link;
            this.type = type;
        }

        public int id() {return id;}

        public int category() {return category;}

        public String title() {return title;}

        public String link() {return link;}

        public int type() {return type;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (CategoryResources) obj;
            return this.id == that.id &&
                    this.category == that.category &&
                    java.util.Objects.equals(this.title, that.title) &&
                    java.util.Objects.equals(this.link, that.link) &&
                    this.type == that.type;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, category, title, link, type);
        }

        @Override
        public String toString() {
            return "CategoryResources[" +
                    "id=" + id + ", " +
                    "category=" + category + ", " +
                    "title=" + title + ", " +
                    "link=" + link + ", " +
                    "type=" + type + ']';
        }
    }
}
