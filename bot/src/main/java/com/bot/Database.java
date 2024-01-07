package com.bot;

import static spark.Spark.delete;

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
import java.util.Collections;

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
     * Checks the firebase database for the current "now_playing"
     * 
     * @return Song object corresponding to the value in the "now_playing" key
     */
    public Song getNowPlaying() {
        CompletableFuture<Song> future = new CompletableFuture<>();

        DatabaseReference ref = db.getReference("now_playing");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Song locationValue = dataSnapshot.getValue(Song.class);
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
        return null;
    }

    /**
     * Change the value of "now_playing" in the database to the specified Song.
     */
    public void setNowPlaying(Song newSong) {
        DatabaseReference nowPlayingRef = db.getReference("now_playing");
        nowPlayingRef.setValue(newSong, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newSong);
                return;
            }
            Logging.log(logContext, "Error updating location to: " + newSong + ".\n" + databaseError.getMessage());
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
     * Adds a new String Key/Value pair to the root of the database.
     * Updates it if the value newKey was already in the database.
     * 
     * @param newKey           Key of the new entry.
     * @param newValue         Value of the new entry.
     * @param completeSilently Whether or not the method should execute without
     *                         printing to STDOUT
     */
    public void addEntry(String newKey, String newValue, boolean completeSilently) {
        DatabaseReference ref = db.getReference(newKey);
        ref.setValue(newValue, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                if (!completeSilently)
                    Logging.log(logContext, databaseReference.getKey() + " updated to " + newValue);
                return;
            }
            Logging.log(logContext,
                    "Error updating " + newKey + " to: " + newValue + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Takes a song object, packages it up, and adds it to the "now_playing"
     * key in the database. Used to communicate with the frontend with player data.
     * 
     * @param song The Song object to add.
     */
    public void addNowPlaying(Song song) {
        DatabaseReference ref = db.getReference("now_playing");
        ref.setValue(song, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + song);
                return;
            }
            Logging.log(logContext,
                    "Error updating 'now_playing' to: " + song + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Given a youtube ID of a song, adds to the given URL to the end of the queue.
     * 
     * @param url The Youtube ID of the song to add
     */
    public void addSong(String url) {

        // Figure out what position value to assign to this song by getting a list of
        // all songs, and finding the first value which not already in use.
        ArrayList<Song> allSongs = getSongs();
        ArrayList<Integer> positions = new ArrayList<Integer>();

        // Get list of all in use positions
        for (Song song : allSongs) {
            positions.add(song.getPosition());
        }

        // Find a not yet in use position to add use.
        // By the time this loop breaks, "pos" is a value not in use
        int pos = 0;
        for (; pos < positions.size(); pos++) {
            if (pos != positions.get(pos).intValue())
                break;
        }

        // Query the Youtube API to get the song name
        Song newSong = YoutubeAPI.getSongData(url);
        newSong.setPosition(pos);

        // Add the newly generated song object to the database
        DatabaseReference newSongRef = db.getReference("songs/" + pos);
        newSongRef.setValue(newSong, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newSong);
                return;
            }
            Logging.log(logContext,
                    "Error updating song to " + newSong + ".\n" + databaseError.getMessage());
        });
    }

    /**
     * Attempts to remove the song at a given position.
     * Adjusts the positions of each song after removal.
     * 
     * @param position The position of the song to be removed.
     */
    public void removeSong(int position) {

        // Create a local representation of the queue as an ArrayList
        ArrayList<Song> allSongs = getSongs();

        // Remove specified song from local list, update each song with new positions
        ArrayList<Song> updatedSongs = new ArrayList<Song>();
        int i = 0;
        for (Song thisSong : allSongs)
            if (thisSong.getPosition() != position) {
                thisSong.setPosition(i);
                updatedSongs.add(thisSong);
                i++;
            }

        // Delete all songs from the queue
        DatabaseReference allSongsRef = db.getReference("songs");
        allSongsRef.removeValueAsync();

        // Re-populate the database queue with the updated local list
        i = 0;
        for (Song thisSong : updatedSongs) {
            DatabaseReference thisSongRef = db.getReference("songs/" + i);
            thisSongRef.setValue(thisSong, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Logging.log(logContext, databaseReference.getKey() + " updated to " + thisSong);
                    return;
                }
                Logging.log(logContext,
                        "Error updating song to " + thisSong + ".\n" + databaseError.getMessage());
            });
            i++;
        }
    }

    /**
     * Attempts to move the specified song to a new position in the queue.
     * Adjusts the positions of each song after removal.
     * 
     * @param initialPos The position of the song to be moved.
     * @param newPos     The position in queue where the new song is to be moved.
     */
    public void moveSong(int initialPos, int newPos) {

        // Create a local representation of the queue as an ArrayList
        ArrayList<Song> allSongs = getSongs();

        // Adjusts if new position is above bounds
        if (newPos >= allSongs.size())
            newPos = allSongs.size() - 1;

        // Does nothing if there's no change to be made
        if (initialPos == newPos) {
            Logging.log(logContext, "Nothing to move, new position is the same as original");
            return;
        }

        // Check if params are within the range of the queue length, else do nothing
        if (initialPos >= allSongs.size() || initialPos < 0
                || newPos >= allSongs.size() || newPos < 0) {
            Logging.log(logContext, "Invalid range when attempting to move song " + initialPos + " to " + newPos
                    + " with queue length " + allSongs.size());
            return;
        }

        Song movingSong = allSongs.get(initialPos);

        // If it needs to move up, move up until the index of the moving song is
        while (allSongs.indexOf(movingSong) != newPos) {

            int index = allSongs.indexOf(movingSong);

            if (initialPos > newPos)
                // If the song needs to move up in the queue, swap it up until it gets there
                Collections.swap(allSongs, index, index - 1);
            else if (initialPos < newPos)
                // Else, if song needs to move down in queue, swap it until it gets there
                Collections.swap(allSongs, index, index + 1);
        }
        Logging.log(logContext, "Song " + movingSong.getName() + " moved up to position " + newPos);

        // Delete all songs from the queue
        DatabaseReference allSongsRef = db.getReference("songs");
        allSongsRef.removeValueAsync();

        // Re-populate the database queue with the updated local list
        int i = 0;
        for (Song thisSong : allSongs) {

            // Also makes sure each song has the proper position assigned before adding
            thisSong.setPosition(i);

            // Adds the song to the database
            DatabaseReference thisSongRef = db.getReference("songs/" + i);
            thisSongRef.setValue(thisSong, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Logging.log(logContext, databaseReference.getKey() + " updated to " + thisSong);
                    return;
                }
                Logging.log(logContext,
                        "Error updating song to " + thisSong + ".\n" + databaseError.getMessage());
            });
            i++;
        }
    }

    /**
     * Checks the database for a current list of songs.
     * 
     * @return An ArrayList of Song objects containing each song.
     */
    public ArrayList<Song> getSongs() {
        CompletableFuture<ArrayList<Song>> futureSongs = new CompletableFuture<>();

        DatabaseReference songsRef = db.getReference("songs");
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

                        if (item == null)
                            continue;

                        // Assert this this is a HashMap, which it will be since the
                        // song data is stored in a JSON Object
                        HashMap<String, Object> thisMap = (HashMap) item;
                        String name = (String) thisMap.get("name");
                        String channel = (String) thisMap.get("channel");
                        String url = (String) thisMap.get("url");

                        String stringPosition = thisMap.get("position").toString();
                        int position = Integer.parseInt(stringPosition);

                        Song thisSong = new Song(position, name, channel, url);
                        mySongs.add(thisSong);
                    }
                }
                // If genericSongs were returned as a map instead of a list
                else if (genericSongs instanceof HashMap) {
                    // Since we know it's a hashmap of hashmaps, make it an arrayList instead!
                    Collection<Object> songsMapCollection = ((HashMap) genericSongs).values();
                    ArrayList<Object> objectSongs = new ArrayList<>(songsMapCollection);

                    for (Object item : objectSongs) {

                        if (item == null)
                            continue;

                        // Assert this this is a HashMap, which it will be since the
                        // song data is stored in a JSON Object
                        HashMap<String, Object> thisMap = (HashMap) item;
                        String name = (String) thisMap.get("name");
                        String channel = (String) thisMap.get("channel");
                        String url = (String) thisMap.get("url");

                        String stringPosition = thisMap.get("position").toString();
                        int position = Integer.parseInt(stringPosition);

                        Song thisSong = new Song(position, name, channel, url);
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
