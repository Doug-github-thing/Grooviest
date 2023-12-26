package com.bot;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String token = System.getenv("DISCORD_TOKEN");

        Database db = new Database();
        String breadValue = db.getBreadValue().get();
        Logging.log("Main", breadValue);

        Bot bot = new Bot(token);

        Spark.setupRoutes(bot);
    }
}
