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

import java.util.ArrayList;

/**
 * An object encapsulating Discord Bot functions according to JDA API.
 */
public class Bot extends ListenerAdapter {

    // Bot instance, used for setting activity after initial build
    private JDA botJDA;

    // Set up JDA Discord parameters
    private Guild myGuild;
    private AudioManager audioManager;
    private AudioChannelUnion audioChannel;

    // Set up Lavaplater manager for playing audio
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private AudioPlayer player;
    private TrackScheduler trackScheduler;

    /**
     * Prefix for console logs which identify that a log belongs to this component.
     */
    private String logContext = "Bot";

    /**
     * Instance of my Database object. Controls connection to the Firebase Realtime
     * Database. Defines get and set methods for values in the database.
     * Created as null, passed to the constructor on build.
     */
    private Database db;

    /**
     * Creates Bot instance. Requires Bot API token, and a database connection to
     * the Firebase Realtime Database for reading and writing queue, and
     * communicating bot state to the web clients.
     * The Database connection is controlled by that Database object.
     * 
     * @param TOKEN Discord private bot ID token, from Discord bot dev portal.
     * @param db    Instance of my Database object.
     */
    public Bot(String TOKEN, Database db) {

        // Initialize the database connection.
        this.db = db;

        // Initialize the Bot connection using JDA Discord bot API.
        botJDA = JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.customStatus("Please use -join in a voice channel to connect"))
                .build();

        // Set up player manager to work with local audio, and youtube audio
        playerManager.registerSourceManager(new LocalAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, null, null));

        // Make AudioPlayer player within the AudioPlayerManager manager
        player = playerManager.createPlayer();

        // Initialize Lavaplayer functions to play sounds through the bot voice channel.
        trackScheduler = new TrackScheduler(player, this, db);
        player.addListener(trackScheduler);

        // Since the bot can't start paused or connected
        db.addEntry("paused", "false");
        db.addEntry("location", "");
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

            // Check if user is connected to a voice channel
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("Please join a voice channel in this server").queue();
                return;
            }

            myGuild = event.getGuild();

            // Let 'em know
            String msg = "Now listening in: " + event.getGuild() + "!";
            event.getChannel().sendMessage(msg).queue();

            // Connect to the voice channel
            audioManager = myGuild.getAudioManager();
            audioChannel = event.getMember().getVoiceState().getChannel();
            audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
            audioManager.openAudioConnection(audioChannel);

            // Change activity and status, update database to reflect new channel connection
            botJDA.getPresence().setStatus(OnlineStatus.ONLINE);
            botJDA.getPresence().setActivity(Activity.customStatus(
                    "Currently attached to " + myGuild.getName() + ": " + audioChannel.getName()));
            db.setLocationValue(myGuild.getName() + ": " + audioChannel.getName());
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
            Logging.log(logContext, "Use '-join' command to connect me to a voice channel");
            return;
        }

        switch (str) {
            case "join":
                join();
                break;
            case "leave":
                leave();
                break;
            case "pause":
                pause();
                break;
            case "play":
                play();
                break;
            case "skip":
                skip();
                break;
            default:
                break;
        }
    }

    /**
     * Bot attempts to join the currently selected audio channel.
     */
    private void join() {
        Logging.log(logContext, "Joining the channel");
        audioManager.openAudioConnection(audioChannel);
    }

    /**
     * Bot attempts to disconnect from the current audio channel.
     */
    private void leave() {
        Logging.log(logContext, "Leaving the channel");
        audioManager.closeAudioConnection();
    }

    /**
     * Bot attempts to pause playback.
     */
    private void pause() {
        player.setPaused(true);
    }

    /**
     * Bot attempts to resume playback. If already playing, does nothing.
     * If paused, resumes playback. If not paused and not playing, plays next song.
     */
    private void play() {
        if (player.isPaused()) {
            player.setPaused(false);
            return;
        }

        String nowPlaying = db.getValue("now_playing");
        if (nowPlaying == null || nowPlaying.length() == 0)
            playNext();
    }

    /**
     * Skips the currently playing song.
     */
    private void skip() {
        playNext();
    }

    /**
     * Bot attempts to play the audio file specified in the filename parameter.
     * 
     * @param url YoutubeID of the song to play.
     * @return True if performed successfully. False if not.
     */
    public boolean playURL(String url) {

        // If not currently in a channel
        if (audioManager == null || !audioManager.isConnected()) {
            Logging.log(logContext, "Not connected to a channel!");
            return false;
        }

        // Standin Youtube for "a"
        playerManager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // for (AudioTrack track : playlist.getTracks()) {
                // Logging.log("TODO", "Implement AudioLoadResultHandler playlistLoaded");
                // // trackScheduler.queue(track);
                // }
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

        return true;
    }

    /**
     * Adds a song to the database with the given youtubeID.
     * If the queue was empty prior to this entry, and the bot is not
     * already playing something, start playing now!
     * 
     * @param url YoutubeID of song to add
     */
    public void addSong(String url) {

        // Add the song the the queue.
        db.addSong(url);

        // If not already playing something, then start now.
        String nowPlaying = db.getValue("now_playing");
        if (nowPlaying == null || nowPlaying.length() == 0)
            playNext();
    }

    /**
     * Plays the song at the top of the queue, then removes the song from the queue.
     */
    public void playNext() {

        // Get the first song off the top of the database
        ArrayList<Song> queue = db.getSongs();
        if (queue.size() == 0) {
            Logging.log(logContext, "Reached end of queue");
            player.stopTrack();
            db.addEntry("now_playing", "");
            db.addEntry("paused", "false");
            return;
        }
        Song thisSong = queue.get(0);

        Logging.log(logContext, "Attemping to play next song in the database: " + thisSong.getName());

        // Remove from the top of the queue
        db.removeSong(thisSong.getPosition());

        // Update Now Playing to track the current song
        db.addEntry("now_playing", thisSong.getName());
        // Play that song
        playURL(thisSong.getURL());
    }
}
