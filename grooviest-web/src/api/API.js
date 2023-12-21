/**
 * Defines this app's interactions with the Bot's Web service
 */
export default class API {

    /**
     * Sends a one liner POST command at the specified URL/bot_api/{command}
     * @param command name of the command, ie "leave" or "join"
     */
    static sendCommand = (command) => {
        const resource = `https://${process.env.REACT_APP_BOT_ADDRESS}:25566/bot_api/${command}`;
        console.log(resource);
        
        fetch(resource, {
            method: "POST"
        }).then(res => { 
            console.log(`Response from bot: ${res.status}, ${res.statusText}`);
        });
    }
}