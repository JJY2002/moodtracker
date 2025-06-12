package com.mad.moodtrackerproject;

import java.io.Serializable;
import java.util.Date;

public class Mood implements Serializable {
    public String userId;
    public int mood;
    public String note;
    public String ambientLight;
    public Date dateTime; // Firestore stores this as a Timestamp

    public Mood() {
        // Required empty constructor for Firestore
    }

    public Mood(String userId, int mood, String note, String ambientLight) {
        this.userId = userId;
        this.mood = mood;
        this.note = note;
        this.ambientLight = ambientLight;
        this.dateTime = new Date(); // Current date and time
    }
}
