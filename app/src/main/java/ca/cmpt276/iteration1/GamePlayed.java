package ca.cmpt276.iteration1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.PlayedGame;

public class GamePlayed extends AppCompatActivity {

    private final String POSITION = "Position";
    private int position;
    GameManager gm = GameManager.getInstance();
    private final List<PlayedGame> gameHistory= new ArrayList<>();
    NewListAdapter adapter;

    public Intent makeIntent(Context context, int pos){
        Intent intent = new Intent(context, GamePlayed.class);
        intent.putExtra(POSITION, pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_played);
        extractDataFromIntent();
        setUpFab();
        createList();
        populateRecyclerView();
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