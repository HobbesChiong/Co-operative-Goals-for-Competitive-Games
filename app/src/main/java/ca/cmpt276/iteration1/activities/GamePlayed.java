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
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;

public class GamePlayed extends AppCompatActivity {

    private static final String POSITION = "Position";
    private int position;
    GameManager gm;
    private final List<PlayedGame> gameHistory= new ArrayList<>();
    NewListAdapter adapter;

    public static Intent makeIntent(Context context, int pos){
        Intent intent = new Intent(context, GamePlayed.class);
        intent.putExtra(POSITION, pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_played);

        gm = GameManager.getInstance();

        loadGamesPlayedList();

        extractDataFromIntent();
        setUpFab();
        createList();
        populateRecyclerView();
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
        position = intent.getIntExtra(POSITION, 0);
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab_addGame);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GamePlayed.this, NewGameCreationScreen.class);

                intent.putExtra("GameType", gm.getGameTypes().get(position).getType());
                startActivity(intent);
            }
        });
    }

    private void createList() {
        int size = gm.getPlayedGames().size();
        String type = gm.getPlayedGames().get(position).getType();
        for(int i = 0; i < size; i++){
            if(gm.getPlayedGames().get(i).getType().equals(type)){
                gameHistory.add(gm.getPlayedGames().get(i));
            }
        }
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