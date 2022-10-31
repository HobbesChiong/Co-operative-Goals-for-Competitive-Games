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
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;

public class GameTypeActivity extends AppCompatActivity {
    public static final String GAME_TYPE = "GameType";
    public static final String EDIT_GAME_TYPE = "EditGameType";
    private static final int INT_INVALID = -1;
    private MenuInflater menuInflater;

    private boolean editGameActivity;
    private String gameTypeString;
    private GameType gameType;

    EditText gameName;
    EditText goodScore;
    EditText badScore;

    private GameManager gameManager;

    // If parameter argument only has a context, we are creating a new game type
    public static Intent makeIntent(Context context){
        return new Intent(context, GameTypeActivity.class);
    }

    // If parameter argument includes a string for gameType, we are editing an existing game type
    public static Intent makeIntent(Context context, String gameType){
        Intent intent = new Intent(context, GameTypeActivity.class);

        intent.putExtra(EDIT_GAME_TYPE, true);
        intent.putExtra(GAME_TYPE, gameType);

        return intent;
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();
        editGameActivity = intent.getBooleanExtra(EDIT_GAME_TYPE, false);
        gameTypeString = intent.getStringExtra(GAME_TYPE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);

        // Get components
        ActionBar ab = getSupportActionBar();
        menuInflater = getMenuInflater();

        gameName = findViewById(R.id.etGameName);
        goodScore = findViewById(R.id.etGoodScore);
        badScore = findViewById(R.id.etBadScore);

        String appBarTitle;
        gameManager = GameManager.getInstance();

        // If we are editing an existing game type
        if (editGameActivity == true){
            extractIntentExtras();
            gameType = gameManager.getGameType(gameTypeString);
            appBarTitle = "Edit Game Type Configuration";
            setGameTypeInfo();
        }
        else {
            appBarTitle = "New Game Type Configuration";
        }

        ab.setTitle(appBarTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        if(editGameActivity == true) {
            menuInflater.inflate(R.menu.menu_appbar_delete, menu);
        }
        else{
            menuInflater.inflate(R.menu.menu_add_type_appbar, menu);
        }

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
                    String res = gameName + " configuration saved";
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show();

                    gameManager.addGameType(gameType);

                    finish();
                } catch (Exception e) {
                    Toast.makeText(this,"Game configuration is invalid!",Toast.LENGTH_SHORT).show();
                }

                break;
            }

            case R.id.btnDelete: {
                try{
                    gameManager.deleteGameType(gameTypeString);
                    String res = "Deleting " + gameTypeString;
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
                    finish();
                }
                catch(Exception e){
                    Toast.makeText(this, "Can't delete this Game Configuration", Toast.LENGTH_SHORT).show();
                }

            }
        }

        return true;
    }

    // When the user would like to save changes to an existing game type
    private void editGameType(){

        String newGameName = gameName.getText().toString();
        int newGoodScore = Integer.parseInt(goodScore.getText().toString());
        int newBadScore = Integer.parseInt(badScore.getText().toString());

        gameType.editGameType(newGameName, newGoodScore, newBadScore);
    }

    private void setGameTypeInfo(){
        // When the user is editing an existing game type, set the text fields accordingly

        gameName.setText(gameType.getGameType());
        goodScore.setText(String.valueOf(gameType.getGoodScore()));
        badScore.setText(String.valueOf(gameType.getBadScore()));

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