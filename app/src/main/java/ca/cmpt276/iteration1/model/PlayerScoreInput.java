package ca.cmpt276.iteration1.model;

public class PlayerScoreInput {

    private int playerId;
    private String playerName;
    private int playerScore;

    public PlayerScoreInput(int playerNumber) {
        this.playerId = playerNumber;
        this.playerName = "Player " + (playerId + 1);

        // If playerScore is not set, we will set it to -1 by default
        // This prevents the recyclerviewadapter from trying to pull 0 and setting it as
        // the edittext's input field
        this.playerScore = -1;
    }

    public PlayerScoreInput(int playerNumber, int playerScore){
        this.playerId = playerNumber;
        this.playerName = "Player " + (playerId + 1);
        this.playerScore = playerScore;
    }

    public int getPlayerId(){
        return playerId;
    }
    public String getPlayerName(){
        return playerName;
    }
    public int getPlayerScore() { return playerScore; }

    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    public void setPlayerName(int playerId){
        this.playerName = "Player " + (playerId + 1);
    }
    public void setPlayerScore(int playerScore){
        this.playerScore = playerScore;
    }

}
