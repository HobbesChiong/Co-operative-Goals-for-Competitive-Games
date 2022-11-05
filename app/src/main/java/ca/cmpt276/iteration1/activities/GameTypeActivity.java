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

/**
 * Activity for creating a new type of game.
 *
 * The user can input the name of the game, what a good score (per player) should look like
 * and a bad score (per player should look like)
 */
public class GameTypeActivity extends AppCompatActivity {
    public static final String GAME_TYPE = "GameType";
    public static final String EDIT_GAME_TYPE = "EditGameType";
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
        ab.setDisplayHomeAsUpEnabled(true);

        gameName = findViewById(R.id.etGameName);
        goodScore = findViewById(R.id.etGoodScore);
        badScore = findViewById(R.id.etBadScore);

        String appBarTitle;
        gameManager = GameManager.getInstance();

        extractIntentExtras();
        // If we are editing an existing game type
        if (editGameActivity == true){
            gameType = gameManager.getGameTypeFromString(gameTypeString);
            appBarTitle = getString(R.string.edit_game_type);
            setGameTypeInfo();
        }
        else {
            appBarTitle = getString(R.string.new_game_type);
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
                // Check to see if we are saving a new configuration or editing an existing one
                if (editGameActivity == false){
                    // Try to create a new game configuration. If it fails, toast!
                    try {
                        String gameName = getStringFromEditText(R.id.etGameName);
                        int goodScore = getIntFromEditText(R.id.etGoodScore);
                        int badScore = getIntFromEditText(R.id.etBadScore);

                        if (gameName.isEmpty()) {
                            throw new IllegalArgumentException("Game name cannot be empty!");
                        }

                        GameType gameType = new GameType(gameName, goodScore, badScore);
                        String res = gameName + " " + getString(R.string.configuration_saved);
                        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();

                        gameManager.addGameType(gameType);

                        finish();
                    } catch (Exception e) {
                        Toast.makeText(this, R.string.invalid_config,Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    editGameType();
                    finish();
                }
                break;
            }

            case R.id.btnDelete: {
                try{
                    gameManager.deleteGameType(gameTypeString);
                    String res = getString(R.string.deleting) + " " + gameTypeString;
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
                    finish();
                }
                catch(Exception e){
                    Toast.makeText(this, R.string.cant_delete, Toast.LENGTH_SHORT).show();
                }

            }
            case android.R.id.home:
                this.finish();
                return true;
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