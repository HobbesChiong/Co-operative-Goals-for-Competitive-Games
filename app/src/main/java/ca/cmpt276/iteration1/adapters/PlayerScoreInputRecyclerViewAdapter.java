package ca.cmpt276.iteration1.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.interfaces.PlayerScoreInputRecyclerViewInterface;
import ca.cmpt276.iteration1.model.PlayerScoreInput;


// Citation: https://www.youtube.com/watch?v=O9hdu8i-u9c
public class PlayerScoreInputRecyclerViewAdapter extends RecyclerView.Adapter<PlayerScoreInputRecyclerViewAdapter.MyViewHolder> {

    private final String PLAYER_SCORE_EDIT_TEXT_KEY = "id";

    private Context context;
    private boolean editGame;
    private ArrayList<PlayerScoreInput> playerScoreInputs;
    private ArrayList<Integer> playerScoreInputIds = new ArrayList<>();
    private PlayerScoreInputRecyclerViewInterface recyclerViewInterface;

    public PlayerScoreInputRecyclerViewAdapter(Context context, ArrayList<PlayerScoreInput> playerScoreInputs, boolean editGame, PlayerScoreInputRecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.playerScoreInputs = playerScoreInputs;
        this.editGame = editGame;
        this.recyclerViewInterface = recyclerViewInterface;
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
        int id = createCustomId(playerScoreInputs.get(position).getPlayerId());
        holder.etPlayerScoreInput.setId(id);
        holder.etPlayerScoreInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(context, "Player " + (position + 1) + " (ID: " + id + ") has been updated.", Toast.LENGTH_SHORT).show();
                recyclerViewInterface.checkAllPlayerScoreInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        playerScoreInputIds.add(id);
    }

    private int createCustomId(int playerId){
        // We are dynamically creating edit texts
        // Create unique custom id's for each one so they can be accessed from classes other than this one

        StringBuilder stringKey = new StringBuilder();
        for (int i = 0; i < PLAYER_SCORE_EDIT_TEXT_KEY.length(); i++){
            stringKey.append(PLAYER_SCORE_EDIT_TEXT_KEY.charAt(i) - 'a' + 1);
        }
        stringKey.append(String.valueOf(playerId));

        String sbString = stringKey.toString().replaceAll("\\D", "");
        return Integer.parseInt(sbString);
    }

    @Override
    public int getItemCount() {
        return playerScoreInputs.size();
    }

    public ArrayList<Integer> getPlayerScoreInputIds(){
        return playerScoreInputIds;
    }
}
