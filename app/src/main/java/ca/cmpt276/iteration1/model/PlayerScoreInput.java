package ca.cmpt276.iteration1.model;

public class PlayerScoreInput {

    private int playerNumber;
    private int playerScore;

    public PlayerScoreInput(int playerNumber, int playerScore) {
        this.playerNumber = playerNumber;
        this.playerScore = playerScore;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }
    public int getPlayerScore(){
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber){
        this.playerNumber = playerNumber;
    }
    public void setPlayerScore(int playerScore){
        this.playerScore = playerScore;
    }

}
