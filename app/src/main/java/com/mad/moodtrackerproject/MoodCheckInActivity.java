package com.mad.moodtrackerproject;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

public class MoodCheckInActivity extends AppCompatActivity implements SensorEventListener {
    // Mood emoji buttons (TextViews)
    private TextView mood1Btn, mood2Btn, mood3Btn, mood4Btn, mood5Btn;
    // Note input
    private EditText noteEditText;
    // Save button
    private Button saveMoodBtn;
    private int mood = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private float currentLightLevel = -1; // Default if sensor not available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mood_check_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();

        //Select mood
        mood1Btn.setOnClickListener(v -> {
            mood = 1;
            resetMoodButtonOpacities();
        });
        mood2Btn.setOnClickListener(v -> {
            mood = 2;
            resetMoodButtonOpacities();
        });
        mood3Btn.setOnClickListener(v -> {
            mood = 3;
            resetMoodButtonOpacities();
        });
        mood4Btn.setOnClickListener(v -> {
            mood = 4;
            resetMoodButtonOpacities();
        });
        mood5Btn.setOnClickListener(v -> {
            mood = 5;
            resetMoodButtonOpacities();
        });

        saveMoodBtn.setOnClickListener(v -> {
            String lightLevel = (currentLightLevel >= 0) ? currentLightLevel + " lx" : "Unavailable";
            var userMood = new Mood(mAuth.getCurrentUser().getUid(), mood, noteEditText.getText().toString(), lightLevel);
            db.collection("mood").add(userMood);
            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            finish();
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mood1Btn = findViewById(R.id.mood1Btn);
        mood2Btn = findViewById(R.id.mood2Btn);
        mood3Btn = findViewById(R.id.mood3Btn);
        mood4Btn = findViewById(R.id.mood4Btn);
        mood5Btn = findViewById(R.id.mood5Btn);
        noteEditText = findViewById(R.id.note);
        saveMoodBtn = findViewById(R.id.saveMoodBtn);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "Light sensor not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetMoodButtonOpacities() {
        mood1Btn.setAlpha(0.5f);
        mood2Btn.setAlpha(0.5f);
        mood3Btn.setAlpha(0.5f);
        mood4Btn.setAlpha(0.5f);
        mood5Btn.setAlpha(0.5f);
        switch (mood) {
            case 1: mood1Btn.setAlpha(1.0f);
            break;
            case 2: mood2Btn.setAlpha(1.0f);
            break;
            case 3: mood3Btn.setAlpha(1.0f);
            break;
            case 4: mood4Btn.setAlpha(1.0f);
            break;
            default: mood5Btn.setAlpha(1.0f);
            break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLightLevel = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}