package ca.cmpt276.iteration1.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

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

    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int GAME_PLAYED_POSITION_NON_EXISTENT = -1;
    private final int INVALID_SCORE = -1;
    private int gamePlayedPosition;

    private boolean editGameActivity = false;
    private boolean gameCompleted = false;
    private boolean choiceMade = false;

    private GameManager gameManager;
    private String gameTypeString;
    private GameType gameType;
    private PlayedGame playedGame;

    private String difficulty;
    private int playerAmount;
    private int totalScore;
    private String takePhoto;
    private String gamePlayImagePath;

    private ArrayList<Integer> playerScores;

    private EditText etPlayerAmount;
    private RecyclerView rvPlayerScoreInputs;
    private PlayerScoreInputRecyclerViewAdapter recyclerViewAdapter;
    private Dialog showGamePlayImageDialog;

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

        // If we are editing a game, it will give a position relative to the list of
        // games played for a certain type
        if (gamePlayedPosition != GAME_PLAYED_POSITION_NON_EXISTENT){
            editGameActivity = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        gameManager = GameManager.getInstance();
        rvPlayerScoreInputs = findViewById(R.id.rvPlayerScoreInputs);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_new_game);

        extractIntentExtras();
        setupDifficultyButtons();
        setUpPhotoOptionsButtons();

        if (editGameActivity == true){
            actionBar.setTitle(R.string.edit_game);
            setExistingValues();
        }
        if (editGameActivity == false){
            actionBar.setTitle(R.string.create_new_game);
            setDefaultValues();
        }

        setupDifficultyButtons();
        setupPlayerCountInput();
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_add_type_appbar, menu);
        menu.findItem(R.id.btnViewPhoto).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case (R.id.btnSave): {
                try {
                    updatePlayerScoreInputs();
                    updateTotalGameScore();

                    if (gameCompleted == false){
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

            case(R.id.btnViewPhoto): {
                createShowGamePlayImageDialog();
                break;
            }

            case (android.R.id.home): {
                finish();
            }
        }

        return true;
    }

    private void saveNewGame(){
        // Just reinitialize playerScores as we do not want to keep "invisible" data
        playerScores = recyclerViewAdapter.getScores();

        int achievementIndex = gameType.getAchievementIndex(totalScore, playerAmount, difficulty);
        LocalDateTime datePlayed = LocalDateTime.now();
        PlayedGame currentGame = new PlayedGame(gameTypeString, playerAmount, totalScore, achievementIndex, difficulty, playerScores, datePlayed, takePhoto, gamePlayImagePath);
        gameManager.addPlayedGame(currentGame);
    }

    private void saveExistingGame(){
        // Just reinitialize playerScores as we do not want to keep "invisible" data
        playerScores = recyclerViewAdapter.getScores();

        int achievementIndex = gameType.getAchievementIndex(totalScore, playerAmount, difficulty);
        playedGame.editPlayedGame(playerAmount, totalScore, achievementIndex, difficulty, playerScores, takePhoto, gamePlayImagePath);
    }

    private void setupDifficultyButtons(){
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

        highlightSelectedButton(difficulty, difficultyButtons);

        // Choosing player count is hidden by default as a user needs to select a difficulty first
        // If any of these buttons are pressed, enable player count input
        btnDifficultyEasy.setOnClickListener(view -> {
            highlightSelectedButton(btnDifficultyEasy.getTag().toString(), difficultyButtons);

            difficulty = "Easy";
            updateScoreTextView();
        });
        btnDifficultyNormal.setOnClickListener(view -> {
            highlightSelectedButton(btnDifficultyNormal.getTag().toString(), difficultyButtons);

            difficulty = "Normal";
            updateScoreTextView();
        });
        btnDifficultyHard.setOnClickListener(view -> {
            highlightSelectedButton(btnDifficultyHard.getTag().toString(), difficultyButtons);

            difficulty = "Hard";
            updateScoreTextView();
        });
    }

    private void highlightSelectedButton(String selectedButtonTag, ArrayList<Button> buttons){
        for (Button btn : buttons){
            if (btn.getTag().equals(selectedButtonTag)){
                btn.setBackgroundColor(Color.RED);
            }
            else {
                btn.setBackgroundColor(getColor(R.color.purple_500));
            }
        }
    }

    //this function is to set up the yes no button that asking whether the user willing to take a photo to save for the game play
    private void setUpPhotoOptionsButtons() {
        Button btnYes = findViewById(R.id.btnYesToTakePhoto);
        Button btnNo = findViewById(R.id.btnNoToTakePhoto);
        ArrayList<Button> takePhotoButton = new ArrayList<>();

        btnYes.setTag("Yes");
        btnNo.setTag("No");

        takePhotoButton.add(btnYes);
        takePhotoButton.add(btnNo);

        highlightSelectedButton(takePhoto, takePhotoButton);


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightSelectedButton("Yes", takePhotoButton);
                takePhoto = "Yes";
                choiceMade = true;
                startCamera();
                updateScoreTextView();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightSelectedButton("No", takePhotoButton);
                takePhoto = "No";
                choiceMade = true;
                gamePlayImagePath = null;
                updateScoreTextView();
            }
        });
    }

    private void createShowGamePlayImageDialog() {
        showGamePlayImageDialog = new Dialog(GamePlayActivity.this);
        showGamePlayImageDialog.setContentView(R.layout.dialog_show_game_play_image);
        showGamePlayImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showGamePlayImageDialog.show();

        populateShowGamePlayImageDialog();
    }

    private void populateShowGamePlayImageDialog() {
        //Set imageView clickable to let the user retake the photo
        ImageView imageView = showGamePlayImageDialog.findViewById(R.id.ivGamePlayPhoto);
        imageView.setClickable(true);
        imageView.setImageBitmap(getBitmapFromPath(gamePlayImagePath, showGamePlayImageDialog.getContext().getResources()));
        Toast.makeText(this, getString(R.string.tap_to_edit_image), Toast.LENGTH_SHORT).show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGamePlayImageDialog.dismiss();
                startCamera();
            }
        });

        //Set up close button. When the user clicked the close button, the dialog will dismiss.
        Button closeShowGamePlayImageDialog = showGamePlayImageDialog.findViewById(R.id.btnCloseShowGamePlayImageDialog);
        closeShowGamePlayImageDialog.setOnClickListener(view -> {
            showGamePlayImageDialog.dismiss();
        });
    }

    private void startCamera() {
        if (checkStorageAndCameraPermissions() == true){
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityIntent.launch(cameraIntent);
        }
        else {
            Toast.makeText(GamePlayActivity.this,
                            getString(R.string.user_denied_permissions),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent i = result.getData();
            Bundle extras = i.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            createShowGamePlayImageDialog();

            updateGamePhotoImageView(imageBitmap);

            saveImage(imageBitmap);
        }
    });

    private void updateGamePhotoImageView(Bitmap imageBitmap) {
        ImageView iv = showGamePlayImageDialog.findViewById(R.id.ivGamePlayPhoto);
        iv.setImageBitmap(imageBitmap);
    }

    private void saveImage(Bitmap imageBitmap) {
        //Create a folder for storing images of each game play
        File directory = new File(this.getFilesDir(), "gamePlayImage");
        if(!directory.exists()){
            directory.mkdirs();
        }

        //Save the image to jpeg
        File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");
        OutputStream outputStream;
        try{
            //Create the output stream
            outputStream = new FileOutputStream(imageFile);

            //Compress the bitmap
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            //Close the output stream
            outputStream.flush();
            outputStream.close();
            gamePlayImagePath = imageFile.getAbsolutePath();
        }
        catch(Exception e){
            Log.e("Exception: ", e.toString());
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getBitmapFromPath(String imagePath, Resources resources){
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        if(imageBitmap == null){
            imageBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        }
        return imageBitmap;
    }

    private void setDefaultValues(){
        playerAmount = 4;
        difficulty = "Normal";
        playerScores = new ArrayList<Integer>(Collections.nCopies(playerAmount, INVALID_SCORE));
        choiceMade = false;
        updateScoreTextView();
    }

    private void setExistingValues(){
        editGameActivity = true;
        gameCompleted = true;

        gameType = gameManager.getGameTypeFromString(gameTypeString);
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedPosition);

        difficulty = playedGame.getDifficulty();
        playerAmount = playedGame.getNumberOfPlayers();
        totalScore = playedGame.getTotalScore();
        playerScores = playedGame.getPlayerScores();
        gamePlayImagePath = playedGame.getPicturePath();
        takePhoto = playedGame.getTakePhotoOptions();

        choiceMade = true;
        updateScoreTextView();

        LinearLayout linearLayout = findViewById(R.id.LLPhotoOption);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void setupPlayerCountInput(){
        etPlayerAmount = findViewById(R.id.etPlayerCount);
        etPlayerAmount.setText(String.valueOf(playerAmount));
        etPlayerAmount.addTextChangedListener(playerCountInputWatcher);
    }

    private final TextWatcher playerCountInputWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                playerAmount = Integer.parseInt(etPlayerAmount.getText().toString());

                // When the user changes the amount of players, we want to reset the adapter and textview for total score
                // This prevents any old data from persisting and being carried over - basically gives the user a fresh start!
                recyclerViewAdapter = null;
                TextView tvScoreWithAchievementLevel = findViewById(R.id.tvScoreWithAchievementLevel);
                tvScoreWithAchievementLevel.setText(R.string.waiting_player_score_input);

                setupRecyclerView();
                updateTotalGameScore();
            }
            catch (NumberFormatException numberFormatException){
                Log.i("Undefined Player Amount", getString(R.string.waiting_user_new_input));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setupRecyclerView(){
        // Create the models to send into the recyclerview adapter
        ArrayList<PlayerScoreInput> playerScoreInputModels = new ArrayList<>();
        for (int i = 0; i < playerAmount; i++){
            try {
                if (playerScores.get(i) == INVALID_SCORE){
                    playerScoreInputModels.add(new PlayerScoreInput(i));
                }
                if (playerScores.get(i) != INVALID_SCORE){
                    playerScoreInputModels.add(new PlayerScoreInput(i, playerScores.get(i)));
                }
            }
            catch (IndexOutOfBoundsException exception){
                playerScoreInputModels.add(new PlayerScoreInput(i));
            }
        }

        recyclerViewAdapter = new PlayerScoreInputRecyclerViewAdapter(GamePlayActivity.this, playerScoreInputModels, editGameActivity, GamePlayActivity.this);
        rvPlayerScoreInputs.setAdapter(recyclerViewAdapter);
        rvPlayerScoreInputs.setLayoutManager(new LinearLayoutManager(GamePlayActivity.this));
    }

    @Override
    public void updatePlayerScoreInputs() {
        // https://stackoverflow.com/questions/5600668/how-can-i-initialize-an-arraylist-with-all-zeroes-in-java
        if (playerAmount > playerScores.size()){
            playerScores = new ArrayList<>(Collections.nCopies(playerAmount, INVALID_SCORE));
        }
        ArrayList<Integer> inputtedScores = recyclerViewAdapter.getScores();

        for (int i = 0; i < inputtedScores.size(); i++){
            playerScores.set(i, inputtedScores.get(i));
        }

        updateTotalGameScore();
    }

    private void updateTotalGameScore(){
        totalScore = 0;
        gameCompleted = true;

        // We only go up to player amount to not add "invisible" scores
        for (int i = 0; i < playerAmount; i++){
            if (i < playerScores.size()){
                totalScore += playerScores.get(i);
                if (playerScores.get(i) == INVALID_SCORE){
                    // If there is any unfilled data, do not allow user to save
                    gameCompleted = false;
                }
            }
            else {
                totalScore += 0;
            }
        }

        updateScoreTextView();
    }

    public void updateScoreTextView() {
        // If there aren't any players, don't update the score textview
        if (playerAmount == 0) {
            return;
        }

        TextView tvScoreWithAchievementLevel = findViewById(R.id.tvScoreWithAchievementLevel);

        if (!gameCompleted) {
            tvScoreWithAchievementLevel.setText(R.string.waiting_player_score_input);
            return;
        }

        String achievementTitle = gameType.getAchievementLevel(totalScore, playerAmount, difficulty);
        tvScoreWithAchievementLevel.setText(getString(R.string.display_score_only, totalScore));

        // If the user has not denied permissions
        if (checkStorageAndCameraPermissions() == true){
            //If the user still haven't chosen whether they would take photo or not, the achievement is not going to be shown
            LinearLayout linearLayout = findViewById(R.id.LLPhotoOption);
            linearLayout.setVisibility(View.VISIBLE);

            if(choiceMade){
                tvScoreWithAchievementLevel.setText(getString(R.string.display_score_and_achievement, totalScore, achievementTitle));
            }
        }
        else {
            tvScoreWithAchievementLevel.setText(getString(R.string.display_score_and_achievement, totalScore, achievementTitle));
        }

    }

    //Code from https://developer.android.com/codelabs/camerax-getting-started#1
    private boolean checkStorageAndCameraPermissions(){
        // If either camera permissions or storage permissions are missing, request permissions again
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(GamePlayActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

}