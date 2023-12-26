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

    public String getBread() {

        String myBreadValue;

        breadRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Logging.log(logContext, dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        return "bwa";
    }
}
