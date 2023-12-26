package com.bot;

import static spark.Spark.*;
import spark.Filter;

public class Spark {

    /**
     * Sets up the project's Java Spark web server.
     * 
     * @param bot An object encapsulating Bot behavior and state.
     * @param db  A Database object encapusating Firebase database functions.
     */
    public static void setupRoutes(Bot bot, Database db) {

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
        post("/api/:param", (req, res) -> {
            String param = req.params(":param");
            bot.parseWebCommand(param);
            return "Attempting to perform command: " + param;
        });

        // Plays audio with a given filename
        post("/api/file/:name", (req, res) -> {
            String name = req.params(":name");
            boolean success = bot.playFile(name);

            res.status(200);
            if (success)
                res.body("Successful");
            else
                res.body("Unsuccessful");

            return "Attempting to play file: " + name;
        });

        // Adds audio with the given youtube ID to the queue
        post("/api/add/:url", (req, res) -> {
            String url = req.params(":url");
            db.addSong(url);

            res.status(200);
            return "Attempting to add the following youtube video to the queue: " + url;
        });

        // Stop Spark gracefully on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));
    }
}
