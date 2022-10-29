package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;

public class NewGameCreationScreen extends AppCompatActivity {

    // temp data
    // need gameType passed as a string via an Intent
    private final String gameType = "Uno";
    private GameType uno;
    private final GameManager gameManager = GameManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);

        // temp gameType
        GameType uno = new GameType("Uno", 10,100);



        // action bar setup
        ActionBar ab = getSupportActionBar();
        MenuInflater menuInflater = getMenuInflater();
        ab.setDisplayHomeAsUpEnabled(true);

        populateAchievementList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu
        getMenuInflater().inflate(R.menu.menu_add_type_appbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnSave) {
            try {
                EditText etGameScore = findViewById(R.id.etGameScore);
                EditText etNumberOfPlayers = findViewById(R.id.etNumberOfPlayers);

                if (etNumberOfPlayers == null || etGameScore == null) {
                    throw new IllegalArgumentException("Edit Text fields need to be filled");
                }

                int gameScore = Integer.parseInt(etGameScore.getText().toString());
                int numberOfPlayers = Integer.parseInt(etNumberOfPlayers.getText().toString());

                if (gameScore <= 0 || numberOfPlayers <= 0) {
                    throw new IllegalArgumentException("Edit Text fields need to have positive values");
                }

                PlayedGame currGame = new PlayedGame(gameType, numberOfPlayers, gameScore, uno.getAchievementLevel(gameScore, numberOfPlayers));
                gameManager.addPlayedGame(currGame);

            } catch (Exception e) {
                Toast.makeText(this, "Game configuration is invalid!", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }



    private void populateAchievementList() {
        // for now I'm assuming my list is taking in an array of Strings
        ArrayList<String> listOfAchievementScores = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.achievement_score,
                listOfAchievementScores
        );
        ListView list = findViewById(R.id.lvAchievementList);
        list.setAdapter(adapter);
    }

}