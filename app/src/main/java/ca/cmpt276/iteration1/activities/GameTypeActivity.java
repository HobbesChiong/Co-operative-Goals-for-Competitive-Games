package ca.cmpt276.iteration1.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

        // Register photo button
        Button b = findViewById(R.id.btPhoto);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the camera and take a picture
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityIntent.launch(cameraIntent);
            }
        });
    }

    // https://stackoverflow.com/questions/71082372/startactivityforresult-is-deprecated-im-trying-to-update-my-code
    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Extract the bitmap the camera just took a photo of
                    Intent i = result.getData();
                    Bundle extras = i.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // Apply the bitmap to our imageview
                    ImageView imageView = findViewById(R.id.ivGameBox);
                    imageView.setImageBitmap(imageBitmap);

                    // Save the bitmap to storage (https://www.youtube.com/watch?v=oLcxTunwaFk)

                    // todo: migrate this into one big permission check (does the user have storage AND camera perms?)
                    if (ContextCompat.checkSelfPermission(GameTypeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(GameTypeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                    }

                    // Create a folder for storing images of board games
                    File directory = new File(Environment.getExternalStorageDirectory(), "BoardGameImages");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Save the image to a jpeg
                    File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");
                    OutputStream outputStream;
                    try {
                        // Create the output stream
                        outputStream = new FileOutputStream(imageFile);

                        // Compress the bitmap
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

                        // Close the output stream
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        // Show a (un)helpful toast if any errors occurred along the way
                        Toast.makeText(GameTypeActivity.this, "Couldn't save image! Did you grant the appropriate permissions?", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
                    try {
                        editGameType();
                        finish();
                    }
                    catch (Exception e) {
                        Toast.makeText(GameTypeActivity.this, "Invalid configuration.", Toast.LENGTH_SHORT).show();
                    }
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