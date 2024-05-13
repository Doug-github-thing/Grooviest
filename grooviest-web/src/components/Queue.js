// Reactively display the current queue
import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

import QueueCard from "./QueueCard";
import "./Queue.css";


/**
 * @returns The current contents of the queue in a list of QueueCard components.
 */
const Queue = () => {


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
        <div id="queue-wrapper">

            {/* The title */}
            <h2>Queue</h2>

            {/* The list */}
            <div className="songs">
                {songs == null ? (
                    <>The queue is empty</>
                    ) : (
                    songs.map((song, index) => (
                        <QueueCard key={index} song={song} index={index} />
                    ))
                )}
            
            </div>
        </div>
      );
};

export default Queue;