package com.circlework;

import java.sql.Timestamp;

public class Objects {

    //
    // IF YOU CHANGE ANY OF THESE, UPDATE THE objects.ts FILE TOO!!!!!!!!
    //

    public record Empty() {}

    public record User(
        int id,
        String username,
        String password,
        int balance,
        int circle_id) {}

//1078

    public record Goal(
            int id,
            int owner_id,
            String owner_name,
            boolean is_private,
            int categoryId,
            String goal_name,
            String goal_body,
            Timestamp due_date,
            int approval_count
            ) {}

    public record Circle(
        int id,
        String name,
        int color,
        int team_count,
        int monthly_donation,
        int total_donation,
        int tasks_started,
        int tasks_completed) {}

    public record GoalCategory(
        int id,
        String name,
        String description,
        int color
    ) {}

    public record Charity(
        int id,
        String name,
        String image,
        String link,
        int raised_monthly,
        int raised_total
    ) {}

    public record Payment(
        int id,
        int user_id,
        int amount
    ) {}

    public record CategoryResources(
        int id,
        int category,
        String title,
        String link,
        int type
    ) {}
}
