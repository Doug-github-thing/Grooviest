// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying the song card
import NowPlayingCard from "./NowPlayingCard";

import PlayerUtils from "../utils/PlayerUtils"

const Player = () => {

    // For displaying the Now Playing message
    const [playing, setPlaying] = useState();
    // For tracking paused status
    const [paused, setPaused] = useState("");
    // For track seeking
    const [duration, setDuration] = useState("");
    const [elapsed, setElapsed] = useState("");


    // Updates "player" with the value of the current song in the database 
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing");
        return onValue( query, (snapshot) => setPlaying(snapshot.val()));
    }, []);

    // Tracks if the bot is paused
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "paused");
        return onValue( query, (snapshot) => setPaused(snapshot.val()) );
    }, []);

    // For track seeking
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing/duration");
        return onValue( query, (snapshot) => setDuration(snapshot.val()) );
    }, []);

    // For track seeking
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing/elapsed");
        return onValue( query, (snapshot) => setElapsed(snapshot.val()) );
    }, []);


    return (
        <>
            {playing == null ?
                <h2>Not currently playing</h2> :
                    paused === "true" ? 
                        <h2>PAUSED</h2>
                            :
                        <h2>Now Playing</h2>}
        
            <div className="now-playing">
                { // Check if the player is playing anything
                (playing != null) ? 
                    <NowPlayingCard song={playing} paused={paused} /> : <></>
                }
            </div>
            <div>{PlayerUtils.formatTime(elapsed)} / {PlayerUtils.formatTime(duration)}</div>
        </>
    );
}

export default Player;