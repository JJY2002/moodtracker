package com.mad.moodtrackerproject.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mad.moodtrackerproject.Mood;
import com.mad.moodtrackerproject.MoodCheckInActivity;
import com.mad.moodtrackerproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainMenuFragment extends Fragment {

    private MainViewModel mViewModel;
    private LineChart lineChart;
    private Button moodBtn;
    private List<Mood> moodList = new ArrayList<>();

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        lineChart = rootView.findViewById(R.id.lineChart);
        moodBtn = rootView.findViewById(R.id.moodCheckInBtn);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("mood")
                .whereEqualTo("userId", userId)
                .orderBy("dateTime", Query.Direction.DESCENDING) // get latest first
                .get()
                .addOnCompleteListener(q -> {
                    var moodList = q.getResult().toObjects(Mood.class);
                    TextView emoji = rootView.findViewById(R.id.moodEmoji);
                    TextView moodType = rootView.findViewById(R.id.moodType);
                    TextView streak = rootView.findViewById(R.id.streakTxt);

                    if (!moodList.isEmpty()) {
                        String[] emojis = {"😡", "🙁", "😐", "🙂", "😄"};
                        String[] moodLabels = {"Angry", "Sad", "Neutral", "Happy", "Joyful"};

                        int latestMood = moodList.get(0).mood;
                        int moodIndex = latestMood - 1;

                        if (moodIndex >= 0 && moodIndex < emojis.length) {
                            emoji.setText(emojis[moodIndex]);
                            moodType.setText(moodLabels[moodIndex]);
                        }

                        // --- Calculate mood streak ---
                        int streakCount = 1;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        String lastDate = sdf.format(moodList.get(0).dateTime);

                        for (int i = 1; i < moodList.size(); i++) {
                            Mood current = moodList.get(i);
                            String currentDate = sdf.format(current.dateTime);

                            // Only count if mood is same and date is exactly 1 day before lastDate
                            try {
                                java.util.Calendar cal1 = java.util.Calendar.getInstance();
                                java.util.Calendar cal2 = java.util.Calendar.getInstance();
                                cal1.setTime(moodList.get(i - 1).dateTime);
                                cal2.setTime(current.dateTime);
                                cal1.add(java.util.Calendar.DATE, -1); // expected streak day

                                boolean sameMood = current.mood == latestMood;
                                boolean oneDayBefore = sdf.format(cal1.getTime()).equals(currentDate);

                                if (sameMood && oneDayBefore) {
                                    streakCount++;
                                } else {
                                    break; // streak broken
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        }

                        streak.setText("Mood Streak: " + streakCount + " day(s)");
                    }
                });


        // Load chart initially
        loadMoodChartData(userId);
        ImageButton refreshBtn = rootView.findViewById(R.id.refreshChart);
        refreshBtn.setOnClickListener(v -> {
            loadMoodChartData(userId); // Reload chart
        });

        moodBtn.setOnClickListener(v -> {
            Intent i = new Intent(rootView.getContext(), MoodCheckInActivity.class);
            startActivity(i);
        });

        var desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);


        var emoji = "\uD83D\uDE21, \uD83D\uDE10, \uD83D\uDE42, \uD83D\uDE01, \uD83D\uDE04";

        LinearLayout moti = rootView.findViewById(R.id.motivateLayout);
        moti.setOnClickListener(v -> {
            startActivity(new Intent(rootView.getContext(), MotivationalQuoteActivity.class));
            requireActivity().finish();
        });
        // Random quote
        String[] quotes = {
                "Believe you can and you're halfway there.",
                "Every emotion is a message. Listen, don't ignore.",
                "Tracking your mood is the first step to understanding your story.",
                "Not every day is good, but there's something good in every day.",
                "Your feelings are valid. Let's explore them together."
        };
        int randomQuoteIndex = new Random().nextInt(quotes.length);
        TextView quote = rootView.findViewById(R.id.menuQuoteTxt);
        quote.setText(quotes[randomQuoteIndex]);


        return rootView;
    }

    private void loadMoodChartData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        var get = db.collection("mood")
                .whereEqualTo("userId", userId)
                .orderBy("dateTime")
                .get();
        get.addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Entry> entries = new ArrayList<>();
            List<String> xLabels = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());

            int index = 0;
            for (var doc : queryDocumentSnapshots) {
                Mood mood = doc.toObject(Mood.class);
                entries.add(new Entry(index, mood.mood));

                if (mood.dateTime != null) {
                    xLabels.add(sdf.format(mood.dateTime));
                } else {
                    xLabels.add("?");
                }

                index++;
            }

            LineDataSet dataset = new LineDataSet(entries, "Mood");
            dataset.setLineWidth(3f);
            dataset.setCircleRadius(4f);
            dataset.setColor(Color.parseColor("#7053bf"));
            dataset.setCircleColor(Color.parseColor("#7053bf"));
            dataset.setDrawCircleHole(false);
            dataset.setDrawValues(false);
            dataset.setDrawFilled(true);
            dataset.setFillColor(Color.parseColor("#b8a4e0"));
            XAxis x = lineChart.getXAxis();
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setGranularity(1f);
            x.setLabelCount(xLabels.size(), true);
            x.setDrawGridLines(false);
            x.setValueFormatter(new IndexAxisValueFormatter(xLabels));

            YAxis y = lineChart.getAxisLeft();
            y.setDrawLabels(false);
            y.setDrawGridLines(false);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.setData(new LineData(dataset));
            lineChart.invalidate();
        });
    }
}