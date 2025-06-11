package com.mad.moodtrackerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.mad.moodtrackerproject.R;

import com.mad.moodtrackerproject.databinding.ActivityMainMenuBinding;
import com.mad.moodtrackerproject.ui.main.MainMenuFragment;

public class MainMenuActivity extends AppCompatActivity {

    ActivityMainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainMenuFragment.newInstance())
                    .commitNow();
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            var id = item.getItemId();
            //Navigation bottom, add similarly with the below code for the other buttons
            if (id == R.id.navHome) {
                replaceFragment(new MainMenuFragment());
                return true;
            }
            Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }
}