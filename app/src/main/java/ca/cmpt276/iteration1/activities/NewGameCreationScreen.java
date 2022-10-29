package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.PlayedGame;

public class NewGameCreationScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);


        populateAchievementList();
    }

    private void populateAchievementList() {
        ArrayAdapter<PlayedGame> adapter = new AchievementListAdapter();
        ListView list = findViewById(R.id.lvAchievementList);
        list.setAdapter(adapter);
    }
    private class AchievementListAdapter extends ArrayAdapter<PlayedGame>{
        public AchievementListAdapter() {
            super(NewGameCreationScreen.this, R.layout.achievement_score);
        }

    }
}