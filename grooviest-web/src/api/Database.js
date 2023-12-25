/**
 * Defines this app's interactions with Firebase's Realtime Database
 */
import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";

const firebaseConfig = {
  apiKey: "AIzaSyCRGvzTsVU7PLkSnddisezX5qwuzDoIUmY",
  authDomain: "grooviest-bot.firebaseapp.com",
  projectId: "grooviest-bot",
  storageBucket: "grooviest-bot.appspot.com",
  messagingSenderId: "270103660058",
  appId: "1:270103660058:web:577dbf91f8599615e533a3",
  databaseURL: "https://grooviest-bot-default-rtdb.firebaseio.com",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);


// Initialize Realtime Database and get a reference to the service
const database = getDatabase(app);

export default database;