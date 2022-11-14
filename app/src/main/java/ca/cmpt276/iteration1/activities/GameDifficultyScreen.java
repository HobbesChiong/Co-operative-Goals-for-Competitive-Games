package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.iteration1.R;

public class GameDifficultyScreen extends AppCompatActivity {
    private String difficulty = "none";
    private int numberOfPlayers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty_screen);
        
        setupButtonListeners();
        setTitle(getString(R.string.difficulty_selection));
    }

    public static Intent makeIntent(Context context, String gameTypeString){
        Intent intent = new Intent(context, GameDifficultyScreen.class);
        intent.putExtra("GameType", gameTypeString);
        return intent;
    }

    private void setupButtonListeners() {
        EditText etNoOfPlayer = findViewById(R.id.etChooseNumberOfPlayers);
        Button btnEasy = findViewById(R.id.btnEasy);
        Button btnNormal = findViewById(R.id.btnNormal);
        Button btnHard = findViewById(R.id.btnHard);
        Button btnContinue = findViewById(R.id.btnContinue);

        btnEasy.setOnClickListener(this::btnDifficultyClick);
        btnNormal.setOnClickListener(this::btnDifficultyClick);
        btnHard.setOnClickListener(this::btnDifficultyClick);
        btnContinue.setOnClickListener(view -> {
            try{
                if(!etNoOfPlayer.getText().toString().equals("")){
                    numberOfPlayers = Integer.parseInt(etNoOfPlayer.getText().toString());
                }
                if(difficulty.equals("none")){
                    throw new IllegalArgumentException();
                }
                //extract data from intent
                Intent prevIntent = getIntent();
                String gameType = prevIntent.getStringExtra("GameType");

                Intent intent = NewGameCreationScreen.makeIntent(GameDifficultyScreen.this, gameType);
                intent.putExtra("difficulty",difficulty);
                intent.putExtra("numberOfPlayers", numberOfPlayers);
                startActivity(intent);
            }
            catch(IllegalArgumentException exception){
                Toast.makeText(this, "Please choose a difficulty", Toast.LENGTH_SHORT).show();
            }

        });

    }
    private void btnDifficultyClick(View v){
        Button btn = (Button)v;
        difficulty =  btn.getText().toString();
        String res = getString(R.string.difficulty_is_set_to) + difficulty;
        Toast.makeText(GameDifficultyScreen.this, res, Toast.LENGTH_SHORT).show();
    }
}