package com.bot;

public class Main {
    public static void main(String[] args) {

        String token = System.getenv("DISCORD_TOKEN");

        Bot bot = new Bot(token);

        Spark.setupRoutes(bot);
    }
}
