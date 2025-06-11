package com.mad.moodtrackerproject.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.mad.moodtrackerproject.R;

import java.util.ArrayList;

public class MainMenuFragment extends Fragment {

    private MainViewModel mViewModel;
    private LineChart lineChart;

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
        var desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);

        var emoji = "\uD83D\uDE21, \uD83D\uDE10, \uD83D\uDE42, \uD83D\uDE01, \uD83D\uDE04";

        XAxis x = lineChart.getXAxis();
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
        dataset.setLineWidth(2f);
        dataset.setColor(Color.parseColor("#6b14c2"));
        dataset.setCircleColor(Color.parseColor("#6b14c2"));
        dataset.setDrawValues(false);
        lineChart.setData(new LineData(dataset));

        return rootView;
    }

}