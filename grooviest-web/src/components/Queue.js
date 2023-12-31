// Reactively display the current queue
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";

import "./Queue.css";

const Queue = ({ className }) => {


    // For tracking which songs are currently in the queue.
    const [songs, setSongs] = useState([]);


    // Called each time there is an update to the queue in the database.
    // If it is now empty, set the state to an empty queue. Else, extract those values.
    useEffect(() => {
        const db = getDatabase();
        const query = ref(db, "songs");
        return onValue(query, (snapshot) => {
            const data = snapshot.val();
            snapshot.exists() ? setSongs(Object.values(data)) : setSongs();    
        });
    }, []);


    return (
        <div className={className}>
            <h2>Queue</h2>
            <div>
                {songs == null ? <></> :
                    songs.map((song, index) => (

                        <div className="song-wrapper" key={index}>
                            <div className="id">
                                <div className="name">Name: {song.name}</div>
                                <div className="url">URL: {song.url}</div>
                            </div>
                            <CommandButton className="button" text="-" command={`remove/${song.position}`}/>
                        </div>
                    ))}
            </div>
        </div>
    );
}

export default Queue;