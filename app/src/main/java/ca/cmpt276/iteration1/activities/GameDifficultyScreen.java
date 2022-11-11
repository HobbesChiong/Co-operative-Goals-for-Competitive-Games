package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.cmpt276.iteration1.R;

public class GameDifficultyScreen extends AppCompatActivity {
    private String difficulty;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty_screen);
        
        setupButtonListeners();

    }

    private void setupButtonListeners() {
        Button btnEasy = findViewById(R.id.btnEasy);
        Button btnNormal = findViewById(R.id.btnNormal);
        Button btnHard = findViewById(R.id.btnHard);
        Button btnContinue = findViewById(R.id.btnContinue);

        btnEasy.setOnClickListener(this::btnDifficultyClick);
        btnNormal.setOnClickListener(this::btnDifficultyClick);
        btnHard.setOnClickListener(this::btnDifficultyClick);

        // setup btnContinue
        // TODO add the difficulty scaling into the game
        btnContinue.setOnClickListener(view -> {
            Intent prevIntent = getIntent();
            String gameType = prevIntent.getStringExtra("GameType");
            Intent intent = new Intent(GameDifficultyScreen.this, NewGameCreationScreen.class);
            intent.putExtra("GameType",gameType);
            startActivity(intent);
        });

    }
    private void btnDifficultyClick(View v){
        Button btn = (Button)v;
        difficulty =  btn.getText().toString();
        String res = "difficulty is set to " + difficulty;
        Toast.makeText(GameDifficultyScreen.this, res, Toast.LENGTH_SHORT).show();
    }
}