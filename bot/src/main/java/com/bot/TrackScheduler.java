package com.bot;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import java.util.Timer;
import java.util.TimerTask;

public class TrackScheduler extends AudioEventAdapter {

    /**
     * The Bot instance, used for calling for new songs to be played
     * on previous track completion.
     */
    private Bot bot;

    /**
     * The database instance, used for editing database values which
     * communicate track status to the clients.
     */
    private Database db;

    /**
     * Context used for identifying this class in the stdout logs.
     */
    private String logContext = "Player";

    /**
     * A timer object to keep track of how long the song has been playing.
     */
    private Timer timer = new Timer(true);

    /**
     * Track currently playing.
     */
    private AudioTrack currentTrack = null;

    /**
     * 
     * @param player Player instance to statisfy AudioEventAdapter implementation.
     * @param bot    Bot instance, used for interacting with the main bot functions.
     * @param db     Database instance, used for interacting with Firebase RTDB.
     */
    public TrackScheduler(AudioPlayer player, Bot bot, Database db) {
        super();
        this.bot = bot;
        this.db = db;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        Logging.log(logContext, "Playback paused");

        timer.purge();
        // Update the database's paused variable to reflect this
        db.addEntry("paused", "true");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        Logging.log(logContext, "Playback resumed");

        scheduleElapsedTimer();
        // Update the database's paused variable to reflect this
        db.addEntry("paused", "false");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing

        currentTrack = track;
        Logging.log(logContext, "Started new track: " + track.getInfo());
        db.addEntry("now_playing/duration", "" + track.getDuration());
        db.addEntry("now_playing/elapsed", "" + 0, false);

        scheduleElapsedTimer();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        timer.purge();
        currentTrack = null;
        Logging.log(logContext, "Finished track: " + track.toString());

        if (endReason.mayStartNext) {
            Logging.log(logContext, "Calling Bot.playNext()");
            bot.playNext();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext
        // = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not
        // finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you
        // can put a
        // clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be
        // received separately)
        Logging.log(logContext, "Reached an exception playing track: " + track.toString());
        timer.purge();
        Logging.log(logContext, "Playing next...");
        bot.playNext();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start
        // a new track
        Logging.log(logContext, "Player got stuck playing track: " + track.toString());
        timer.purge();
        Logging.log(logContext, "Playing next...");
        bot.playNext();
    }

    /**
     * Schedules a new "elapsed time" timer for the track currently playing.
     */
    private void scheduleElapsedTimer() {
        // Start a timer to update the database every second
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Add entry without logging!
                if (currentTrack != null)
                    db.addEntry("now_playing/elapsed", "" + currentTrack.getPosition(), true);
            }
        }, 1000, 1000);

    }
}