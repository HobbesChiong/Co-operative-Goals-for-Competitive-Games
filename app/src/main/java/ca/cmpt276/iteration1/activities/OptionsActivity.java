package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ca.cmpt276.iteration1.R;

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
        String[] achievementNames = {"Animals", "Powerful Fantasy Characters", "Spongebob"};

        RadioGroup group = findViewById(R.id.rgAchievementTheme);

        // Create the radio buttons
        for (int i = 0; i < achievementNames.length; i++) {
            RadioButton button = new RadioButton(this);
            button.setText(achievementNames[i]);

            // todo: setup on-click callbacks
            //button.setOnClickListener();

            // Add to radio group
            group.addView(button);
        }
    }
}