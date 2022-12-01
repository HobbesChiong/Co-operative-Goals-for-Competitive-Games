package ca.cmpt276.iteration1.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.activities.GamePlayActivity;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;
import ca.cmpt276.iteration1.interfaces.GamePlayedListRecyclerViewInterface;

/**
 * Code inspired by blog post on 29 Oct, 2022 from https://thumbb13555.pixnet.net/blog/post/311803031-%E7%A2%BC%E8%BE%B2%E6%97%A5%E5%B8%B8-%E3%80%8Eandroid-studio%E3%80%8F%E5%9F%BA%E6%9C%ACrecyclerview%E7%94%A8%E6%B3%95
 * Citation: https://www.youtube.com/watch?v=7GPUpvcU1FE
 * Custom RecyclerView Adapter for displaying the game history in GamePlayedListActivity
 * Display all played game history with the chosen game type.
 * */

public class GamePlayedListRecyclerViewAdapter extends RecyclerView.Adapter<GamePlayedListRecyclerViewAdapter.ViewHolder> {

    private final GamePlayedListRecyclerViewInterface recyclerViewInterface;

    Context context;

    GameManager gameManager;
    GameType gameType;
    ArrayList<PlayedGame> playedGames;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd @ hh.mm a");

    public GamePlayedListRecyclerViewAdapter(Context context, GamePlayedListRecyclerViewInterface recyclerViewInterface, String gameTypeString){
        this.gameManager = GameManager.getInstance();
        this.gameType = gameManager.getGameTypeFromString(gameTypeString);
        this.playedGames = gameManager.getSpecificPlayedGames(gameTypeString);
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gamePlayImage;
        TextView dScore, dNoOfPlayer, dAchievement, dDifficulty, dDatePlayed;

        public ViewHolder(@NonNull View itemView, GamePlayedListRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            gamePlayImage = itemView.findViewById(R.id.iv_showGamePlayImage);
            dScore = itemView.findViewById(R.id.tvDisplayScore);
            dNoOfPlayer = itemView.findViewById(R.id.tvDisplayNoOfPlayer);
            dAchievement = itemView.findViewById(R.id.tvDisplayAchievement);
            dDifficulty = itemView.findViewById(R.id.tvDisplayDifficulty);
            dDatePlayed = itemView.findViewById(R.id.tvDatePlayed);

            itemView.setOnLongClickListener(view -> {
                Log.i("Tag", "something has been long clicked");
                if (recyclerViewInterface != null){
                    int position = getAbsoluteAdapterPosition();

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemLongClick(position);
                        return true;
                    }
                }
                return true;
            });
        }
    }

    @NonNull
    @Override
    public GamePlayedListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_history_list_layout, parent, false);
        return new GamePlayedListRecyclerViewAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.gamePlayImage.setImageBitmap(GamePlayActivity.getBitmapFromPath(playedGames.get(position).getPicturePath(), context.getResources()));
        holder.dScore.setText(String.valueOf(playedGames.get(position).getTotalScore()));
        holder.dNoOfPlayer.setText(String.valueOf(playedGames.get(position).getNumberOfPlayers()));
        holder.dAchievement.setText(playedGames.get(position).getAchievement());
        holder.dDifficulty.setText(playedGames.get(position).getDifficulty());
        holder.dDatePlayed.setText("Date played: " + playedGames.get(position).getDatePlayed().format(dateTimeFormatter));
    }

    @Override
    public int getItemCount() {
        return playedGames.size();
    }

}