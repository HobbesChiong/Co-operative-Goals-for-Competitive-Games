package ca.cmpt276.iteration1.model;
/*
* The PlayedGame class is for storing played game's data.
* type for game name
* numberOfPlayers for #Players
* score for the total score of the whole team game
* achievement for the achievement that the team got according to their score.
* */

import androidx.annotation.NonNull;

/**
 * Class representing a game that the player has already played.
 */
public class PlayedGame {
    private final String type;
    private int numberOfPlayers;
    private int score;

    // The index of the achievement that was achieved
    private int achievementIndex;

    public PlayedGame(String type, int numberOfPlayers, int score, int achievementIndex) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
        this.achievementIndex = achievementIndex;
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
