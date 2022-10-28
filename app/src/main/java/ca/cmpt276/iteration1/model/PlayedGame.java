package ca.cmpt276.iteration1.model;

public class PlayedGame {
    private String type;
    private int numberOfPlayers;
    private int score;

    public PlayedGame(String type, int numberOfPlayers, int score) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
