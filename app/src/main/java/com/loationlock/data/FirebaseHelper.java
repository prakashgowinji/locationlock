package com.loationlock.data;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper INSTANCE = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference locationNode;

    public static FirebaseHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseHelper();
        }
        return INSTANCE;
    }

    /**
     * Init FireBase Database
     */
    private FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    /**
     * Initializes the database with required flags. Preferably call from application level.
     */
    public void init() {
        FirebaseDatabase database = getFirebaseDatabase();
        locationNode = database.getReference(DataConstants.LOCATION_NODE_NAME);
        locationNode.keepSynced(true);
    }

    public void pushLocationInfo(LocationLoc locationLoc) {
        //String key = locationNode.child(DataConstants.LOCATION_NODE_NAME).push().getKey();
        String key = locationNode.push().getKey();
        System.out.println("XXX Key: "+ key);
        Map<String, Object> postValues = locationLoc.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        locationNode.updateChildren(childUpdates);
    }
}
