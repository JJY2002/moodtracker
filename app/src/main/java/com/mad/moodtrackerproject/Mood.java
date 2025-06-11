package com.mad.moodtrackerproject;

import java.io.Serializable;
import java.util.Date;

public class Mood implements Serializable {
    public String userId;
    public int mood;
    public String note;
    public Date dateTime; // Firestore stores this as a Timestamp

    public Mood() {
        // Required empty constructor for Firestore
    }

    public Mood(String userId, int mood, String note) {
        this.userId = userId;
        this.mood = mood;
        this.note = note;
        this.dateTime = new Date(); // Current date and time
    }
}
