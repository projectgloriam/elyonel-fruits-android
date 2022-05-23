package com.projectgloriam.elyonelfruits.model;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.List;

public class Order {
    //User ID
    private String uid;

    @ServerTimestamp
    private FieldValue timestamp;
    private boolean purchased;
    private boolean delivered;
    private String phone;
    private Place place;
    //See https://developers.google.com/places/android-sdk/place-details for more details

    public Order() {}

    public Order(String uid) {
        this.uid = uid;

        // generate timestamp
        this.timestamp = FieldValue.serverTimestamp();
        this.purchased = false;
        this.delivered = false;
    }

    public String getPhone(){ return phone; }

    public Place getPlace(){ return place; }

    public String getUid() { return uid;  }

    @ServerTimestamp
    public FieldValue getTimestamp(){ return timestamp; }

    public boolean isPurchased() {
        return purchased;
    }

    public boolean isDelivered() {
        return delivered;
    }

}
