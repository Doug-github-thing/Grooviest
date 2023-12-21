package com.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;

public class Bot extends ListenerAdapter {

    Guild myGuild;
    AudioManager audioManager;
    AudioChannelUnion audioChannel;

    // AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    // AudioSourceManagers.registerRemoteSources(playerManager);
    // attempting to use Lavaplayer for audio

    public Bot(String TOKEN) {
        JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .setActivity(Activity.customStatus("Being a little Bot"))
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot())
            return;
        if (!event.getMessage().getContentRaw().startsWith("-"))
            return;
        if (!event.isFromGuild())
            return;

        if (event.getMessage().getContentRaw().equalsIgnoreCase("-egg")) {
            event.getChannel().sendMessage("emngmgg").queue();
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("-join")) {
            myGuild = event.getGuild();

            // Let 'em know
            String msg = "Now listening in: " + event.getGuild() + "!";
            event.getChannel().sendMessage(msg).queue();

            // Connect to the voice channel
            audioManager = myGuild.getAudioManager();
            audioChannel = event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(audioChannel);
        }
    }

    public void parseWebCommand(String str) {
        if (myGuild == null) {
            System.out.println("Use '-join' command to connect me to a voice channel");
            return;
        }

        switch (str) {
            case "join":
                join();
                break;
            case "leave":
                leave();
                break;
            default:
                break;
        }
    }

    public void join() {
        System.out.println("Attempting to join the channel");
        audioManager.openAudioConnection(audioChannel);
    }

    public void leave() {
        System.out.println("Attempting to leave the channel");
        audioManager.closeAudioConnection();
    }

    public void playFile(String filename) {
        System.out.println("I am supposed to try to play the file: /sounds/" + filename);

        File soundFile = new File("/sounds/" + filename).getAbsoluteFile();

        System.out.println(soundFile);
    }
}
