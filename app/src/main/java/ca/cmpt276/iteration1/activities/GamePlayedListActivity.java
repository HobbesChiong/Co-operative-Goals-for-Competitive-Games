package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.adapters.GamePlayedListRecyclerViewAdapter;
import ca.cmpt276.iteration1.interfaces.GamePlayedListRecyclerViewInterface;


/**
 * Activity which lists games played for a specific type of game.
 */
public class GamePlayedListActivity extends AppCompatActivity implements GamePlayedListRecyclerViewInterface {

    private static final String GAME_TYPE_INDEX = "Position";

    // Index of which type of game we're dealing with
    private int gameTypeIndex;

    private GameManager gm;
    private String gameTypeString;
    private GameType gameType;
    private GamePlayedListRecyclerViewAdapter adapter;

    // Fields for the achievement levels dialog
    private Dialog achievementLevelsDialog;
    private Dialog statisticsDialog;
    private TextView achievementLevels;
    private EditText achievementLevelPlayerCount;
    private String dialogSelectedDifficulty = "Normal";
    private ArrayList<Button> difficultyButtons;

    public static Intent makeIntent(Context context, int pos){
        Intent intent = new Intent(context, GamePlayedListActivity.class);
        intent.putExtra(GAME_TYPE_INDEX, pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_played);

        gm = GameManager.getInstance();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        extractDataFromIntent();
        loadGamesPlayedList();
        setUpFab();
        populateRecyclerView();

        setTitle(gameTypeString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add button to view achievement levels
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_appbar_gameplayed, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        switch(item.getItemId()){
            case (R.id.btnViewAchievementLevels): {
                createAchievementLevelDialog();
                break;
            }
            case (R.id.btnStatistics):{
                createStatisticsDialog();
                break;
            }
            case (android.R.id.home): {
                finish();
                break;
            }
        }

        return true;
    }


    @Override
    public void onBackPressed(){
        saveGamesPlayedList();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        saveGamesPlayedList();

        // if no games are made in the current type yet show empty state otherwise set the text view to blank.
        TextView gamePlayedEmptyState = findViewById(R.id.tvGamePlayedEmptyState);
        if(gm.getSpecificPlayedGames(gameTypeString).isEmpty()) {
            gamePlayedEmptyState.setText(R.string.empty_game_state);

        }
        else{
            gamePlayedEmptyState.setText(R.string.blank);
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        gm = GameManager.getInstance();
        populateRecyclerView();
        super.onStart();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        gameTypeIndex = intent.getIntExtra(GAME_TYPE_INDEX, 0);

        // Retrieve the type of game we're playing as
        gameTypeString = gm.getGameTypeAtIndex(gameTypeIndex).getGameType();
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab_addGame);
        fab.setOnClickListener(v -> {
            Intent intent = GamePlayActivity.makeIntent(GamePlayedListActivity.this, gameTypeString);
            startActivity(intent);
        });
    }

    private void saveGamesPlayedList(){
        // https://www.youtube.com/watch?v=jcliHGR3CHo
        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html
        // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/time/LocalDateTime.html
        // https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.1/com/google/gson/TypeAdapter.html
        // https://www.javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/stream/JsonWriter.html
        // https://stackoverflow.com/questions/61432170/how-to-serialize-localdate-using-gson

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).setPrettyPrinting().create();

        String json = gson.toJson(gm.getPlayedGames());

        editor.putString("Game Played List", json);
        editor.apply();
    }

    private void loadGamesPlayedList() {
        gm = GameManager.getInstance();

        // https://www.youtube.com/watch?v=jcliHGR3CHo
        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);

        // https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html
        // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/time/LocalDateTime.html
        // https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.1/com/google/gson/TypeAdapter.html
        // https://www.javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/stream/JsonWriter.html
        // https://stackoverflow.com/questions/61432170/how-to-serialize-localdate-using-gson

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).setPrettyPrinting().create();

        String json = sharedPreferences.getString("Game Played List", null);
        Type type = new TypeToken<ArrayList<PlayedGame>>() {}.getType();

        if (json == null){
            return; // keep the old instance of game manager if no save exists
        }

        // Set retrieved data if not null to game manager's playedGame list
        gm.loadGamePlayedList(gson.fromJson(json, type));
    }

    private void createAchievementLevelDialog() {
        achievementLevelsDialog = new Dialog(GamePlayedListActivity.this);
        achievementLevelsDialog.setContentView(R.layout.dialog_view_achievement_levels);
        achievementLevelsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        achievementLevelsDialog.show();

        gameType = gm.getGameTypeFromString(this.gameTypeString);

        Button btnDialogDifficultyEasy = achievementLevelsDialog.findViewById(R.id.btnDialogDifficultyEasy);
        Button btnDialogDifficultyNormal = achievementLevelsDialog.findViewById(R.id.btnDialogDifficultyNormal);
        Button btnDialogDifficultyHard = achievementLevelsDialog.findViewById(R.id.btnDialogDifficultyHard);

        difficultyButtons = new ArrayList<>();
        difficultyButtons.add(btnDialogDifficultyEasy);
        difficultyButtons.add(btnDialogDifficultyNormal);
        difficultyButtons.add(btnDialogDifficultyHard);

        // Since the normal difficulty is selected by default, just set the normal one to black
        btnDialogDifficultyNormal.setBackgroundColor(Color.BLACK);

        btnDialogDifficultyEasy.setOnClickListener(view -> {
            highlightSelectedDifficultyButton(btnDialogDifficultyEasy.getId());

            dialogSelectedDifficulty = "Easy";
            setAchievementLevelsText();
            Toast.makeText(GamePlayedListActivity.this, "Selected \"Easy\" difficulty multiplier.", Toast.LENGTH_SHORT).show();
        });
        btnDialogDifficultyNormal.setOnClickListener(view -> {
            highlightSelectedDifficultyButton(btnDialogDifficultyNormal.getId());

            dialogSelectedDifficulty = "Normal";
            setAchievementLevelsText();
            Toast.makeText(GamePlayedListActivity.this, "Selected \"Normal\" difficulty multiplier.", Toast.LENGTH_SHORT).show();
        });
        btnDialogDifficultyHard.setOnClickListener(view -> {
            highlightSelectedDifficultyButton(btnDialogDifficultyHard.getId());

            dialogSelectedDifficulty = "Hard";
            setAchievementLevelsText();
            Toast.makeText(GamePlayedListActivity.this, "Selected \"Hard\" difficulty multiplier.", Toast.LENGTH_SHORT).show();
        });

        achievementLevelPlayerCount = achievementLevelsDialog.findViewById(R.id.etAchievementLevelPlayerCount);
        achievementLevelPlayerCount.addTextChangedListener(new TextWatcher() {
            // https://stackoverflow.com/questions/8543449/how-to-use-the-textwatcher-class-in-android
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setAchievementLevelsText();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button closeAchievementLevelsDialog = achievementLevelsDialog.findViewById(R.id.btnCloseAchievementLevelsDialog);
        closeAchievementLevelsDialog.setOnClickListener(view -> {
            achievementLevelsDialog.dismiss();
        });
    }

    private void createStatisticsDialog() {
        statisticsDialog = new Dialog(GamePlayedListActivity.this);
        statisticsDialog.setContentView(R.layout.dialog_statistics);
        statisticsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        statisticsDialog.show();

        gameType = gm.getGameTypeFromString(this.gameTypeString);
        populateStatisticsDialog();
    }

    /**
     * for each played game in specific played game create an array with indexes as achievement index and arr[i] as amt of achievements
     * Stores strings in format "achievment: 4" for adapter then displays on list view
     * O(n) n = specificPlayedGames size
     */
    private void populateStatisticsDialog(){
        // for each played game in specific played game create an array with indexes as achievement index and arr[i] as amt of achievements O(n + k) run time vs O(8n)
        ArrayList<PlayedGame> specificPlayedGames = gm.getSpecificPlayedGames(this.gameTypeString);
        int[] achievementsEarnedInts = new int[8];
        String[] achievementsEarnedStrings = new String[8];
        int achievementTheme = gm.getAchievementTheme();

        // initialize each index to have 0 achievements earned instead of garbage/null values
        for(int i = 0; i < 8; i++) {
            achievementsEarnedInts[i] = 0;
        }

        // adds 1 for each achievement earned per currGame
        for(PlayedGame currGame : specificPlayedGames){
            achievementsEarnedInts[currGame.getAchievementIndex()] += 1;
        }

        // stores the strings for adapter
        for(int i = 0; i < 8; i++){
            achievementsEarnedStrings[i] = gameType.getSpecificAchievement(achievementTheme,i) + ": " + achievementsEarnedInts[i];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.statistics_list, achievementsEarnedStrings);
        ListView list = statisticsDialog.findViewById(R.id.lvStatistics);
        list.setAdapter(adapter);
    }

    private void highlightSelectedDifficultyButton(int selectedDifficultyButtonId){
        for (Button difficultyButton : difficultyButtons){
            if (difficultyButton.getId() == selectedDifficultyButtonId){
                difficultyButton.setBackgroundColor(Color.BLACK);
            }
            else {
                difficultyButton.setBackgroundColor(getColor(R.color.purple_500));
            }
        }
    }

    private void setAchievementLevelsText(){
        achievementLevels = achievementLevelsDialog.findViewById(R.id.tvAchievementLevels);
        try {
            int playerCount = Integer.parseInt(achievementLevelPlayerCount.getText().toString());

            // If user enters in 0 players
            if (playerCount == 0){
                achievementLevels.setText(R.string.invalid_num_of_player);
            }
            else {
                String message = "";
                for (String line : gameType.getAchievementLevelScoreRequirements(playerCount, dialogSelectedDifficulty)) {
                    message += line + "\n";
                }
                achievementLevels.setText(message);
            }
        }
        catch (NumberFormatException numberFormatException){
            achievementLevels.setText(R.string.waiting_for_input);
        }
    }

    /*
* Code inspired by blog post on 29 Oct, 2022 from https://thumbb13555.pixnet.net/blog/post/311803031-%E7%A2%BC%E8%BE%B2%E6%97%A5%E5%B8%B8-%E3%80%8Eandroid-studio%E3%80%8F%E5%9F%BA%E6%9C%ACrecyclerview%E7%94%A8%E6%B3%95
* */
    private void populateRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_gameHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new GamePlayedListRecyclerViewAdapter(GamePlayedListActivity.this, GamePlayedListActivity.this, gameTypeString);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position){

    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = GamePlayActivity.makeIntent(GamePlayedListActivity.this, gameTypeString, position);
        startActivity(intent);
    }
}