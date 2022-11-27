package ca.cmpt276.iteration1.model;

/**
* The PlayedGame class is for storing played game's data.
* type for game name
* numberOfPlayers for #Players
* score for the total score of the whole team game
* achievement for the achievement that the team got according to their score.
* ArrayList to store each player's score and information
* */

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayedGame {
    private final String type;

    private int numberOfPlayers;
    private int totalScore;

    private int achievementIndex;
    private String difficulty;
    private ArrayList<Integer> playerScores;

    private final LocalDateTime datePlayed;

    public PlayedGame(String type,
                      int numberOfPlayers,
                      int totalScore,
                      int achievementIndex,
                      String difficulty,
                      ArrayList<Integer> playerScores,
                      LocalDateTime datePlayed) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.totalScore = totalScore;
        this.achievementIndex = achievementIndex;
        this.difficulty = difficulty;
        this.playerScores = playerScores;
        this.datePlayed = datePlayed;
    }

    public String getType() {
        return type;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public ArrayList<Integer> getPlayerScores() { return playerScores; }

    public LocalDateTime getDatePlayed() { return datePlayed; }

    public String getAchievement() {
        GameManager gameManager = GameManager.getInstance();
        return GameType.getAchievementName(achievementIndex, gameManager.getAchievementTheme());
    }

    public void editPlayedGame(int numberOfPlayers, int score, int achievementIndex, String difficulty, ArrayList<Integer> playerScores){
        this.numberOfPlayers = numberOfPlayers;
        this.totalScore = score;
        this.achievementIndex = achievementIndex;
        this.difficulty = difficulty;
        this.playerScores = playerScores;
    }

    @NonNull
    @Override
    public String toString() {
        String output;
        output = "Score: " + totalScore + ", " + numberOfPlayers + "Players, " + getAchievement();
        return output;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
