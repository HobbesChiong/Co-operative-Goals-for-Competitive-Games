package ca.cmpt276.iteration1.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.activities.GamePlayActivity;
import ca.cmpt276.iteration1.interfaces.PlayerScoreInputRecyclerViewInterface;
import ca.cmpt276.iteration1.model.PlayerScoreInput;


// Citation: https://www.youtube.com/watch?v=O9hdu8i-u9c
public class PlayerScoreInputRecyclerViewAdapter extends RecyclerView.Adapter<PlayerScoreInputRecyclerViewAdapter.MyViewHolder> {

    private final String PLAYER_SCORE_EDIT_TEXT_KEY = "id";
    private final int INVALID_SCORE = -1;

    private Context context;
    private boolean editGame;
    private ArrayList<PlayerScoreInput> playerScoreInputs;
    private PlayerScoreInputRecyclerViewInterface recyclerViewInterface;

    // Stores the data in the edit text for all the recyclerviews
    private int[] recyclerViewEditTextData;

    public PlayerScoreInputRecyclerViewAdapter(Context context, ArrayList<PlayerScoreInput> playerScoreInputs, boolean editGame, PlayerScoreInputRecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.playerScoreInputs = playerScoreInputs;
        this.editGame = editGame;
        this.recyclerViewInterface = recyclerViewInterface;

        recyclerViewEditTextData = new int[playerScoreInputs.size()];

        Arrays.fill(recyclerViewEditTextData, INVALID_SCORE);

        // If we're editing the game, load all our previous scores into the list of data
        if (editGame) {
            for (int i = 0; i < playerScoreInputs.size(); i ++) {
                int playerScore = playerScoreInputs.get(i).getPlayerScore();
                recyclerViewEditTextData[i] = playerScore;
            }
        }
    }

    // Viewholder for recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from our recyclerview layout file
        // Can think of this as an onCreate method

        TextView tvPlayerNumber;
        EditText etPlayerScoreInput;

        public MyViewHolder(@NonNull View itemView, PlayerScoreInputRecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            tvPlayerNumber = itemView.findViewById(R.id.tvPlayerNumber);
            etPlayerScoreInput = itemView.findViewById(R.id.etPlayerScoreInput);
        }
    }

    @NonNull
    @Override
    public PlayerScoreInputRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (giving the look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.player_score_input_card, parent, false);

        return new PlayerScoreInputRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerScoreInputRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Assigning values ot the views we created in the recyclerview card layout file
        // based on the position of the recycler view
        holder.tvPlayerNumber.setText("Player " + (position + 1));

        // As we are creating new cards on the spot, we need to assign them IDs that will stay persistent throughout the activity
        int id = createCustomId(position);
        holder.etPlayerScoreInput.setId(id);

        // Load in the previous value the edit text had
        int playerScore = recyclerViewEditTextData[position];
        if (playerScore != INVALID_SCORE) {
            holder.etPlayerScoreInput.setText(String.valueOf(playerScore));
        }


        holder.etPlayerScoreInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Toast.makeText(context, "Player " + (position + 1) + " (ID: " + id + ") has been updated.", Toast.LENGTH_SHORT).show();
                Log.i("Something", id + " has changed");

                // Try to record the current value of the score if it exists
                try {
                    int scoreValue = Integer.parseInt(holder.etPlayerScoreInput.getText().toString());
                    recyclerViewEditTextData[position] = scoreValue;
                } catch (Exception e){
                    recyclerViewEditTextData[position] = INVALID_SCORE;
                }


                recyclerViewInterface.checkAllPlayerScoreInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.setIsRecyclable(false);
    }

    private int createCustomId(int position){
        // We are dynamically creating edit texts
        // Create unique custom id's for each one so they can be accessed from classes other than this one

        StringBuilder stringKey = new StringBuilder();
        for (int i = 0; i < PLAYER_SCORE_EDIT_TEXT_KEY.length(); i++){
            stringKey.append(PLAYER_SCORE_EDIT_TEXT_KEY.charAt(i) - 'a' + 1);
        }
        stringKey.append(position);

        String sbString = stringKey.toString().replaceAll("\\D", "");
        return Integer.parseInt(sbString);
    }

    @Override
    public int getItemCount() {
        return playerScoreInputs.size();
    }

    public ArrayList<Integer> getScores() {
        // Create a list of scores
        ArrayList<Integer> score = new ArrayList<>();
        for (int i : recyclerViewEditTextData) {
            // One of the score fields hasn't been filled out, return nothing!
            if (i == INVALID_SCORE) {
                return null;
            }

            // Otherwise, add the current score to our list of scores
            score.add(i);
        }
        return score;
    }
}
