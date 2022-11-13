package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.model.ListViewAdapter;

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
    private String difficulty;
    private int noOfPlayer;
    private int score;
    private List playerScore;

    // For when we are editing an existing played game
    private final int POSITION_NON_EXISTENT = -1;
    private int gamePlayedPosition;
    private PlayedGame playedGame;

    // When it takes in a string, we are creating a new game based on the type
    public static Intent makeIntent(Context context, String gameTypeString){
        Intent intent = new Intent(context, NewGameCreationScreen.class);
        intent.putExtra("GameType", gameTypeString);
        return intent;
    }

    // When it receives a string and position, we are editing an existing game
    public static Intent makeIntent(Context context, String gameTypeString, int position){
        Intent intent = new Intent(context, NewGameCreationScreen.class);
        intent.putExtra("GameType", gameTypeString);
        intent.putExtra("GamePlayedPosition", position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);


        // action bar setup
        ActionBar ab = getSupportActionBar();
        MenuInflater menuInflater = getMenuInflater();
        ab.setDisplayHomeAsUpEnabled(true);

        gm = GameManager.getInstance();
        gameType = gm.getGameTypeFromString(gameTypeString);
        extractIntentExtras();
        setUpListView();
    }

    private void setUpListView() {
        ListView lv = findViewById(R.id.lv_ScoreInput);
        lv.setItemsCanFocus(true);
        for(int i = 0; i < noOfPlayer; i++){
            playerScore.add(0);
        }
        ListViewAdapter adapter = new ListViewAdapter(this, playerScore);
        lv.setAdapter(adapter);
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();
        this.gameTypeString = intent.getStringExtra("GameType");
        this.gamePlayedPosition = intent.getIntExtra("GamePlayedPosition", POSITION_NON_EXISTENT);
        this.gameType = gm.getGameTypeFromString(gameTypeString);
        this.noOfPlayer = intent.getIntExtra("numberOfPlayer", 1);

        // Creating a new game
        if (this.gamePlayedPosition == POSITION_NON_EXISTENT){
            setTitle(getString(R.string.add_new_game) + " (" + intent.getStringExtra("difficulty") + ")");
            this.difficulty = intent.getStringExtra("difficulty");
        }
        // Editing an existing game
        else {
            setTitle(getString(R.string.edit_existing_game));
            setPlayedGameInfo();
        }
    }

    private void setPlayedGameInfo(){
        ArrayList<PlayedGame> playedGames = gm.getSpecificPlayedGames(gameTypeString);

        this.playedGame = playedGames.get(gamePlayedPosition);
        this.difficulty = playedGame.getDifficulty();


    }

    private void displayError(String message){
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
                GameType gameType = gm.getGameTypeFromString(gameTypeString);
                String achievementLevel = getString(R.string.achievement_level) + " " + gameType.getAchievementLevel(score, noOfPlayer, difficulty);

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
                    // Creating a new game
                    if (gamePlayedPosition == POSITION_NON_EXISTENT){
                        PlayedGame currGame = new PlayedGame(gameTypeString, numberOfPlayers, gameScore, gameType.getAchievementIndex(gameScore, numberOfPlayers,difficulty), difficulty);
                        gm.addPlayedGame(currGame);

                        String res = gameTypeString + getString(R.string.game_saved_toast);
                        Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
                    }
                    // Editing an existing game
                    else {
                        playedGame.editPlayedGame(numberOfPlayers, gameScore, gameType.getAchievementIndex(gameScore, numberOfPlayers, difficulty));
                        Toast.makeText(this, getString(R.string.save_changes_to_existing_game), Toast.LENGTH_SHORT).show();
                    }

                    //taken from https://stackoverflow.com/questions/37248300/how-to-finish-specific-activities-not-all-activities
                    Intent intent = new Intent(this, GamePlayed.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, R.string.invalid_game, Toast.LENGTH_SHORT).show();
                    break;
                }
            case android.R.id.home:
                Intent intent = new Intent(this, GamePlayed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }


        return true;
    }

}