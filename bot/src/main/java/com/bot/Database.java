package com.bot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

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
     * Holds a database reference to the Bread parameter.
     * Gets initilized in the constructor before being called.
     */
    private DatabaseReference locationRef;

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
    }

    /**
     * Checks the firebase database for the value of the Location variable.
     * Uses CompletableFuture class to handle async aspect of database querying.
     * 
     * @return String content of the Location variable.
     */
    public String getLocationValue() {
        CompletableFuture<String> future = new CompletableFuture<>();

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String locationValue = dataSnapshot.getValue(String.class);
                future.complete(locationValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(
                        new RuntimeException("Error reading bread value: " + databaseError.getMessage()));
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

    public void addEntry(String newKey, String newValue) {

        Logging.log(logContext, "Attempting to add new entry: " + newKey + ", " + newValue);

        DatabaseReference ref = db.getReference(newKey);

        ref.setValue(newValue, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Logging.log(logContext, databaseReference.getKey() + " updated to " + newValue);
                return;
            }
            Logging.log(logContext, "Error updating location to: " + newValue + ".\n" + databaseError.getMessage());
        });
    }

}
