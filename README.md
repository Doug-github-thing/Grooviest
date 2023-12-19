# Grooviest
A discord bot that plays music on demand, and a web interface to improve user experience.

## Current status

Run the app with mvn exec:java.

User has to join a voice channel, and enter `-join` in any text channel. The bot will join that voice channel.

Once this is done, the bot is now pointing to that voice channel.

The bot can be told to leave or rejoin the channel with a POST request at `bot_server_address/bot_api/:param` where `:param` is either `leave`, `join`, or `play`.