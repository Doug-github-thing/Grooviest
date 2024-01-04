// Reactively display the current connection location
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";

// Button icons
import { IoEnterOutline, IoExitOutline } from "react-icons/io5";

import "./Header.css";

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

            <div id="wrapper">
                <div id="join-button">
                    <CommandButton icon={<IoEnterOutline/>} command="join" />
                </div>

                <h4 id="online-status">
                    {onlineStatus !== "true" ?
                    <>The bot is offline</>
                    :
                    <>
                        <div id="connection-status">
                            {(channel ? 
                                <>Currently attached to: {channel}</>
                                :
                                <>Please use -join while connected to a voice channel</>
                            )}
                        </div>
                    </>}
                </h4>
                
                <div id="leave-button">
                    <CommandButton text={<IoExitOutline/>} command="leave" />
                </div>

            </div>

        </header>
    );
}

export default Header;
