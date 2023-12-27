package com.bot;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

public class TrackScheduler extends AudioEventAdapter {

    private Bot bot;

    public TrackScheduler(AudioPlayer player, Bot bot) {
        super();
        this.bot = bot;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        Logging.log("Player", "Playback paused");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        Logging.log("Player", "Playback resumed");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
        Logging.log("Player", "Started new track: " + track.getInfo());
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            Logging.log("Player", "Finished track: " + track.toString());
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
        Logging.log("Player", "Reached an exception playing track: " + track.toString());
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start
        // a new track
        Logging.log("Player", "Player got stuck playing track: " + track.toString());
    }
}