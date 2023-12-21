// For sending commands to the Bot Webserver
import API from "../api/API";

const CommandButton = ({ text, command }) => {
    
    const buttonCallback = () => {
        API.sendCommand(command);
    }
    
    return <button onClick={buttonCallback}>{text}</button>;
}

export default CommandButton;
