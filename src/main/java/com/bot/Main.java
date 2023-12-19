package com.bot;

import com.bot.Bot;

public class Main 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        String token = System.getenv("DISCORD_TOKEN");
        Bot bot = new Bot(token);

        Spark.setupRoutes(bot);
    }
}
