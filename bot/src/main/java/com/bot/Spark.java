package com.bot;

import static spark.Spark.*;
import spark.Filter;

public class Spark {

    /**
     * Sets up the project's Java Spark web server.
     * 
     * @param bot An object encapsulating Bot behavior and state.
     */
    public static void setupRoutes(Bot bot) {

        final int port = 25566;

        // Set up Spark
        port(port);

        // Allow through CORS for development
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        // Get "/" to make sure it's running
        get("/", (req, res) -> {
            return "The Grooviest bot is live!";
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
