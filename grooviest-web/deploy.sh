#!/bin/bash

echo "Running npm run build"
npm run build

echo "Deploying to firebase"
firebase deploy
