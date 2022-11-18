package ca.cmpt276.iteration1.model;

public class PlayerScoreInput {

    private int playerId;
    private String playerName;
    private int playerScore;

    public PlayerScoreInput(int playerNumber) {
        this.playerId = playerNumber;
        this.playerName = "Player " + playerId;
    }

    public PlayerScoreInput(int playerNumber, int playerScore){
        this.playerId = playerNumber;
        this.playerName = "Player " + playerId;
        this.playerScore = playerScore;
    }

    public int getPlayerId(){
        return playerId;
    }
    public String getPlayerName(){
        return playerName;
    }

    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    public void setPlayerName(int playerId){
        this.playerName = "Player " + playerId;
    }

}
