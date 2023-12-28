import { useState } from "react";

// To send commands to the bot API.
import API from "../api/API";
import CommandButton from "./CommandButton";

const TextForm = ({ label }) => {

    const [searchTerms, setSearchTerms] = useState("");
    const [searchResults, setSearchResults] = useState(null);

    // When the search button is pressed
    const handleSubmit = async (event) => {
        event.preventDefault(); // Do not refresh the page

        const searchResults = await API.youtubeSearch(searchTerms);
        setSearchResults(searchResults.items); 

        setSearchTerms(""); // Clear search terms once searched
    }

    return (
        <div>
            {/* Search bar */}
            <form onSubmit={handleSubmit}>
                <label>
                    {label}
                    <input type="text" name="youtube" value={searchTerms}
                        onChange={(e) => setSearchTerms(e.target.value)} />
                </label>
                <input type="submit" value="Search" />
            </form>

            {/* Search Results */}
            {searchResults == null ? <></> :
            searchResults.map((video, index) => (
                <div className="video-wrapper" key={index}>
                    <div className="id">
                        <div className="name">Name: {video.snippet.title}</div>
                        <div className="url">channel: {video.snippet.channelTitle}</div>
                    </div>
                    <CommandButton className="button" text="+" command={`add/${video.id.videoId}`}/>
                </div>
            ))}
        </div>
    );
};

export default TextForm;