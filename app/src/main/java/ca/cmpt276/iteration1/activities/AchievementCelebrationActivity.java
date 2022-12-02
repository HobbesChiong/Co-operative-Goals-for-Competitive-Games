package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

    private PlayedGame playedGame;

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

        gameManager = GameManager.getInstance();
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedIndex);


    }
}