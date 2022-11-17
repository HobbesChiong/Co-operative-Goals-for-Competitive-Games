package ca.cmpt276.iteration1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.interfaces.PlayerScoreInputRecyclerViewInterface;
import ca.cmpt276.iteration1.model.PlayerScoreInput;


// Citation: https://www.youtube.com/watch?v=O9hdu8i-u9c
public class PlayerScoreInputRecyclerViewAdapter extends RecyclerView.Adapter<PlayerScoreInputRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<PlayerScoreInput> playerCountCards;
    private PlayerScoreInputRecyclerViewInterface recyclerViewInterface;

    public PlayerScoreInputRecyclerViewAdapter(Context context, ArrayList<PlayerScoreInput> playerCountCards, PlayerScoreInputRecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.playerCountCards = playerCountCards;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    // Viewholder for recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from our recyclerview layout file
        // Can think of this as an onCreate method

        TextView tvPlayerNumber;
        EditText etPlayerScoreInput;

        public MyViewHolder(@NonNull View itemView) {
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

        return new PlayerScoreInputRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerScoreInputRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assigning values ot the views we created in the recyclerview card layout file
        // based on the position of the recycler view


    }

    @Override
    public int getItemCount() {
        return playerCountCards.size();
    }
}
