// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";

const Header = ({ title }) => {

    // For displaying which voice channel the bot is attached to
    const [channel, setChannel] = useState();
    // For displaying the Now Playing message
    const [playing, setPlaying] = useState();
    const db = getDatabase();

    useEffect(() => {
        const query = ref(db, "location");
        return onValue(query, (snapshot) => setChannel(snapshot.val()) );
      }, []);

    useEffect(() => {
        const query = ref(db, "now_playing");
        return onValue(query, (snapshot) => setPlaying(snapshot.val()) );
    }, []);

    return (
        <header className="app-header">
            <h1>{title}</h1>
            <h4 className="connection-status">Currently attached to: {channel}</h4>
            <h4 className="now-playing">Now Playing: {playing}</h4>

            <div className="control-buttons">
                <CommandButton text="Join" command="join" />
                <CommandButton text="Leave" command="leave" />
            </div>
        </header>
    );
}

export default Header;