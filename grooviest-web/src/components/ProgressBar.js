import { useState, useEffect } from "react";
import { ref, getDatabase, onValue } from "firebase/database";

// For displaying ms timestamps as human readable
import PlayerUtils from "../utils/PlayerUtils";
// For displaying linear progress bar
// import "bootstrap/dist/css/bootstrap.css";
// import ProgressBar from "react-bootstrap/ProgressBar";

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
        <div>
            <div className="progress-bar-outside" style={{
                width:`100%`,
                height:"5px",
                backgroundColor:"#424549",
                margin:"5px"
            }}>
                {duration === 0 ? <></> :
                    <div className="progress-bar-inside" style={{
                        width:`${100 * (elapsed / duration)}%`,
                        height:"5px",
                        backgroundColor:"gray",
                        position:"absolute"
                    }} />
                }
            </div>

            <div>{PlayerUtils.formatTime(elapsed)} / {PlayerUtils.formatTime(duration)}</div>
        </div>
    );
    
}

export default MyProgressBar;
    