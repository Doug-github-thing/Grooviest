#!/usr/bin/bash

docker build -t lavalink-bot .
docker stop lavalink-bot
docker rm lavalink-bot
docker run -d --name lavalink-bot --env-file .env -p 2333:2333 lavalink-bot
