// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";

const Header = ({ title }) => {

    // For tracking if the bot is online
    const [onlineStatus, setOnlineStatus] = useState();
    // For displaying which voice channel the bot is attached to
    const [channel, setChannel] = useState();
    // For displaying the Now Playing message
    const [playing, setPlaying] = useState();
    // For tracking paused status
    const [paused, setPaused] = useState("");

    
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "bot_online");
        return onValue( query, (snapshot) => setOnlineStatus(snapshot.val()) );
    }, []);

    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "location");
        return onValue( query, (snapshot) => setChannel(snapshot.val()) );
      }, []);

    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing");
        return onValue( query, (snapshot) => setPlaying(snapshot.val()) );
    }, []);

    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "paused");
        return onValue( query, (snapshot) => setPaused(snapshot.val()) );
    }, []);

    return (
        <header className="app-header">
            <h1>{title}</h1>

            <h4 className="online-status">
                {onlineStatus !== "true" ?
                <>The bot is offline</>
                :
                <>
                    <div className="connection-status">
                        {(channel ? 
                            <>Currently attached to: {channel}</>
                            :
                            <>Please use -join while connected to a voice channel</>
                        )}
                    </div>
                    <div className="now-playing">
                        { // Check if the player is playing anything
                        (playing === "" || playing === null) ? 
                        <>Not currently playing</>
                        :
                            // Check if it's paused
                            (paused !=="true") ?
                            <>Now playing: {playing}</> 
                            :
                            <>PAUSED: {playing}</>
                        }
                    </div>
                </>}
            </h4>
            
            <div className="control-buttons">
                <CommandButton text="Join" command="join" />
                <CommandButton text="Leave" command="leave" />
                <CommandButton text="&#9658;" command="play" />
                <CommandButton className="fa fa-pause" text="| |" command="pause" />
                <CommandButton text="skip" command="skip" />
            </div>
        </header>
    );
}

export default Header;
