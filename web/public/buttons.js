const bot_url = "http://" + process.env.BOT_ADDRESS + ":25566"

const sendCommand = (content) => {
    // console.log(`Sending POST at ${bot_url}/bot_api/${content}`);

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
