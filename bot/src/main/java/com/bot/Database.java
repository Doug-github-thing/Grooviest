package com.bot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.*;

/**
 * Defines and controls connection to the Firebase Realtime Database.
 * https://firebase.google.com/docs/admin/setup
 */
public class Database {

    public Database() {

        try {
            FileInputStream serviceAccount = new FileInputStream("bot-database-account.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://grooviest-bot-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);

            Logging.log("Firebase DB", "Initialized database");

        } catch (Exception e) {
            Logging.log("Firebase DB", "Error initilizaing database:");
            e.printStackTrace();
            FirebaseApp.initializeApp();
        }
    }
}
