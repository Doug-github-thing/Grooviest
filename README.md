# Grooviest
A discord bot that plays music on demand, and a web interface to improve user experience.

## Current status

Run the app with mvn exec:java.

User has to join a voice channel, and enter `-join` in any text channel. The bot will join that voice channel.

Once this is done, the bot is now pointing to that voice channel.

The bot can be told to leave or rejoin the channel with a POST request at `bot_server_address/bot_api/:param` where `:param` is either `leave`, `join`, or `play`.

## Installing bot on a clean Raspberry Pi

Clone this git repo onto the pi. Install maven, pm2, nginx core.

### Java Bot Environment

1. Bot relies on a a `bot-database-account.json` file in the `bot/` folder, the root of the maven project.

    The file looks like this:
    
    ```json
    {
        "type": "service_account",
        "project_id": "grooviest-bot",
        "private_key_id": "<private key id from console.cloud.google.com/iam-admin/serviceaccounts>",
        "private_key": "-----BEGIN PRIVATE KEY-----\n<Very long private key>\n-----END PRIVATE KEY-----\n",
        "client_email": "grooviest-bot-database-account@grooviest-bot.iam.gserviceaccount.com",
        "client_id": "<client_id>",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/grooviest-bot-database-account%40grooviest-bot.iam.gserviceaccount.com",
        "universe_domain": "googleapis.com"
    }

    ```

    Get the values for private key related stuff for the firebase service account stuff from Google cloud IAM console. [Google Service Account console](https://console.cloud.google.com/iam-admin/serviceaccounts). Good luck, they make this very difficult to find.

1. The bot also requires passing the discord token and youtube API keys through the command line at runtime. This can be done with a makefile. This makefile is not included in the repo due to containing sensitive tokens:

    ```makefile
    DISCORD_TOKEN="<discord token for your bot goes here. get this from the discord bot developer console>"
    YOUTUBE_API_KEY="<youtube api key goes here>"

    run:
        DISCORD_TOKEN=${DISCORD_TOKEN} YOUTUBE_API_KEY=${YOUTUBE_API_KEY} mvn exec:java

    build:
        mvn clean
        mvn package

    all:
        mvn clean install
        DISCORD_TOKEN=${DISCORD_TOKEN} YOUTUBE_API_KEY=${YOUTUBE_API_KEY}  mvn exec:java
```

### Daemon management

1. Install pm2 to manage bot uptime.

1. `pm2 start --name "Grooviest_daemon" "make all"` to run the bot as a new background process.

    - Relies on the above defined makefile

    - Access bot debugging logs with `pm2 log`

1. Check status with `pm2 status`. If running, bot should be visible as "online" in Discord.

1. Run `pm2 save`

1. Once working as intended, use `pm2 startup` and follow terminal instructions to get it to restart the bot on pi reboot.

### Configure NGINX for forwarding www https traffic to the bot

1. Install nginx core
1. Setup certbot to configure ssl certificates: [https://certbot.eff.org/instructions?ws=nginx&os=debianbuster](https://certbot.eff.org/instructions?ws=nginx&os=debianbuster)
1. Configure incoming 443 traffic to redirect via proxy to localhost at port 25566

    Edit `/etc/nginx/sites-available/default` to include the following context:

```
server {

    server_name <bot_web_address>; # managed by Certbot

    location / {
        proxy_pass http://localhost:25566;
    }

    listen [::]:443 ssl ipv6only=on; # managed by Certbot
    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/grooviest.duckdns.org/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/grooviest.duckdns.org/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot
}
```
