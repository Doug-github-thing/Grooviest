package com.bot;

import static spark.Spark.*;
import spark.Filter;
import java.lang.Integer;

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

        // Adds audio with the given youtube ID to the queue
        post("/api/add/:url", (req, res) -> {
            String url = req.params(":url");
            bot.addSong(url);
            return "Attempting to add the following youtube video to the queue: " + url;
        });

        // Removes the queue element with the given position
        post("/api/remove/:position", (req, res) -> {
            String stringPosition = req.params(":position");
            int position = Integer.parseInt(stringPosition);

            db.removeSong(position);

            res.status(200);
            return "Attempting to remove the song at the following position: " + position;
        });

        // Moves the element with the given position to the new position
        post("/api/move/:initialPosition/:newPosition", (req, res) -> {
            String stringInitialPosition = req.params(":initialPosition");
            String stringNewPosition = req.params(":newPosition");

            // Convert string parameters to the desired types
            int initialPosition = Integer.parseInt(stringInitialPosition);
            int newPosition = Integer.parseInt(stringNewPosition);

            db.moveSong(initialPosition, newPosition);

            res.status(200);
            return "Attempting to move position " + initialPosition + " to position " + newPosition;
        });

        // Adds audio with the given youtube ID to the queue
        post("/api/seek/:ms", (req, res) -> {
            String stringMS = req.params("ms");
            long ms = Long.parseLong(stringMS);
            bot.seek(ms);
            return "Attempting to seek in the current track at: " + stringMS + " ms.";
        });

        // Stop Spark gracefully on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));
    }
}
