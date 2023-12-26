package com.bot;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {

        String token = System.getenv("DISCORD_TOKEN");

        Database db = new Database();

        try {
            Logging.log("Main", db.getBreadValue());
        } catch (ExecutionException | InterruptedException e) {
            Logging.log("Main", "Error accessing Bread value");
            e.printStackTrace();
        }

        Bot bot = new Bot(token);

        Spark.setupRoutes(bot);
    }
}
