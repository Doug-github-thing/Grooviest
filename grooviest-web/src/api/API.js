/**
 * Defines this app's interactions with the Bot's Web service
 */
export default class API {

    static bot_address = process.env.REACT_APP_BOT_ADDRESS;
    static youtube_api_key = process.env.REACT_APP_YOUTUBE_API_KEY;
    // static bot_address = "http://127.0.0.1:25566";

    /**
     * Sends a one liner POST command at the specified URL/bot_api/{command}
     * @param command name of the command, ie "leave" or "join"
     */
    static sendCommand = (command) => {
        const resource = `${this.bot_address}/api/${command}`;
        console.log(`Sending '${command}' to ${resource}`);

        fetch(resource, {
            method: "POST"
        }).then(res => {
            console.log(`Response from bot: ${res.status}, ${res.statusText}`);
        });
    }

    static youtubeSearch = async (terms) => {
        const base = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video";
        const search = `&q=${encodeURIComponent(terms)}`;
        const key= `&key=${this.youtube_api_key}`;

        const resource = base + search + key;

        return await fetch(resource, {
            method: "GET"
        }).then(res => {
            return res.json();
        });
    }
}
