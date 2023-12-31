import { useState } from "react";

// To send commands to the bot API.
import API from "../api/API";
import SearchResultCard from "./SearchResultCard";

const SearchBar = ({ label }) => {

    // The String search terms used.
    const [searchTerms, setSearchTerms] = useState("");
    // Search Results expected to be the form of an Array of search result objects,
    // which contain video metadata. Null if there is an error, empty Array if 0 results.
    const [searchResults, setSearchResults] = useState(null);

    /**
     * When the search button is pressed, query YoutubeAPI, and handle parsing their
     * response into an array of Result objects, or null in the event of an error.
     */
    const handleVideoSearch = async (event) => {
        event.preventDefault(); // Do not refresh the page

        const rawSearchResults = await API.youtubeSearch(searchTerms);
        // console.log("Search resutls: ", rawSearchResults.items);

        // Check if response has expected shape. If not, return
        if (!rawSearchResults.hasOwnProperty("pageInfo") 
            || !rawSearchResults.pageInfo.hasOwnProperty("resultsPerPage")) {
                console.error("Error performing search. Results: ", rawSearchResults);
                setSearchResults(null);
                return;
        }
        
        // Find how many results there are
        const resultsCount = rawSearchResults.pageInfo["resultsPerPage"];
        let resultsArray = new Array(resultsCount);

        // Iterate through them, converting each result into a easy to read object
        for (let i = 0; i < resultsCount; i++) {
            resultsArray[i] = {
                id: rawSearchResults.items[i].id.videoId,
                name: rawSearchResults.items[i].snippet.title,
                channel: rawSearchResults.items[i].snippet.channelTitle,
                thumbnail: rawSearchResults.items[i].snippet.thumbnails.default.url,
                duration: 666
            }
        }
        console.log("Parsed these search results: ", resultsArray);
        
        // Set Results Array, and clear search terms once searched is finished
        setSearchResults(resultsArray);
        setSearchTerms("");
    }

    return (
        <div>
            {/* Search bar */}
            <form onSubmit={handleVideoSearch}>
                <label>
                    {label}
                    <input type="text" name="youtube" value={searchTerms}
                        onChange={(e) => setSearchTerms(e.target.value)} />
                </label>
                <input type="submit" value="Search" />
            </form>

            {/* Search Results list */}
            {searchResults == null ? <></> :
                searchResults.length === 0 ? <>No Search Results</> :
                    searchResults.map((result, index) => (
                        <SearchResultCard key={index} result={result} removeSearchResultsCallback={setSearchResults}/>
                    ))}
        </div>
    );
};

export default SearchBar;