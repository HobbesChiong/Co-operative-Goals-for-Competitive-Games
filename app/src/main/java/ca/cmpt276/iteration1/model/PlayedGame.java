package ca.cmpt276.iteration1.model;
/*
* The PlayedGame class is for storing played game's data.
* type for game name
* numberOfPlayers for #Players
* score for the total score of the whole team game
* achievement for the achievement that the team got according to their score.
* */

public class PlayedGame {
    private final String type;
    private final int numberOfPlayers;
    private final int score;
    private final String achievement;

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
}
