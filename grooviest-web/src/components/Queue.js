// Reactively display the current queue
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

// For displaying buttons
import CommandButton from "./CommandButton";

import "./Queue.css";

const Queue = ({ className }) => {

    const db = getDatabase();

    const [songs, setSongs] = useState([]);

    useEffect(() => {
        const query = ref(db, "songs");
        return onValue(query, (snapshot) => {
            const data = snapshot.val();
  
            if (snapshot.exists()) {
                const newSongs = Object.values(data);
                setSongs(newSongs);
            }
        });
    }, []);

    return (
        <div className={className}>
            <h2>Queue</h2>
            <div>
                {songs.map((song, index) => (

                    <div className="song-wrapper" key={index}>
                        {/* <div className="index"> {index + 1} </div> */}
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