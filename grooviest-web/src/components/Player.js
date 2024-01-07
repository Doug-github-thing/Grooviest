// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying the song card
import NowPlayingCard from "./NowPlayingCard";
import MyProgressBar from "./ProgressBar";
import Header from "./Header";


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
                <p>Not currently playing</p> :
                    paused === "true" ? 
                        <p style={{marginTop:"0.5vh"}}>PAUSED</p>
                            :
                        <p style={{margin:"0.5vh"}}>Now Playing</p>}


            <Header />
            
        
            <div className="now-playing">
                { // Check if the player is playing anything
                (playing != null) ? 
                    <NowPlayingCard song={playing} paused={paused} /> : <></>
                }
            </div>
            <MyProgressBar />
        </>
    );
}

export default Player;