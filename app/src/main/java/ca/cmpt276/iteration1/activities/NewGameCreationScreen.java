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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    EditText gameScore;
    EditText numberOfPlayers;

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

        // edittext input field setup
        gameScore = findViewById(R.id.etGameScore);
        gameScore.addTextChangedListener(inputTextWatcher);
        numberOfPlayers = findViewById(R.id.etNumberOfPlayers);
        numberOfPlayers.addTextChangedListener(inputTextWatcher);


        setTitle(getString(R.string.add_new_game));
    }

    public void displayError(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private final TextWatcher inputTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            TextView displayAchievementLevel = findViewById(R.id.tvGameAchievementLevel);

            try {
                int players = Integer.parseInt(numberOfPlayers.getText().toString());
                int score = Integer.parseInt(gameScore.getText().toString());

                if (players == 0) {
                    displayError(getString(R.string.player_number_error));
                    throw new NumberFormatException();
                }
                GameType gameType = gm.getGameTypeFromString(gameTypeString);
                String achievementLevel = getString(R.string.achievement_level) + " " + gameType.getAchievementLevel(score, players);

                displayAchievementLevel.setText(achievementLevel);
            }
            catch (NumberFormatException numberFormatException) {
                displayAchievementLevel.setText(R.string.game_achievement_level_calculating);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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

                    PlayedGame currGame = new PlayedGame(gameTypeString, numberOfPlayers, gameScore, gameType.getAchievementIndex(gameScore,numberOfPlayers));
                    gm.addPlayedGame(currGame);

                    String res = gameTypeString + getString(R.string.game_saved_toast);
                    Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, R.string.invalid_game, Toast.LENGTH_SHORT).show();
                    break;
                }
            case android.R.id.home:
                this.finish();
                return true;
        }


        return true;
    }

}