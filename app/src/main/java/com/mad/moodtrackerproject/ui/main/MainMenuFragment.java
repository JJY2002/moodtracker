package com.mad.moodtrackerproject.ui.main;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.mad.moodtrackerproject.MainActivity;
import com.mad.moodtrackerproject.MainMenuActivity;
import com.mad.moodtrackerproject.Mood;
import com.mad.moodtrackerproject.MoodCheckInActivity;
import com.mad.moodtrackerproject.R;
import com.mad.moodtrackerproject.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainMenuFragment extends Fragment {

    private MainViewModel mViewModel;
    private LineChart lineChart;
    private Button moodBtn;
    private List<Mood> moodList = new ArrayList<>();
    private FirebaseFirestore db;
    private View rootView;
    private String userId;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        lineChart = rootView.findViewById(R.id.lineChart);
        moodBtn = rootView.findViewById(R.id.moodCheckInBtn);
        db = FirebaseFirestore.getInstance();
        var firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(rootView.getContext(), MainActivity.class));
            requireActivity().finish();
        }

        moodBtn.setOnClickListener(v -> {
            Intent i = new Intent(rootView.getContext(), MoodCheckInActivity.class);
            startActivity(i);
        });
        userId = firebaseUser.getUid();
        loadMenuData();

        // Load chart initially
        loadMoodChartData(userId);
        ImageButton refreshBtn = rootView.findViewById(R.id.refreshChart);
        refreshBtn.setOnClickListener(v -> {
            db.collection("mood")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            batch.delete(doc.getReference());
                        }
                        batch.commit().addOnSuccessListener(unused -> {
                            Toast.makeText(requireContext(), "All mood data deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(rootView.getContext(), MainMenuActivity.class));
                            requireActivity().finish();
                        }).addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "Failed to delete mood data", Toast.LENGTH_SHORT).show()
                        );
                    });
        });


        var desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);

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
    @SuppressLint("SetTextI18n")
    private void loadMenuData() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(q -> {
                    var user = q.getResult().toObject(User.class);
                    TextView menuTxt = rootView.findViewById(R.id.menuTxt);
                    menuTxt.setText("Hi " + user.name + ", don't forget to check in your mood today!");
                });

        db.collection("mood")
                .whereEqualTo("userId", userId)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(q -> {
                    TextView emoji = rootView.findViewById(R.id.moodEmoji);
                    TextView moodType = rootView.findViewById(R.id.moodType);
                    TextView streak = rootView.findViewById(R.id.streakTxt);
                    TextView note = rootView.findViewById(R.id.noteTxt);

                    if (q.isSuccessful() && q.getResult() != null && !q.getResult().isEmpty()) {
                        List<DocumentSnapshot> docs = q.getResult().getDocuments();

                        String[] emojis = {"😡", "🙁", "😐", "🙂", "😄"};
                        String[] moodLabels = {"Angry", "Sad", "Neutral", "Happy", "Joyful"};
                        Mood latestMoodObj = docs.get(0).toObject(Mood.class);
                        int latestMood = latestMoodObj.mood;
                        int moodIndex = latestMood - 1;

                        if (moodIndex >= 0 && moodIndex < emojis.length) {
                            emoji.setText(emojis[moodIndex]);
                            moodType.setText(moodLabels[moodIndex]);
                        }
                        note.setText("Note: " + latestMoodObj.note);

                        // --- Check if today's mood already exists and get its document ID ---
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        String today = sdf.format(new Date());

                        for (DocumentSnapshot doc : docs) {
                            Mood mood = doc.toObject(Mood.class);
                            String docId = doc.getId(); // ✅ this is what you want

                            String moodDate = sdf.format(mood.dateTime);
                            if (moodDate.equals(today)) {
                                moodBtn.setText("Edit Mood");
                                moodBtn.setOnClickListener(v -> {
                                    Intent i = new Intent(rootView.getContext(), MoodCheckInActivity.class);
                                    i.putExtra("moodId", docId); // pass the correct doc ID here
                                    startActivity(i);
                                });
                                break;
                            }
                        }

                        // --- Calculate streak ---
                        int streakCount = 1;
                        for (int i = 1; i < docs.size(); i++) {
                            Mood prev = docs.get(i - 1).toObject(Mood.class);
                            Mood curr = docs.get(i).toObject(Mood.class);

                            Calendar cal1 = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal1.setTime(prev.dateTime);
                            cal2.setTime(curr.dateTime);
                            cal1.add(Calendar.DATE, -1);

                            boolean sameMood = curr.mood == latestMood;
                            boolean oneDayBefore = sdf.format(cal1.getTime()).equals(sdf.format(curr.dateTime));

                            if (sameMood && oneDayBefore) {
                                streakCount++;
                            } else {
                                break;
                            }
                        }

                        streak.setText("Mood Streak: " + streakCount + " day(s)");
                    } else {
                        emoji.setText("");
                        moodType.setText("");
                        note.setText("Note: ");
                        streak.setText("Mood Streak: 0 day(s)");
                    }
                });
        // At the end of loadMenuData()
        TextView personalTxt = rootView.findViewById(R.id.personalTxt);
        Button deleteMsg = rootView.findViewById(R.id.deleteMsgBtn);
        db.collection("personal").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String msg = documentSnapshot.getString("message");
                        if (msg != null && !msg.isEmpty()) {
                            personalTxt.setText(msg);
                            deleteMsg.setVisibility(View.VISIBLE); // Show delete button if message exists

                        } else {
                            personalTxt.setText("Tap to add your message");
                            deleteMsg.setVisibility(View.GONE);
                        }
                    } else {
                        personalTxt.setText("Tap to add your message");
                        deleteMsg.setVisibility(View.GONE);
                    }
                });

        LinearLayout personalLayout = rootView.findViewById(R.id.personalLayout);
        personalLayout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Set Personal Message");


            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setHint("Enter your message here");
            input.setPadding(32, 16, 32, 16);
            if(!personalTxt.getText().toString().equals("Tap to add your message")) {
                builder.setTitle("Edit Personal Message");
                input.setText(personalTxt.getText());
            }

            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newMessage = input.getText().toString().trim();
                if (!newMessage.isEmpty()) {
                    Map<String, Object> msgData = new HashMap<>();
                    msgData.put("message", newMessage);

                    db.collection("personal").document(userId)
                            .set(msgData)
                            .addOnSuccessListener(aVoid -> {
                                personalTxt.setText(newMessage);
                                Toast.makeText(requireContext(), "Message saved!", Toast.LENGTH_SHORT).show();
                                deleteMsg.setVisibility(View.VISIBLE); // Show delete button if message exists
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Failed to save message", Toast.LENGTH_SHORT).show()
                            );
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
        deleteMsg.setOnClickListener(v -> {
            db.collection("personal").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        personalTxt.setText("Tap to add your message");
                        deleteMsg.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Failed to delete message", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}