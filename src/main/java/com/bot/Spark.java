package com.bot;

import com.bot.Bot;
import static spark.Spark.*;

public class Spark {

    public static void setupRoutes(Bot bot) {
        // Set up Spark
        port(4567); // Set the port for your Spark server

        get("/bot_api/:param", (req, res) -> {
            String param = req.params(":param");
            bot.touch(param);
            res.status(200);
            res.body("Heggo!");
            return "You said, " + param + "!";
        });

        // Stop Spark gracefully on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));
    }
}
