package com.bot;

import static spark.Spark.*;
import spark.Filter;

public class Spark {

    public static void setupRoutes(Bot bot) {
        // Set up Spark
        port(25566); // Set the port for your Spark server

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        // For basic single line commands
        post("/bot_api/:param", (req, res) -> {
            String param = req.params(":param");
            bot.parseWebCommand(param);
            return "Attempting to perform command: " + param;
        });

        // Plays audio with a given filename
        post("/bot_api/file/:name", (req, res) -> {
            String name = req.params(":name");
            bot.playFile(name);
            return "Attempting to play file: " + name;
        });

        // Stop Spark gracefully on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));
    }
}
