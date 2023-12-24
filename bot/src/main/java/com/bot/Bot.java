package com.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.OnlineStatus;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;

import java.io.File;

public class Bot extends ListenerAdapter {

    // Bot instance, used for setting activity after initial build
    JDA botJDA;

    // Set up JDA Discord parameters
    Guild myGuild;
    AudioManager audioManager;
    AudioChannelUnion audioChannel;

    // Set up Lavaplater manager for playing audio
    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    AudioPlayer player;
    TrackScheduler trackScheduler;

    public Bot(String TOKEN) {
        botJDA = JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity
                        .customStatus("Please use -join in a voice channel to connect"))
                .build();

        // Set up player manager to work with local audio, and youtube audio
        playerManager.registerSourceManager(new LocalAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, null, null));

        // Make AudioPlayer player within the AudioPlayerManager manager
        player = playerManager.createPlayer();

        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
    }

    // Callback when a message is received
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Filter message to make sure it's a real command
        if (event.getAuthor().isBot()
                || !event.isFromGuild()
                || !event.getMessage().getContentRaw().startsWith("-"))
            return;

        // "join" route: Identifies which audio channel the requestor is in, then joins.
        if (event.getMessage().getContentRaw().equalsIgnoreCase("-join")) {
            myGuild = event.getGuild();

            // Let 'em know
            String msg = "Now listening in: " + event.getGuild() + "!";
            event.getChannel().sendMessage(msg).queue();

            // Connect to the voice channel
            audioManager = myGuild.getAudioManager();
            audioChannel = event.getMember().getVoiceState().getChannel();
            audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
            audioManager.openAudioConnection(audioChannel);

            // Change activity and status to reflect new channel connection
            botJDA.getPresence().setStatus(OnlineStatus.ONLINE);
            botJDA.getPresence().setActivity(Activity.customStatus(
                    "Currently attached to " + myGuild.getName() + ": " + audioChannel.getName()));
        }

        // "egg" route: Says egg.
        if (event.getMessage().getContentRaw().equalsIgnoreCase("-egg")) {
            event.getChannel().sendMessage("emngmgg").queue();
        }
    }

    /**
     * Reads a command coming from a web request pointed at the bot's server
     * address. Passes this on to the appropriate designated method.
     * 
     * @param str The name of the command, ie "join", "leave"
     */
    public void parseWebCommand(String str) {
        if (myGuild == null) {
            Logging.log("Web", "Use '-join' command to connect me to a voice channel");
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

    /**
     * Bot attempts to join the currently selected audio channel.
     */
    public void join() {
        Logging.log("Web", "Attempting to join the channel");
        audioManager.openAudioConnection(audioChannel);
    }

    /**
     * Bot attempts to disconnect from the current audio channel.
     */
    public void leave() {
        Logging.log("Web", "Attempting to leave the channel");
        audioManager.closeAudioConnection();
    }

    /**
     * Bot attempts to play the audio file specified in the filename parameter.
     * 
     * @param filename The filename of file to play.
     */
    public void playFile(String filename) {
        Logging.log("Web", "I am supposed to try to play the file: /sounds/" + filename);

        File soundFile = new File("/sounds/" + filename).getAbsoluteFile();

        Logging.log("Web", soundFile.toString());

        // Standin Youtube for "a"
        playerManager.loadItem("Ku6nJjmEeaw", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    Logging.log("TODO", "Implement AudioLoadResultHandler playlistLoaded");
                    // trackScheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
            }
        });

    }
}
