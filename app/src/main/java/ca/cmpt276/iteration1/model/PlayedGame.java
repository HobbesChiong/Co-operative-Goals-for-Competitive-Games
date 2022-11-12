package ca.cmpt276.iteration1.model;
/*
* The PlayedGame class is for storing played game's data.
* type for game name
* numberOfPlayers for #Players
* score for the total score of the whole team game
* achievement for the achievement that the team got according to their score.
* */

import androidx.annotation.NonNull;

import ca.cmpt276.iteration1.R;

public class PlayedGame {
    private final String type;
    private int numberOfPlayers;
    private int score;
    private String achievement;

    public PlayedGame(String type, int numberOfPlayers, int score, String achievement) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
        this.achievement = achievement;
    }

    public String getType() {
        return type;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getScore() {
        return score;
    }

    public String getAchievement(){
        return achievement;
    }

    public void editPlayedGame(int numberOfPlayers, int score, String achievement){
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
        this.achievement = achievement;
    }

    @NonNull
    @Override
    public String toString() {
        String output;
        output = "Score: " + score + ", " + numberOfPlayers + "Players, " + achievement;
        return output;
    }
}
