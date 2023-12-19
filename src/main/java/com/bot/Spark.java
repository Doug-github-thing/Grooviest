package com.bot;

import com.bot.Bot;
import static spark.Spark.*;

public class Spark {

    public static void setupRoutes(Bot bot) {
        // Set up Spark
        port(4567); // Set the port for your Spark server

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
