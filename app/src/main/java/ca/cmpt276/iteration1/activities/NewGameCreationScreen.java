package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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


    private String gameTypeString;
    private GameType gameType;
    private final GameManager gm = GameManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);

        Intent intent = getIntent();
        gameTypeString = intent.getStringExtra("GameType");
        gameType = gm.getGameType(gameTypeString);

        // action bar setup
        ActionBar ab = getSupportActionBar();
        MenuInflater menuInflater = getMenuInflater();
        ab.setDisplayHomeAsUpEnabled(true);

        numberOfPLayerCheck();

        setTitle("Add new game");
    }

    private void numberOfPLayerCheck() {
        EditText numberOfPlayers = findViewById(R.id.etNumberOfPlayers);

        numberOfPlayers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                if(e.toString().equals("")){
                    return;
                }
                int numberOfPlayers = Integer.parseInt(e.toString());
                populateAchievementList(numberOfPlayers);
            }
        });
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


                PlayedGame currGame = new PlayedGame(gameTypeString, numberOfPlayers, gameScore,gameType.getAchievementLevel(gameScore, numberOfPlayers));
                gm.addPlayedGame(currGame);

            } catch (Exception e) {
                Toast.makeText(this, "Game configuration is invalid!", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }



    private void populateAchievementList(int numberOfPlayers) {
        // for now I'm assuming my list is taking in an array of Strings

        GameType currGameType = gm.getGameType("Uno");
        ArrayList<String> listOfAchievementScores = currGameType.getAchievementLevelScoreRequirements(numberOfPlayers);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.achievement_score,
                listOfAchievementScores
        );
        ListView list = findViewById(R.id.lvAchievementList);
        list.setAdapter(adapter);
    }


}