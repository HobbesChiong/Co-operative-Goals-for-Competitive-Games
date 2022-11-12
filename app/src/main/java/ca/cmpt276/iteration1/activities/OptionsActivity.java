package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Add the back button to the actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        createRadioButtons();
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
                    GameManager manager = GameManager.getInstance();
                    manager.setGameTheme(buttonIndex);
                }
            });

            // Add to radio group
            group.addView(button);
        }
    }
}