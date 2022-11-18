package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.adapters.PlayerScoreInputRecyclerViewAdapter;
import ca.cmpt276.iteration1.interfaces.PlayerScoreInputRecyclerViewInterface;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.model.PlayerScoreInput;

public class GamePlayActivity extends AppCompatActivity implements PlayerScoreInputRecyclerViewInterface {

    private final int GAME_PLAYED_POSITION_NON_EXISTENT = -1;

    private boolean editGameActivity = false;
    private boolean difficultySelected = false;

    private GameManager gameManager;
    private String gameTypeString;
    private GameType gameType;
    private PlayedGame playedGame;

    private String difficulty;
    private int playerAmount;
    private int gameScore;

    private HashMap<Integer, Integer> playerScores;
    private EditText etPlayerAmount;
    private RecyclerView rvPlayerScoreInputs;

    // If a context and gameType are given, we are creating a new game
    public static Intent makeIntent(Context context, String gameTypeString){
        Intent intent = new Intent(context, GamePlayActivity.class);
        intent.putExtra("GameTypeString", gameTypeString);
        return intent;
    }

    // If a context and position are given, we are editing an existing game
    public static Intent makeIntent(Context context, String gameTypeString, int position){
        Intent intent = new Intent(context, GamePlayActivity.class);
        intent.putExtra("GameTypeString", gameTypeString);
        intent.putExtra("GamePlayedPosition", position);
        return intent;
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();

        gameTypeString = intent.getStringExtra("GameTypeString");

        int gamePlayedPosition = intent.getIntExtra("GamePlayedPosition", GAME_PLAYED_POSITION_NON_EXISTENT);
        if (gamePlayedPosition == GAME_PLAYED_POSITION_NON_EXISTENT){
            // Creating a new game if this condition is true
            return;
        }

        // If it does not exit, we are editing an existing game
        // So grab the needed values
        editGameActivity = true;
        difficultySelected = true;
        gameType = gameManager.getGameTypeFromString(gameTypeString);
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedPosition);
        difficulty = playedGame.getDifficulty();
        playerAmount = playedGame.getNumberOfPlayers();
        gameScore = playedGame.getTotalScore();
        playerScores = playedGame.getPlayerScores();

        // If we are editing a game, set up the screen to display info
        setEditGameInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        gameManager = GameManager.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create New Game");

        setDifficultyButtons();
        extractIntentExtras();

        if (editGameActivity == true && difficultySelected == true){
            actionBar.setTitle("Edit Game");
            enableHiddenElements();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_add_type_appbar, menu);
        return true;
    }

    private void setDifficultyButtons(){
        Button btnDifficultyEasy = findViewById(R.id.btnDifficultyEasy);
        Button btnDifficultyNormal = findViewById(R.id.btnDifficultyNormal);
        Button btnDifficultyHard = findViewById(R.id.btnDifficultyHard);

        // Choosing player count is hidden by default as a user needs to select a difficulty first
        // If any of these buttons are pressed, enable player count input
        btnDifficultyEasy.setOnClickListener(view -> {
            difficulty = "easy";
            difficultySelected = true;
            enableHiddenElements();
        });
        btnDifficultyNormal.setOnClickListener(view -> {
            difficulty = "normal";
            difficultySelected = true;
            enableHiddenElements();
        });
        btnDifficultyHard.setOnClickListener(view -> {
            difficulty = "hard";
            difficultySelected = true;
            enableHiddenElements();
        });
    }

    private void enableHiddenElements(){
        // The player amount edittext and recyclerview containing cards to fill in player scores are hidden by default
        // The user must select a difficulty first in order to select amount of players and input scores
        TextView tvChoosePlayerAmount = findViewById(R.id.tvChoosePlayerAmount);
        etPlayerAmount = findViewById(R.id.etPlayerCount);
        rvPlayerScoreInputs = findViewById(R.id.rvPlayerScoreInputs);

        tvChoosePlayerAmount.setVisibility(View.VISIBLE);
        etPlayerAmount.setVisibility(View.VISIBLE);
        etPlayerAmount.addTextChangedListener(playerCountInputWatcher);
        rvPlayerScoreInputs.setVisibility(View.VISIBLE);
    }

    private final TextWatcher playerCountInputWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                playerAmount = Integer.parseInt(etPlayerAmount.getText().toString());
                setupGameInfoModels();
            }
            catch (NumberFormatException numberFormatException){
                //Toast.makeText(GamePlayActivity.this, "Invalid player amount", Toast.LENGTH_SHORT).show();
                Log.i("Undefined Player Amount", "User has deleted player amount, awaiting new input.");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setupGameInfoModels() {
        // Nothing too complicated, we're just giving each "player score input card" an id ranging from 0 to playerAmount
        // This will help is keep track of which cards have a score inputted or not later on

        // If we are creating a new game, we do not need to pull existing scores from the existing game
        if (editGameActivity == false){
            ArrayList<PlayerScoreInput> playerScoreInputs = new ArrayList<>();
            for (int i = 0; i < playerAmount; i++){
                playerScoreInputs.add(new PlayerScoreInput(i));
            }

            setupRecyclerView(playerScoreInputs);
        }
        // If we are editing a game, we need to pull
    }

    private void setupRecyclerView(ArrayList<PlayerScoreInput> playerScoreInputs){
        RecyclerView recyclerView = findViewById(R.id.rvPlayerScoreInputs);
        PlayerScoreInputRecyclerViewAdapter adapter = new PlayerScoreInputRecyclerViewAdapter(GamePlayActivity.this, playerScoreInputs, editGameActivity, GamePlayActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GamePlayActivity.this));
    }

    private void grabPlayerScores() {
        
    }

    private void setEditGameInfo(){

    }

    @Override
    public void checkAllPlayerScoreInputs() {

    }
}