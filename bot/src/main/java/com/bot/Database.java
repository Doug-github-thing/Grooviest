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
import java.util.concurrent.ExecutionException;

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
    private DatabaseReference breadRef;

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
        breadRef = db.getReference("bread");
    }

    /**
     * Checks the firebase database for the value of the Bread variable.
     * Access the actual value of this variable by calling
     * getBreadValue().get(). throws ExecutionException, InterruptedException
     * 
     * @return A CompletableFuture<String> promising the value of the Bread
     *         variable.
     */
    public CompletableFuture<String> getBreadValue() {
        CompletableFuture<String> future = new CompletableFuture<>();

        breadRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String breadValue = dataSnapshot.getValue(String.class);
                future.complete(breadValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(
                        new RuntimeException("Error reading bread value: " + databaseError.getMessage()));
            }
        });

        return future;
    }
}
