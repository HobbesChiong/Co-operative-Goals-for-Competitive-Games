package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.PlayedGame;

public class GamePlayed extends AppCompatActivity {

    private static final String GAME_TYPE_INDEX = "Position";
    // Index of which type of game we're dealing with
    private int gameTypeIndex;
    private GameManager gm;
    private List<PlayedGame> gameHistory= new ArrayList<>();
    private String gameType;
    private NewListAdapter adapter;

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

        extractDataFromIntent();
        loadGamesPlayedList();
        setUpFab();
        createList();
        populateRecyclerView();

        setTitle(gameType);
    }

    @Override
    public void onBackPressed(){
        saveGamesPlayedList();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        saveGamesPlayedList();
        super.onResume();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        gameTypeIndex = intent.getIntExtra(GAME_TYPE_INDEX, 0);

        // Retrieve the type of game we're playing as
        gameType = gm.getGameTypeAtIndex(gameTypeIndex).getType();
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab_addGame);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GamePlayed.this, NewGameCreationScreen.class);

                intent.putExtra("GameType", gm.getGameTypes().get(gameTypeIndex).getType());
                startActivity(intent);
            }
        });
    }

    private void createList() {
        gameHistory = gm.getSpecificPlayedGames(gameType);
    }

    private void saveGamesPlayedList(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(gm.getGameTypes());

        editor.putString("Game Played List", json);
        editor.apply();
    }

    private void loadGamesPlayedList(){
        gm = GameManager.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("Game Played Preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("Game Played List", null);
        Type type = new TypeToken<ArrayList<PlayedGame>>() {}.getType();

        if (json == null){
            return; // keep the old instance of game manager if no save exists
        }

        // Set retreived data if not null to game manager's game type list
        gm.loadGameTypeList(gson.fromJson(json, type));
    }

/*
* Code inspired by blog post on 29 Oct, 2022 from https://thumbb13555.pixnet.net/blog/post/311803031-%E7%A2%BC%E8%BE%B2%E6%97%A5%E5%B8%B8-%E3%80%8Eandroid-studio%E3%80%8F%E5%9F%BA%E6%9C%ACrecyclerview%E7%94%A8%E6%B3%95
* */
    private void populateRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_gameHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new NewListAdapter();
        rv.setAdapter(adapter);
    }
    private class NewListAdapter extends RecyclerView.Adapter<NewListAdapter.ViewHolder>{
        class ViewHolder extends RecyclerView.ViewHolder{
            private final TextView dScore, dNoOfPlayer, dAchievement;
            public ViewHolder(@NonNull View itemView){
                super(itemView);
                dScore = itemView.findViewById(R.id.tvDisplayScore);
                dNoOfPlayer = itemView.findViewById(R.id.tvDisplayNoOfPlayer);
                dAchievement = itemView.findViewById(R.id.tvDisplayAchievement);
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_history_list_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.dScore.setText(String.valueOf(gameHistory.get(position).getScore()));
            holder.dNoOfPlayer.setText(String.valueOf(gameHistory.get(position).getNumberOfPlayers()));
            holder.dAchievement.setText(gameHistory.get(position).getAchievement());
        }

        @Override
        public int getItemCount() {
            return gameHistory.size();
        }
    }
}