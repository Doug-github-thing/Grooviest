import { useState } from "react";

// To send commands to the bot API.
import API from "../api/API";

const TextForm = ({ label }) => {

    const [search, setSearch] = useState("");

    const handleSubmit = (event) => {
        event.preventDefault();
        setSearch(event.target.value);
        API.sendCommand(`add/${search}`);
        setSearch("");
    }

    return (
        <form onSubmit={handleSubmit}>
            <label>
                {label}
                <input type="text" name="youtube" value={search}
                    onChange={(e) => setSearch(e.target.value)}/>
            </label>
            <input type="submit" value="+" />
        </form>
    );
};

export default TextForm;