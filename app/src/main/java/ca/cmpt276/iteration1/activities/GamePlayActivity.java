package ca.cmpt276.iteration1.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.adapters.PlayerScoreInputRecyclerViewAdapter;
import ca.cmpt276.iteration1.interfaces.PlayerScoreInputRecyclerViewInterface;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.model.PlayerScoreInput;

/**
* Activity to allow user input the game information by choosing the difficulty, number of player, and each player's score
* */
public class GamePlayActivity extends AppCompatActivity implements PlayerScoreInputRecyclerViewInterface {

    private final int GAME_PLAYED_POSITION_NON_EXISTENT = -1;
    private int gamePlayedPosition;

    private boolean editGameActivity = false;
    private int originalPlayerAmount;

    private boolean difficultySelected = false;
    private boolean playersSelected = false;
    private boolean gameCompleted = false;

    private GameManager gameManager;
    private String gameTypeString;
    private GameType gameType;
    private PlayedGame playedGame;

    private String difficulty;
    private int playerAmount;
    private int totalScore;
    private String takePhoto;

    private ArrayList<Integer> playerScores;

    private EditText etPlayerAmount;
    private RecyclerView rvPlayerScoreInputs;
    private PlayerScoreInputRecyclerViewAdapter recyclerViewAdapter;

    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        gameType = gameManager.getGameTypeFromString(gameTypeString);

        gamePlayedPosition = intent.getIntExtra("GamePlayedPosition", GAME_PLAYED_POSITION_NON_EXISTENT);
        if (gamePlayedPosition == GAME_PLAYED_POSITION_NON_EXISTENT){
            // Creating a new game if this condition is true
            return;
        }

        // If a position exit, we are editing an existing game
        // Set up the screen to display info
        setEditGameInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        gameManager = GameManager.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_new_game);

        extractIntentExtras();
        setDifficultyButtons();
        setPhotoOptionsButtons();

        if (editGameActivity == true && difficultySelected == true){
            actionBar.setTitle(R.string.edit_game);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_add_type_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case (R.id.btnSave): {
                try {
                    if (difficultySelected == false || playersSelected == false || gameCompleted == false){
                        Toast.makeText(GamePlayActivity.this, R.string.require_user_to_fill_all_field, Toast.LENGTH_SHORT).show();
                        throw new Exception(String.valueOf(R.string.require_user_to_fill_all_field));
                    }

                    // Creating a new game
                    if (gamePlayedPosition == GAME_PLAYED_POSITION_NON_EXISTENT){
                        Toast.makeText(GamePlayActivity.this, R.string.game_created, Toast.LENGTH_SHORT).show();
                        saveNewGame();
                    }
                    // Editing an existing game
                    else {
                        Toast.makeText(GamePlayActivity.this, R.string.game_changes_saved, Toast.LENGTH_SHORT).show();
                        saveExistingGame();
                    }

                    // Animate the star to signify the achievement
                    ImageView starImage = findViewById(R.id.ivGameSaveAnimation);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                    starImage.startAnimation(animation);
                    starImage.setVisibility(View.VISIBLE);

                    // Store a reference to the activity so we can end the activity after an animation finishes
                    Activity thisActivity = this;

                    // End the activity after the animation finished https://stackoverflow.com/questions/7606498/end-animation-event-android
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            rvPlayerScoreInputs.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //taken from https://stackoverflow.com/questions/37248300/how-to-finish-specific-activities-not-all-activities
                            Intent intent = new Intent(thisActivity, GamePlayedListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    // Play a sound
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.achievement_jingle);
                    mediaPlayer.start(); // no need to call prepare(); create() does that for you

                    return false;
                }
                catch (Exception e){
                    return false;
                }
            }
            case (android.R.id.home): {
                finish();
            }
        }

        return true;
    }

    void saveNewGame(){
        updatePlayerScores();

        int achievementIndex = gameType.getAchievementIndex(totalScore, playerAmount, difficulty);
        LocalDateTime datePlayed = LocalDateTime.now();
        PlayedGame currentGame = new PlayedGame(gameTypeString, playerAmount, totalScore, achievementIndex, difficulty, playerScores, datePlayed);
        gameManager.addPlayedGame(currentGame);
    }

    void saveExistingGame(){
        updatePlayerScores();

        int achievementIndex = gameType.getAchievementIndex(totalScore, playerAmount, difficulty);
        playedGame.editPlayedGame(playerAmount, totalScore, achievementIndex, difficulty, playerScores);
    }

    private void setDifficultyButtons(){
        Button btnDifficultyEasy = findViewById(R.id.btnDifficultyEasy);
        Button btnDifficultyNormal = findViewById(R.id.btnDifficultyNormal);
        Button btnDifficultyHard = findViewById(R.id.btnDifficultyHard);

        btnDifficultyEasy.setTag("Easy");
        btnDifficultyNormal.setTag("Normal");
        btnDifficultyHard.setTag("Hard");

        ArrayList<Button> difficultyButtons = new ArrayList<>();
        difficultyButtons.add(btnDifficultyEasy);
        difficultyButtons.add(btnDifficultyNormal);
        difficultyButtons.add(btnDifficultyHard);

        if (editGameActivity == true){
            highlightSelectedButton(difficulty, difficultyButtons);
        }

        // Choosing player count is hidden by default as a user needs to select a difficulty first
        // If any of these buttons are pressed, enable player count input
        btnDifficultyEasy.setOnClickListener(view -> {
            highlightSelectedButton("Easy", difficultyButtons);

            difficulty = "Easy";
            difficultySelected = true;
            enableHiddenElements();
            updateScoreTextView();
        });
        btnDifficultyNormal.setOnClickListener(view -> {
            highlightSelectedButton("Normal", difficultyButtons);

            difficulty = "Normal";
            difficultySelected = true;
            enableHiddenElements();
            updateScoreTextView();
        });
        btnDifficultyHard.setOnClickListener(view -> {
            highlightSelectedButton("Hard", difficultyButtons);

            difficulty = "Hard";
            difficultySelected = true;
            enableHiddenElements();
            updateScoreTextView();
        });
    }

    //this function is to set up the yes no button that asking whether the user willing to take a photo to save for the game play
    private void setPhotoOptionsButtons() {
        Button btnYes = findViewById(R.id.btnYesToTakePhoto);
        Button btnNo = findViewById(R.id.btnNoToTakePhoto);
        ArrayList<Button> takePhotoButton = new ArrayList<>();

        btnYes.setTag("Yes");
        btnNo.setTag("No");

        takePhotoButton.add(btnYes);
        takePhotoButton.add(btnNo);

        if(editGameActivity){
            highlightSelectedButton(takePhoto, takePhotoButton);
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightSelectedButton("Yes", takePhotoButton);
                takePhoto = "Yes";

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightSelectedButton("No", takePhotoButton);
                takePhoto = "No";
            }
        });
    }

    private void highlightSelectedButton(String selectedButtonTag, ArrayList<Button> buttons){
        for (Button difficultyButton : buttons){
            if (difficultyButton.getTag().equals(selectedButtonTag)){
                difficultyButton.setBackgroundColor(Color.BLACK);
            }
            else {
                difficultyButton.setBackgroundColor(getColor(R.color.purple_500));
            }
        }
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

                // When the user chagnes the amount of players, we want to reset the adapter and textview for total score
                // This prevents any old data from persisting and being carried over - basically gives the user a fresh start!
                recyclerViewAdapter = null;
                TextView tvScoreWithAchievementLevel = findViewById(R.id.tvScoreWithAchievementLevel);
                tvScoreWithAchievementLevel.setText(R.string.waiting_player_score_input);

                playersSelected = true;
                setupGameInfoModels();

                updatePlayerScores();
            }
            catch (NumberFormatException numberFormatException){
                playersSelected = false;
                Log.i("Undefined Player Amount", getString(R.string.waiting_user_new_input));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setupGameInfoModels() {
        // Nothing too complicated, we're just giving each "player score input card" an id ranging from 0 to playerAmount
        // This will help is keep track of which cards have a score inputted or not later on

        ArrayList<PlayerScoreInput> playerScoreInputs = new ArrayList<>();
        // If we are creating a new game, we do not need to pull existing scores from the existing game
        if (editGameActivity == false){
            for (int i = 0; i < playerAmount; i++){
                playerScoreInputs.add(new PlayerScoreInput(i));
            }

            setupRecyclerView(playerScoreInputs);
        }
        if (editGameActivity == true){
            for (int i = 0; i < playerAmount; i++){
                if (i >= playerScores.size()){
                    playerScoreInputs.add(new PlayerScoreInput(i));
                }
                else {
                    playerScoreInputs.add(new PlayerScoreInput(i, playerScores.get(i)));
                }
            }

            setupRecyclerView(playerScoreInputs);
        }
    }

    private void setEditGameInfo(){
        enableHiddenElements();

        editGameActivity = true;
        difficultySelected = true;
        playersSelected = true;
        gameCompleted = true;

        gameType = gameManager.getGameTypeFromString(gameTypeString);
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedPosition);

        difficulty = playedGame.getDifficulty();
        playerAmount = playedGame.getNumberOfPlayers();
        totalScore = playedGame.getTotalScore();
        playerScores = playedGame.getPlayerScores();

        originalPlayerAmount = playerAmount;

        EditText etPlayerCount = findViewById(R.id.etPlayerCount);
        etPlayerCount.setText(String.valueOf(playerAmount));

        updateScoreTextView();
        setupGameInfoModels();
    }

    private void setupRecyclerView(ArrayList<PlayerScoreInput> playerScoreInputs){
        RecyclerView recyclerView = findViewById(R.id.rvPlayerScoreInputs);
        recyclerViewAdapter = new PlayerScoreInputRecyclerViewAdapter(GamePlayActivity.this, playerScoreInputs, editGameActivity, GamePlayActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GamePlayActivity.this));
    }


    @Override
    public void checkAllPlayerScoreInputs() {
        updatePlayerScores();
        updateScoreTextView();
    }

    private void updatePlayerScores() {
        ArrayList<Integer> playerScores = recyclerViewAdapter.getScores();

        // One or more of the scores must be invalid, the game hasn't been completed, don't recalculate!
        if (playerScores == null) {
            gameCompleted = false;
            return;
        }

        setTotalGameScore(playerScores);
    }

    private void setTotalGameScore(ArrayList<Integer> playerScores){
        totalScore = 0;
        for (int score : playerScores){
            totalScore += score;
        }
        this.playerScores = playerScores;
        updateScoreTextView();

        gameCompleted = true;
    }

    public void updateScoreTextView() {
        // If there aren't any players, don't update the score textview
        if (playerAmount == 0) {
            return;
        }

        TextView tvScoreWithAchievementLevel = findViewById(R.id.tvScoreWithAchievementLevel);

        // If we increase decrease the player amount, then increase, then decrease again (and all fields are filled)
        // The game game is still completed. Check if any fields are null and if not, update the text
        if (recyclerViewAdapter.getScores() != null){
            gameCompleted = true;
        }

        if (!gameCompleted) {
            tvScoreWithAchievementLevel.setText(R.string.waiting_player_score_input);
            return;
        }

        String achievementTitle = gameType.getAchievementLevel(totalScore, playerAmount, difficulty);
        tvScoreWithAchievementLevel.setText(getString(R.string.display_score, totalScore, achievementTitle));
    }

}