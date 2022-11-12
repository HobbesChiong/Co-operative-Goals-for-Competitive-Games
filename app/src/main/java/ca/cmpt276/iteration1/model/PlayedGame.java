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
    private int achievementIndex;
    private final String difficulty;

    public PlayedGame(String type, int numberOfPlayers, int score, int achievementIndex, String difficulty) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
        this.achievementIndex = achievementIndex;
        this.difficulty = difficulty;
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

    public String getAchievement() {
        GameManager gameManager = GameManager.getInstance();
        return GameType.getAchievementName(achievementIndex, gameManager.getAchievementTheme());
    }

    public void editPlayedGame(int numberOfPlayers, int score, int achievementIndex){
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
        this.achievementIndex = achievementIndex;
    }

    @NonNull
    @Override
    public String toString() {
        String output;
        output = "Score: " + score + ", " + numberOfPlayers + "Players, " + getAchievement();
        return output;
    }
}
