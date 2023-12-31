import API from "../api/API";

/**
 * Formats a Youtube search result, passed as an object with the form:
 * {
 *      "id": youtubeID,
 *      "title": title,
 *      "channel": channel name,
 *      "thumbnail": link to thumbnail,
 *      "duration": duration
 * } 
 * @param {YoutubeSearchResultItem} result The search result object formatted as specified above
 * @param {function} setSearchResults Function callback for when an "add" button is clicked, to clear
 *                                    the search results from the screen
 */
const SearchResultCard = ({result, removeSearchResultsCallback}) => {

    // Called when "add song" button is pressed.
    // Removes search results from the screen.
    const addSongCallback = (command) => {
        API.sendCommand("add/" + command);
        removeSearchResultsCallback(null);
    }

    return (
        <div className="search-result-card">
            
            <img className="thumbnail" src={result.thumbnail} />
            <div className="name">Name: {result.name}</div>
            <div className="url" alt="thumbnail">channel: {result.channel}</div>
            <button onClick={() => { addSongCallback(result.id) }}>+</button>

        </div>
    )
}

export default SearchResultCard;
