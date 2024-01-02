import { useState, useEffect } from "react";
import { ref, getDatabase, onValue } from "firebase/database";

// For displaying ms timestamps as human readable
import PlayerUtils from "../utils/PlayerUtils";
// For displaying linear progress bar
import "./ProgressBar.css";

/**
 * @returns A linear progress bar for visualizing how for you are into the current song 
 */
const MyProgressBar = () => {
    // Tracks seeking progress as Numbers
    const [duration, setDuration] = useState(0);
    const [elapsed, setElapsed] = useState(0);

    
    // For track seeking
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing/duration");
        return onValue( query, (snapshot) => {
            if (snapshot.val())
                setDuration(Number(snapshot.val()))
            else
                setDuration(0);
        });
    }, []);

    // For track seeking
    useEffect(() => {
        const myDatabase = getDatabase();
        const query = ref(myDatabase, "now_playing/elapsed");
        return onValue( query, (snapshot) => {
            if (snapshot.val())
                setElapsed(Number(snapshot.val()))
            else
                setElapsed(0);
        });
    }, []);


    return (
        <div className="progress-bar">

            {duration === 0 ? 
                // If there's no song playing, just show an empty bar
                <div className="progress-bar-outside" />
                    :
                // Else, show the whole thing
                <>
                    <div className="left">{PlayerUtils.formatTime(elapsed)}</div>

                    <div className="progress-bar-outside">
                        {duration === 0 ? <></> :
                            <div className="progress-bar-inside" 
                                style={{width:`${100 * (elapsed / duration)}%`}} />
                        }
                    </div>

                    <div className="right">{PlayerUtils.formatTime(duration)}</div>   
                </>
            }

        </div>
    );
    
}

export default MyProgressBar;
    