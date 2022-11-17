package ca.cmpt276.iteration1.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


// Citation: https://www.youtube.com/watch?v=O9hdu8i-u9c
public class PlayerScoreInputRecyclerViewAdapter extends RecyclerView.Adapter<PlayerScoreInputRecyclerViewAdapter.MyViewHolder> {

    // Viewholder for recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public PlayerScoreInputRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerScoreInputRecyclerViewAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
