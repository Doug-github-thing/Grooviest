// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";
import NowPlayingCard from "./NowPlayingCard";

const Player = () => {

    // For displaying the Now Playing message
    const [playing, setPlaying] = useState();
    // For tracking paused status
    const [paused, setPaused] = useState("");


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
        </>
    );
}

export default Player;