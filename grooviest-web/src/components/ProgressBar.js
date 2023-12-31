import { useState, useEffect } from "react";
import { ref, getDatabase, onValue } from "firebase/database";

// For displaying ms timestamps as human readable
import PlayerUtils from "../utils/PlayerUtils";
// For sending Seeking requests on bar click
import API from "../api/API";
// For displaying linear progress bar
import "./ProgressBar.css";

/**
 * Tracks how long the current song is, and how long that track has been playing (from the database)
 * 
 * The progress bar itself is two nested divs. They track where the user's mouse is, and when
 * clicked sends a "seek at this point in the song" command to the bot.
 * 
 * @returns A linear progress bar for visualizing how for you are into the current song 
 */
const MyProgressBar = () => {
    // Keeps track of song progress as Numbers
    const [duration, setDuration] = useState(0);
    const [elapsed, setElapsed] = useState(0);
    // For detecting what position on the bar the mouse is hovering
    const [hoveredValue, setHoveredValue] = useState(null);
    // For tracking when the mouse is hovering the bar
    const [isHovered, setIsHovered] = useState(false);

    
    // For visualizing song progress
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

    // For visualizing song progress
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


    // Keeping track of where the mouse is currently seeking
    const handleMouseOver = (event) => {
        setIsHovered(true);

        // If there's no song selected, do nothing
        if (duration === 0) {
            setHoveredValue(0);
            setIsHovered(false);
            return;
        }
        
        // Calculate the value based on the mouse position
        const rect = event.currentTarget.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const totalWidth = rect.width;
        const ms = Math.ceil(duration * (x / totalWidth));
        
        // Update the hovered values
        setHoveredValue(ms);
    };


    return (
        <div className="progress-bar">
            {duration === 0 ? 
                // If there's no song playing, just show an empty bar
                <div className="progress-bar-outside" />
                    :
                // Else, show the whole thing
                <>
                    {/* Current position in song on the left */}
                    <div className="left">{PlayerUtils.formatTime(elapsed)}</div>

                    {/* Progress bar is two nested, colored divs. They get taller while being hovered */}
                    <div className="progress-bar-outside"
                        style={{height: isHovered ? "2.5vmin" : "0.7vmin"}}
                        onMouseMove={handleMouseOver} 
                        onMouseOut={() => {setIsHovered(false)}}
                        onClick={() => {API.sendCommand(`seek/${hoveredValue}`)}}
                    >

                        {/* The bar that moves as the song progresses. A Div which starts from 0% width
                        and fills up to 100% width as the elapsed time approaches the song duration. */}
                        {duration === 0 ? <></> :
                            <div className="progress-bar-inside" 
                                style={{width:`${100 * (elapsed / duration)}%`,
                                    height: isHovered ? "2.5vmin" : "0.7vmin"}}
                                onMouseOut={() => {setIsHovered(false)}}
                                onClick={() => {API.sendCommand(`seek/${hoveredValue}`)}}
                            />
                        }

                        {/* Display the time only while mouse is hovering over the bar */}
                        <div className="mouseover-text"
                            style={{
                                // Conditional CSS rules only triggered while hovered
                                display: isHovered ? "block" : "none",
                                left: `${100 * (hoveredValue / duration)}%`,
                            }}>
                                {PlayerUtils.formatTime(hoveredValue)}
                        </div>

                    </div>

                    {/* Total song duration on the right */}
                    <div className="right">{PlayerUtils.formatTime(duration)}</div>
                </>
            }
        </div>
    );
    
}

export default MyProgressBar;
    