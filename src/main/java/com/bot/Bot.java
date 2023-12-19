package com.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public Bot(String TOKEN) {
        JDABuilder builder = JDABuilder.createDefault(TOKEN);
        builder.addEventListeners(this);
        builder.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().equalsIgnoreCase("!egg")) {
            event.getChannel().sendMessage("Hello, " + event.getAuthor().getAsMention() + "!").queue();
        }
    }

    public void touch(String str) {
        System.out.println("I am the bot and I am getting from the server: " + str);
    }
}
