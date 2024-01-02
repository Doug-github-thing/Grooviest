import CommandButton from "./CommandButton";
import "./Card.css";

/**
 * Song, passed as an object with the form:
 * {
 *      "url": youtubeID,
 *      "name": name,
 *      "channel": channel name,
 *      "thumbnail": link to thumbnail,
 *      "duration": total song duration in ms,
 *      "elapsed": elapsed time in ms 
 * } 
 * @param {YoutubeSearchResultItem} song The song object formatted as specified above
 * @param {integer} index Which number in the list this item corresponds to. Used for alternating colors.
 */
const NowPlayingCard = ({song, paused}) => {

    
    return (
        <div id="odd" className={"song-card now-playing-card"}>
 
            <div className="left-align-wrapper">
                <img className="thumbnail" src={song.thumbnail} alt="thumbnail" width="120" height="90"/>
                <div className="id-wrapper">
                    <div className="name">{song.name}</div>
                    <div className="channel">{song.channel}</div>
                </div>
            </div>

            <div className="playback-buttons">

                {paused === "false" ? 
                    <CommandButton text="| |" command="pause" /> 
                        :
                    <CommandButton text="&#9658;" command="play" />
                }

                <CommandButton text="skip" command="skip" />
            </div>
        </div>
    )
}

export default NowPlayingCard;
