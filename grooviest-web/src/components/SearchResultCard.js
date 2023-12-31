import API from "../api/API";

import "./Card.css";

/**
 * Formats a Youtube search result, passed as an object with the form:
 * {
 *      "id": youtubeID,
 *      "name": name,
 *      "channel": channel name,
 *      "thumbnail": link to thumbnail,
 *      "duration": duration
 * } 
 * @param {YoutubeSearchResultItem} result The search result object formatted as specified above
 * @param {function} setSearchResults Function callback for when an "add" button is clicked, to clear
 *                                    the search results from the screen
 * @param {integer} index Which number in the list this item corresponds to. Used for alternating colors.
 */
const SearchResultCard = ({result, removeSearchResultsCallback, index}) => {

    /**
     * Either "odd" or "even" depending on where the card is in the list.
     */
    const identifier = (index % 2 === 1) ? "even" : "odd";


    // Called when "add song" button is pressed.
    // Removes search results from the screen.
    const addSongCallback = (command) => {
        API.sendCommand("add/" + command);
        removeSearchResultsCallback(null);
    }

    
    return (
        <div id={identifier} className={"search-result-card"}>
 
            <div className="left-align-wrapper">
                <img className="thumbnail" src={result.thumbnail} alt="thumbnail" width="120" height="90"/>
                <div className="id-wrapper">
                    <div className="name">{result.name}</div>
                    <div className="channel">{result.channel}</div>
                </div>
            </div>
            <button onClick={() => { addSongCallback(result.id) }}>+</button>

        </div>
    )
}

export default SearchResultCard;
