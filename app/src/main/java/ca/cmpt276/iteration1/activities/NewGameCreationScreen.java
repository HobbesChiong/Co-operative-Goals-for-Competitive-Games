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

/*
 * Takes in an intent from previous activity and creates a new game of the "GameType"
 * using the inputs "number of players" and "GameScore" then adds it to the "Played Games"
 * array list in the GameManager
 *
 * */
public class NewGameCreationScreen extends AppCompatActivity {


    private String gameTypeString;
    private GameType gameType;
    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);

        gm = GameManager.getInstance();
        Intent intent = getIntent();
        gameTypeString = intent.getStringExtra("GameType");
        gameType = gm.getGameTypeFromString(gameTypeString);

        // action bar setup
        ActionBar ab = getSupportActionBar();
        MenuInflater menuInflater = getMenuInflater();
        ab.setDisplayHomeAsUpEnabled(true);

        numberOfPLayerCheck();

        setTitle(getString(R.string.add_new_game));
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
                int playerNumber = Integer.parseInt(e.toString());
                populateAchievementList(playerNumber);
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
        switch (item.getItemId()) {
            case R.id.btnSave:
                try {
                    EditText etGameScore = findViewById(R.id.etGameScore);
                    EditText etNumberOfPlayers = findViewById(R.id.etNumberOfPlayers);

                    if (etNumberOfPlayers == null || etGameScore == null) {
                        throw new IllegalArgumentException("Edit Text fields need to be filled");
                    }

                    int gameScore = Integer.parseInt(etGameScore.getText().toString());
                    int numberOfPlayers = Integer.parseInt(etNumberOfPlayers.getText().toString());

                    if (gameScore < 0 || numberOfPlayers <= 0) {
                        throw new IllegalArgumentException("Edit Text fields need to have positive values");
                    }

                    PlayedGame currGame = new PlayedGame(gameTypeString, numberOfPlayers, gameScore, gameType.getAchievementLevel(gameScore, numberOfPlayers));
                    gm.addPlayedGame(currGame);

                    String res = gameTypeString + " game saved";
                    Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, R.string.invalidConfig, Toast.LENGTH_SHORT).show();
                    break;
                }
            case android.R.id.home:
                this.finish();
                return true;
        }

        return true;
    }



    private void populateAchievementList(int numberOfPlayers) {
        // for now I'm assuming my list is taking in an array of Strings

        GameType currGameType = gm.getGameTypeFromString(gameTypeString);
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