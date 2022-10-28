package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.cmpt276.iteration1.R;

public class GameTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);

        ActionBar ab = getSupportActionBar();

        String appBarTitle = "Create new Game Configuration";


        ab.setTitle(appBarTitle);

    }
}