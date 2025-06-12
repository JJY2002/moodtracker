package com.mad.moodtrackerproject.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.moodtrackerproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressLevelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressLevelFragment extends Fragment {
    public StressLevelFragment() {
        // Required empty public constructor
    }
    public static StressLevelFragment newInstance() {return new StressLevelFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var viewRoot = inflater.inflate(R.layout.fragment_stress_level, container, false);
        return viewRoot;
    }
}