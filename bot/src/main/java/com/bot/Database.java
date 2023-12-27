package com.bot;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/**
 * Defines and controls connection to the Firebase Realtime Database.
 * https://firebase.google.com/docs/admin/setup
 */
public class Database {

    /**
     * Prefix for console logs which identify that a log belongs to this component.
     */
    private String logContext = "Firebase DB";

    /**
     * Holds the current FirebaseDatabase instance used by the whole class.
     * Gets initilized in the constructor before being called.
     */
    private FirebaseDatabase db;

    /**
     * Holds a database reference to the Location parameter.
     * Gets initilized in the constructor before being called.
     */
    private DatabaseReference locationRef;

    /**
     * Holds a database reference to the Songs list.
     * Gets initilized in the constructor before being called.
     */
    private DatabaseReference songsRef;

    /**
     * Creates a Database object, which encapsulates Firebase Realtime Database
     * connection, and defines helper methods to facilitate CRUD commands.
     */
    public Database() {

        try {
            FileInputStream serviceAccount = new FileInputStream("bot-database-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://grooviest-bot-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);

            Logging.log(logContext, "Initialized database");

        } catch (Exception e) {
            Logging.log(logContext, "Error initilizaing database:");
            e.printStackTrace();
            FirebaseApp.initializeApp();
        }

        // At this point, the app should be initilized
        db = FirebaseDatabase.getInstance();
        locationRef = db.getReference("location");
        songsRef = db.getReference("songs");
    }

    /**
     * Checks the firebase database for the value of the Location variable.
     * Uses CompletableFuture class to handle async aspect of database querying.
     * 
     * @return String content of the Location variable.
     */
    public String getLocationValue() {
        return getValue("location");
    }

    /**
     * Checks the firebase database for the value of the specified variable.
     * Uses CompletableFuture class to handle async aspect of database querying.
     * 
     * @param key The value to be queried.
     * @return String content of the specified key variable.
     */
    public String getValue(String key) {
        CompletableFuture<String> future = new CompletableFuture<>();

        DatabaseReference ref = db.getReference(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String locationValue = dataSnapshot.getValue(String.class);
                future.complete(locationValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(
                        new RuntimeException("Error reading location value: " + databaseError.getMessage()));
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error getting value";
    }

    /**
     * Change the value of Location in the database to the specified String.
     * 
     * @param newValue New String value with which to replace the old value.
     */
    public void setLocationValue(String newValue) {

        locationRef.setValue(newValue, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newValue);
                return;
            }
            Logging.log(logContext, "Error updating location to: " + newValue + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Adds a new String Key/Value pair to the root of the database.
     * Updates it if the value newKey was already in the database.
     * 
     * @param newKey   Key of the new entry.
     * @param newValue Value of the new entry.
     */
    public void addEntry(String newKey, String newValue) {

        DatabaseReference ref = db.getReference(newKey);
        ref.setValue(newValue, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newValue);
                return;
            }
            Logging.log(logContext,
                    "Error updating " + newKey + " to: " + newValue + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Given a youtube ID of a song, adds to the given URL to the end of the queue.
     * 
     * @param url The Youtube ID of the song to add
     */
    public void addSong(String url) {

        // Query the Youtube API to get the song name
        String name = YoutubeAPI.getVideoName(url);

        // Figure out what position value to assign to this song by getting a list of
        // all songs, and counting how many there are.
        int position = getSongs().size();
        DatabaseReference newSongRef = db.getReference("songs/" + position);

        // Create the new song object to add.
        Song newSong = new Song(position, name, url);

        newSongRef.setValue(newSong, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newSong);
                return;
            }
            Logging.log(logContext,
                    "Error updating songs/" + position + " to: " + newSong + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Attempts to remove the song at a given position.
     * 
     * @param position The position of the song to be removed.
     */
    public void removeSong(int position) {
        Logging.log(logContext, "Deleting song at position " + position);
        DatabaseReference deleteSongRef = db.getReference("songs/" + position);
        deleteSongRef.removeValueAsync();
    }

    /**
     * Checks the database for a current list of songs.
     * 
     * @return An ArrayList of Song objects containing each song.
     */
    public ArrayList<Song> getSongs() {
        CompletableFuture<ArrayList<Song>> futureSongs = new CompletableFuture<>();

        songsRef.orderByChild("position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // New arrayList to return.
                ArrayList<Song> mySongs = new ArrayList<Song>();

                // Reads the songs. If it's an ArrayList like it should be,
                // sets them up to be processed as Song objects.
                Object genericSongs = dataSnapshot.getValue();

                // Songs should be returned as an ArrayList or a HashMap.
                // If ArrayList:
                if (genericSongs instanceof ArrayList) {
                    // Since we now know genericSongs IS an arraylist, convert each
                    // item into a Song format.
                    ArrayList<Object> objectSongs = (ArrayList<Object>) genericSongs;

                    for (Object item : objectSongs) {

                        // There can be gaps in the "position" numbers.
                        // Ignore those when considering what songs are in the queue.
                        if (item == null)
                            continue;

                        // Assert this this is a HashMap, which it will be since the
                        // song data is stored in a JSON Object
                        HashMap<String, Object> thisMap = (HashMap) item;
                        String name = (String) thisMap.get("name");
                        String url = (String) thisMap.get("url");

                        String stringPosition = thisMap.get("position").toString();
                        int position = Integer.parseInt(stringPosition);

                        Song thisSong = new Song(position, name, url);
                        mySongs.add(thisSong);
                    }
                }
                // If genericSongs were returned as a map instead of a list
                else if (genericSongs instanceof HashMap) {
                    // Since we know it's a hashmap of hashmaps, make it an arrayList instead!
                    Collection<Object> songsMapCollection = ((HashMap) genericSongs).values();
                    ArrayList<Object> objectSongs = new ArrayList<>(songsMapCollection);

                    for (Object item : objectSongs) {

                        // There can be gaps in the "position" numbers.
                        // Ignore those when considering what songs are in the queue.
                        if (item == null)
                            continue;

                        // Assert this this is a HashMap, which it will be since the
                        // song data is stored in a JSON Object
                        HashMap<String, Object> thisMap = (HashMap) item;
                        String name = (String) thisMap.get("name");
                        String url = (String) thisMap.get("url");

                        String stringPosition = thisMap.get("position").toString();
                        int position = Integer.parseInt(stringPosition);

                        Song thisSong = new Song(position, name, url);
                        mySongs.add(thisSong);
                    }
                }

                futureSongs.complete(mySongs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logging.log(logContext, "Error accessing songs in the database: " + databaseError.getDetails());
                futureSongs.completeExceptionally(
                        new RuntimeException("Error reading songs: " + databaseError.getMessage()));
            }
        });

        // By the time the code reaches here, the songs should have been parsed
        // to a CompletableFuture containing the ArrayList of songs.

        // Use .get() to complete the Future list and return
        try {
            return futureSongs.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Song>();
    }

}
