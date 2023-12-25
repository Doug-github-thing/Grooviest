import React, { useState, useEffect } from "react";
import { getDatabase, ref, onValue } from "firebase/database";

const Queue = ({ className }) => {

    const [songs, setSongs] = useState([]);

    const db = getDatabase();

    useEffect(() => {
        const query = ref(db, "songs");
        return onValue(query, (snapshot) => {
          const data = snapshot.val();
          console.log(snapshot.toJSON());
    
          if (snapshot.exists()) {
            Object.values(data).map((song) => {
              setSongs((songs) => [...songs, song]);
            });
          }
        });
      }, []);

    return (
        <div className={className}>
            <h2>Queue</h2>
            <div>bwa</div>
            <div>
                {songs.map((song, index) => (
                    <div key={song.position}> {song.position} Name: {song.name} URL: {song.url} </div>
                ))}
            </div>
        </div>
    );
}

export default Queue;