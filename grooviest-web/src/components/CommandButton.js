// For sending commands to the Bot Webserver
import API from "../api/API";

const CommandButton = ({ text, command, icon }) => {
    
    const buttonCallback = () => {
        API.sendCommand(command);
    }
    
    return (
        <>
            {icon != null ?
                <button onClick={buttonCallback}>{icon}</button>
                    :
                <button onClick={buttonCallback}>{text}</button>
            }
        </>
    );
}

export default CommandButton;
