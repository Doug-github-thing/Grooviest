#!/usr/bin/bash

docker build -t lavalink-server .
docker stop lavalink-server
docker rm lavalink-server
docker run -d --name lavalink-server -p 2333:2333 lavalink-server

