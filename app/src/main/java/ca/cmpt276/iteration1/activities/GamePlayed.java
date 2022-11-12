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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.model.RecyclerViewAdapter;
import ca.cmpt276.iteration1.model.RecyclerViewInterface;


/**
 * Activity which lists games played for a specific type of game.
 */
public class GamePlayed extends AppCompatActivity implements RecyclerViewInterface {

    private static final String GAME_TYPE_INDEX = "Position";
    // Index of which type of game we're dealing with
    private int gameTypeIndex;

    private GameManager gm;
    private String gameTypeString;
    private GameType gameType;
    private RecyclerViewAdapter adapter;

    private TextView achievementLevels;
    private EditText achievementLevelPlayerCount;

    public static Intent makeIntent(Context context, int pos){
        Intent intent = new Intent(context, GamePlayed.class);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameType t = gm.getGameTypeAtIndex(gameTypeIndex);
                String gameType = t.getGameType();

                Intent intent = NewGameCreationScreen.makeIntent(GamePlayed.this, gameType);
                startActivity(intent);
            }
        });
    }

    private void saveGamesPlayedList(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(gm.getPlayedGames());

        editor.putString("Game Played List", json);
        editor.apply();
    }

    private void loadGamesPlayedList() {
        gm = GameManager.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("Game Played List", null);
        Type type = new TypeToken<ArrayList<PlayedGame>>() {}.getType();

        if (json == null){
            return; // keep the old instance of game manager if no save exists
        }

        // Set retrieved data if not null to game manager's playedGame list
        gm.loadGamePlayedList(gson.fromJson(json, type));
    }

    private void createAchievementLevelDialog() {
        Dialog achievementLevelsDialog = new Dialog(GamePlayed.this);
        achievementLevelsDialog.setContentView(R.layout.dialog_view_achievement_levels);
        achievementLevelsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        achievementLevelsDialog.show();

        gameType = gm.getGameTypeFromString(this.gameTypeString);

        achievementLevels = achievementLevelsDialog.findViewById(R.id.tvAchievementLevels);

        achievementLevelPlayerCount = achievementLevelsDialog.findViewById(R.id.etAchievementLevelPlayerCount);
        achievementLevelPlayerCount.addTextChangedListener(playerCountInputWatcher);

        Button closeAchievementLevelsDialog = achievementLevelsDialog.findViewById(R.id.btnCloseAchievementLevelsDialog);
        closeAchievementLevelsDialog.setOnClickListener(view -> {
            achievementLevelsDialog.dismiss();
        });
    }

    // https://stackoverflow.com/questions/8543449/how-to-use-the-textwatcher-class-in-android
    private final TextWatcher playerCountInputWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                int playerCount = Integer.parseInt(achievementLevelPlayerCount.getText().toString());

                // If user enters in 0 players
                if (playerCount == 0){
                    achievementLevels.setText(R.string.invalid_num_of_player);
                }
                else {
                    String message = "";
                    for (String line : gameType.getAchievementLevelScoreRequirements(playerCount)) {
                        message += line + "\n";
                    }
                    achievementLevels.setText(message);
                }
            }
            catch (NumberFormatException numberFormatException){
                achievementLevels.setText(R.string.waiting_for_input);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /*
* Code inspired by blog post on 29 Oct, 2022 from https://thumbb13555.pixnet.net/blog/post/311803031-%E7%A2%BC%E8%BE%B2%E6%97%A5%E5%B8%B8-%E3%80%8Eandroid-studio%E3%80%8F%E5%9F%BA%E6%9C%ACrecyclerview%E7%94%A8%E6%B3%95
* */
    private void populateRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_gameHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapter(GamePlayed.this, GamePlayed.this, gameTypeString);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = NewGameCreationScreen.makeIntent(GamePlayed.this, gameTypeString, position);
        startActivity(intent);
    }
}