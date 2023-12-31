import CommandButton from "./CommandButton";
import "./Card.css";

/**
 * Song, passed as an object with the form:
 * {
 *      "url": youtubeID,
 *      "name": name,
 *      "channel": channel name,
 *      "thumbnail": link to thumbnail
 * } 
 * @param {YoutubeSearchResultItem} song The song object formatted as specified above
 * @param {integer} index Which number in the list this item corresponds to. Used for alternating colors.
 */
const QueueCard = ({song, index}) => {

    /**
     * Either "odd" or "even" depending on where the card is in the list.
     */
    const identifier = (index % 2 === 1) ? "even" : "odd";

    
    return (
        <div id={identifier} className={"search-result-card"}>
 
            <div className="left-align-wrapper">
                <img className="thumbnail" src={song.thumbnail} alt="thumbnail" width="120" height="90"/>
                <div className="id-wrapper">
                    <div className="name">{song.name}</div>
                    <div className="channel">{song.channel}</div>
                </div>
            </div>
            <CommandButton className="button" text="-" command={`remove/${song.position}`}/>

        </div>
    )
}

export default QueueCard;
