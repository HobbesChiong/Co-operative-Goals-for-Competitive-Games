package ca.cmpt276.iteration1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;

public class GameList extends AppCompatActivity {
    GameManager gm = GameManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        setUpFab();
        populateListView();
        ListView lv = findViewById(R.id.lv_gameTypeList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = GamePlayed.makeIntent(GameList.this, position);
                startActivity(intent);
            }
        });
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
        ArrayAdapter<GameType> adapter = new ArrayAdapter<>(this, R.layout.game_type_list, gm.getGameTypes());
        ListView lv = findViewById(R.id.lv_gameTypeList);
        lv.setAdapter(adapter);
    }
}