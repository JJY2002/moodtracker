package com.mad.moodtrackerproject.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mad.moodtrackerproject.MainMenuActivity;
import com.mad.moodtrackerproject.R;

import java.util.Random;

public class MotivationalQuoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_motivational_quote);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.motiLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView quote = findViewById(R.id.quoteText);

        int[] backgrounds = {
                R.drawable.gradient_bg,
                R.drawable.gradient_bg1,
                R.drawable.gradient_bg2
        };
        int randomIndex = new Random().nextInt(backgrounds.length);

        // Random quote
        String[] quotes = {
                "Believe you can and you're halfway there.",
                "Every emotion is a message. Listen, don't ignore.",
                "Tracking your mood is the first step to understanding your story.",
                "Not every day is good, but there's something good in every day.",
                "Your feelings are valid. Let's explore them together."
        };
        int randomQuoteIndex = new Random().nextInt(quotes.length);
        quote.setText(quotes[randomQuoteIndex]);

        ConstraintLayout layout = findViewById(R.id.motiLayout);
        layout.setBackgroundResource(backgrounds[randomIndex]);
        layout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            finish();
        });
    }
}