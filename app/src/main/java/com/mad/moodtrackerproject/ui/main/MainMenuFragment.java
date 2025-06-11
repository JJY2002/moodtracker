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
import com.mad.moodtrackerproject.Mood;
import com.mad.moodtrackerproject.MoodCheckInActivity;
import com.mad.moodtrackerproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainMenuFragment extends Fragment {

    private MainViewModel mViewModel;
    private LineChart lineChart;
    private Button moodBtn;

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

        /*XAxis x = lineChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter(new IndexAxisValueFormatter(new String[] {"Mon","Tue","Wed","Thu","Fri","Sat"}));
        x.setGranularity(1f);
        x.setLabelCount(6, true);
        x.setDrawGridLines(false);

        YAxis y = lineChart.getAxisLeft();
        y.setMinWidth(0f);
        y.setDrawGridLines(false);
        y.setDrawLabels(false);

        YAxis right = lineChart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);
        right.setDrawAxisLine(false);

        var entries = new ArrayList<Entry>();
        entries.add(new Entry(0, 1));
        entries.add(new Entry(1, 4));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 3));
        entries.add(new Entry(4, 5));
        entries.add(new Entry(5, 7));

        var dataset = new LineDataSet(entries, "");
        dataset.setLineWidth(3f);
        dataset.setCircleRadius(4f);
        dataset.setColor(Color.parseColor("#7053bf"));
        dataset.setCircleColor(Color.parseColor("#7053bf"));
        dataset.setDrawCircleHole(false);
        dataset.setDrawValues(false);
        dataset.setDrawFilled(true);
        dataset.setFillColor(Color.parseColor("#b8a4e0"));
        lineChart.setData(new LineData(dataset));*/
        db.collection("mood")
                .whereEqualTo("userId", userId)
                .orderBy("dateTime")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Entry> entries = new ArrayList<>();
                    List<String> xLabels = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());

                    int index = 0;
                    for (var doc : queryDocumentSnapshots) {
                        Mood mood = doc.toObject(Mood.class);
                        entries.add(new Entry(index, mood.mood));

                        if (mood.dateTime != null) {
                            xLabels.add(sdf.format(mood.dateTime)); // e.g., "Mon"
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

                    lineChart.setData(new LineData(dataset));

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

                    lineChart.invalidate(); // Refresh chart
                });

        return rootView;
    }

}