package com.mad.moodtrackerproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            var i = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        getUser(mUser.getUid());
    }

    private void getUser(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // Use user.name, user.email, etc.
                            Log.d("FIRESTORE", "User name: " + user.name);
                            Toast.makeText(this, user.name, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        var i = new Intent(MainMenuActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        Log.d("FIRESTORE", "No such user");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Failed to fetch user", e);
                });
    }
}