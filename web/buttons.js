const bot_url = "http://127.0.0.1:4567"

const sendCommand = (content) => {
    console.log(`Sending POST at ${bot_url}/bot_api/${content}`);

    fetch(`${bot_url}/bot_api/${content}`, {
        method: "POST"
    }).then(res => {
        console.log("Request complete! response:", res);
    });
}

const joinButton = () => {
    sendCommand("join");
};

const leaveButton = () => {
    sendCommand("leave");
};

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("joinButton").addEventListener("click", joinButton);
    document.getElementById("leaveButton").addEventListener("click", leaveButton);
});
