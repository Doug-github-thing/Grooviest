package com.bot;

public class Main {
    public static void main(String[] args) {

        String token = System.getenv("DISCORD_TOKEN");

        Database db = new Database();

        // db.setBreadValue("good bread");
        // Logging.log("Main", db.getBreadValue());

        Bot bot = new Bot(token, db);

        Spark.setupRoutes(bot, db);
    }
}
