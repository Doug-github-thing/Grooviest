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
                </>}
            </h4>
            
            <div className="control-buttons">
                <CommandButton text="Join" command="join" />
                <CommandButton text="Leave" command="leave" />
            </div>
        </header>
    );
}

export default Header;
