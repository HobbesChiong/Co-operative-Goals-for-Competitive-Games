package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.InvalidParameterException;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameType;

public class GameTypeActivity extends AppCompatActivity {
    private static final int INT_INVALID = -1;
    private MenuInflater menuInflater;


    // When an intent is created with only a context, it is used for creating a new game type
/*    public static Intent makeIntent(Context context){
        return new Intent(context, GameTypeActivity.class);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);

        // Get components
        ActionBar ab = getSupportActionBar();
        menuInflater = getMenuInflater();

        String appBarTitle = "New Game Configuration";


        ab.setTitle(appBarTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        menuInflater.inflate(R.menu.menu_add_type_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btnSave: {
                // Try to create a new game configuration. If it fails, toast!
                try {
                    String gameName = getStringFromEditText(R.id.etGameName);
                    int goodScore = getIntFromEditText(R.id.etGoodScore);
                    int badScore = getIntFromEditText(R.id.etBadScore);

                    if (gameName.isEmpty()) {
                        throw new IllegalArgumentException("Game name cannot be empty!");
                    }

                    GameType gameType = new GameType(gameName, goodScore, badScore);
                } catch (Exception e) {
                    Toast.makeText(this,"Game configuration is invalid!",Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }

        return true;
    }

    private String getStringFromEditText(int editTextID) {
        EditText stringField = findViewById(editTextID);
        return stringField.getText().toString();
    }

    private int getIntFromEditText(int editTextId) {
        // Get the edit text to get the int from
        EditText intField = findViewById(editTextId);
        // Return the integer, parse it to a string
        return Integer.parseInt(intField.getText().toString());
    }
}