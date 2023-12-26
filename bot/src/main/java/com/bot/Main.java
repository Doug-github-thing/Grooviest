package com.bot;

public class Main {
    public static void main(String[] args) {

        String token = System.getenv("DISCORD_TOKEN");

        Database db = new Database();
        Logging.log("Main", db.getBread());

        Bot bot = new Bot(token);

        Spark.setupRoutes(bot);
    }
}
