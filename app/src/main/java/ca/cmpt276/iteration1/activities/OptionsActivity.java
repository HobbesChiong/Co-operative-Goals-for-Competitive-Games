package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;

public class OptionsActivity extends AppCompatActivity {
    public static final String OPTIONS_PREFERENCES = "Options Preferences";
    public static final String ACHIEVEMENT_THEME_INDEX = "Achievement Theme Index";

    private final int REQUEST_PERMISSIONS_CODE = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private GameManager gameManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Add the back button to the actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.options);

        gameManager = GameManager.getInstance();

        createRadioButtons();
        createGrantPermissionsButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return true;
    }

    // Save the
    @Override
    protected void onStop() {
        saveOptions();
        super.onStop();
    }

    private void saveOptions() {
        SharedPreferences sharedPreferences = getSharedPreferences(OPTIONS_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int achievementThemeIndex = GameManager.getInstance().getAchievementTheme();

        editor.putInt(ACHIEVEMENT_THEME_INDEX, achievementThemeIndex);
        editor.apply();
    }

    private void createRadioButtons() {
        String[] achievementNames = getResources().getStringArray(R.array.theme_names);
        RadioGroup group = findViewById(R.id.rgAchievementTheme);

        // Create the radio buttons
        for (int i = 0; i < achievementNames.length; i++) {
            RadioButton button = new RadioButton(this);
            button.setText(achievementNames[i]);

            // Store the index of the radiobutton (and the theme we're using!) for the on-click listener
            final int buttonIndex = i;


            // When a radio button is clicked, the theme for achievements will be switched across the app
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameManager.setGameTheme(buttonIndex);
                }
            });

            // Add to radio group
            group.addView(button);

            // Check the achievement theme if it's the current one
            if (buttonIndex == gameManager.getAchievementTheme()) {
                group.check(button.getId());
            }
        }
    }

    private void createGrantPermissionsButton(){
        Button btnGrantPermissions = findViewById(R.id.btnGrantPermissions);
        btnGrantPermissions.setOnClickListener(view -> {
            grantStorageAndCameraPermissions();
        });
    }

    private void grantStorageAndCameraPermissions(){
        // Check if permissions are granted first, then request if not granted
        if (checkStorageAndCameraPermissions() == false){
            ActivityCompat.requestPermissions(OptionsActivity.this, REQUIRED_PERMISSIONS, 10);
        }
    }

    //Code from https://developer.android.com/codelabs/camerax-getting-started#1
    private boolean checkStorageAndCameraPermissions(){
        // If either camera permissions or storage permissions are missing, request permissions again
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(OptionsActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}