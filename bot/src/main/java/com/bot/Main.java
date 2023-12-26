package com.bot;

public class Main {
    public static void main(String[] args) {

        String token = System.getenv("DISCORD_TOKEN");

        Database db = new Database();

        Bot bot = new Bot(token, db);

        Spark.setupRoutes(bot, db);

        Logging.log("Songs", "These songs are in the database: " + db.getSongs());
    }
}
