package ca.cmpt276.iteration1.model;

public class PlayedGame {
    private final String type;
    private final int numberOfPlayers;
    private final int score;

    public PlayedGame(String type, int numberOfPlayers, int score) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
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
}
