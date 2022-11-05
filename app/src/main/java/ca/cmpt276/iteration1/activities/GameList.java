package ca.cmpt276.iteration1.activities;

import static android.util.Log.DEBUG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;


/**
 * Activity which lists possible game configurations.
 * Has a FAB which lets the user launch into an activity for creating a new game configuration.
 */
public class GameList extends AppCompatActivity {

    GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gm = GameManager.getInstance();

        loadGameTypeList();
        setUpFab();
        populateListView();
        ListView lv = findViewById(R.id.lv_gameTypeList);

        setTitle(getString(R.string.Game_Types));

        // Single tap will open up list of games played for specific game type
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = GamePlayed.makeIntent(GameList.this, position);
                startActivity(intent);
            }
        });

        // Single tap and hold will open up edit game configuration screen
        // https://stackoverflow.com/questions/8846707/how-to-implement-a-long-click-listener-on-a-listview
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = GameTypeActivity.makeIntent(GameList.this, gm.getGameTypes().get(position).getGameType());
                startActivity(intent);

                return true;
            }
        });

        Log.println(DEBUG,"","Reached end of onCrete");
    }

    // When back button on home bar is pressed
    @Override
    public void onBackPressed(){
        saveGameTypeList();
        finishAffinity();
    }

    // When the activity is resumed, save any changes to the game types list
    @Override
    public void onResume() {
        saveGameTypeList();
        populateListView();
        TextView emptyState = findViewById(R.id.tvGameListEmptyState);
        // No games types are added in yet
        if (gm.getGameTypes().isEmpty()) {
            emptyState.setText(R.string.emptyStateGameType);
        } else {
            // instructions when game list is not empty
            emptyState.setText(R.string.open_edit_game_instruction);
        }
        super.onResume();
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab_addGame);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameList.this, GameTypeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        // Get list of game type names
        ArrayList<String> gameTypesList = new ArrayList<>();

        // Get the names of every game type
        for (GameType game : gm.getGameTypes()) {
            gameTypesList.add(game.getGameType());
        }

        // Pack the game names into the listview
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.game_type_list, gameTypesList);
        ListView lv = findViewById(R.id.lv_gameTypeList);
        lv.setAdapter(adapter);
    }

    // Save Game type list
    private void saveGameTypeList(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game Type Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(gm.getGameTypes());

        editor.putString("Game Type List", json);
        editor.apply();
    }

    // Load Game type list
    private void loadGameTypeList() {
        gm = GameManager.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("Game Type Preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("Game Type List", null);
        Type type = new TypeToken<ArrayList<GameType>>() {}.getType();

        if (json == null){
            return; // keep the old instance of game manager if no save exists
        }

        // Set retrieved data if not null to game manager's game type list
        gm.loadGameTypeList(gson.fromJson(json, type));
    }
}