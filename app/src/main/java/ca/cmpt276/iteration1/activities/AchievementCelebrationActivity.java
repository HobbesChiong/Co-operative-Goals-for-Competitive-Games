package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;

public class AchievementCelebrationActivity extends AppCompatActivity {

    private static final String GAME_PLAYED_INDEX_TAG = "GamePlayedIndex";
    private static final String GAME_PLAYED_TYPE_TAG = "GameType";
    private final int INVALID_GAME_PLAYED_INDEX = -1;

    private GameManager gameManager;
    private int gamePlayedIndex;
    private String gameTypeString;
    private GameType gameType;

    private PlayedGame playedGame;
    private int playerCount;
    private int gameScore;
    private String difficulty;
    private String nextLevelName;
    private int pointsToNextLevel;

    private Spinner spnrThemes;
    private TextView tvAchievementLevel;
    private ImageView ivGameSelfie;
    private ImageView ivAchievementAnimation;

    public static Intent makeIntent(Context context, String gameType, int gamePlayedIndex){
        Intent intent = new Intent(context, AchievementCelebrationActivity.class);
        intent.putExtra(GAME_PLAYED_INDEX_TAG, gamePlayedIndex);
        intent.putExtra(GAME_PLAYED_TYPE_TAG, gameType);
        return intent;
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();
        gamePlayedIndex = intent.getIntExtra(GAME_PLAYED_INDEX_TAG, INVALID_GAME_PLAYED_INDEX);
        gameTypeString = intent.getStringExtra(GAME_PLAYED_TYPE_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_celebration);

        extractIntentExtras();
        getGameInfo();
        setGameInfo();
        setPlayAnimationButton();
    }

    private void getGameInfo(){
        gameManager = GameManager.getInstance();
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedIndex);
        gameType = gameManager.getGameTypeFromString(gameTypeString);

        playerCount = playedGame.getNumberOfPlayers();
        gameScore = playedGame.getTotalScore();
        difficulty = playedGame.getDifficulty();

        // Iterate through each achievement level, use regex to grab numerical values
        for (String achievementLevel : gameType.getAchievementLevelScoreRequirements(playerCount, difficulty)){
            int achievementLevelScore = Integer.parseInt(achievementLevel.replaceAll("[^0-9]", ""));
            if (achievementLevelScore > gameScore){
                // If the current achievementLevelScore is greater than the game's game score, it is the next level above
                // Calculate the gap in points to the next level, and grab the name of the next achievement level's name
                pointsToNextLevel = achievementLevelScore - gameScore;
                nextLevelName = gameType.getAchievementLevel(achievementLevelScore, playerCount, difficulty);
                break;
            }
        }
    }

    private void setGameInfo(){
        TextView tvNumberOfPlayers = findViewById(R.id.tvNumberOfPlayers);
        TextView tvGameScore = findViewById(R.id.tvGameScore);
        TextView tvNextLevelGap = findViewById(R.id.tvNextLevelGap);
        String nextLevelTitle = gameType.getAchievementLevel(gameScore, playerCount, difficulty);

        tvAchievementLevel = findViewById(R.id.tvNextLevelName);
        ivGameSelfie = findViewById(R.id.ivGameSelfie);
        ivAchievementAnimation = findViewById(R.id.ivAchievementAnimation);

        tvNumberOfPlayers.setText("Number of players: " + playerCount);
        tvGameScore.setText("Score achieved: " + gameScore);

        tvNextLevelGap.setText(pointsToNextLevel + " points away from the next level!");
        tvAchievementLevel.setText("Next level: " + nextLevelTitle + " - " + (gameScore + pointsToNextLevel));
    }

    private void setPlayAnimationButton(){
        Button btnPlayAnimation = findViewById(R.id.btnPlayAnimation);
        btnPlayAnimation.setOnClickListener(view -> {
            // Animate the star to signify the achievement
            ImageView starImage = findViewById(R.id.ivAchievementAnimation);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            starImage.startAnimation(animation);
            starImage.setVisibility(View.VISIBLE);

            // Store a reference to the activity so we can end the activity after an animation finishes
            Activity thisActivity = this;

            // End the activity after the animation finished https://stackoverflow.com/questions/7606498/end-animation-event-android
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //taken from https://stackoverflow.com/questions/37248300/how-to-finish-specific-activities-not-all-activities
    /*                Intent intent = new Intent(thisActivity, GamePlayedListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);*/
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            // Play a sound
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.achievement_jingle);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        });
    }

}








