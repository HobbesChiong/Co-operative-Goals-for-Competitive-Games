package ca.cmpt276.iteration1.model;

import java.util.ArrayList;

public class GameManager {
    private ArrayList<GameType> gameTypes;
    private ArrayList<PlayedGame> playedGames;

    // Singleton support
    private static GameManager instance;
    private GameManager(){
        // private to prevent anything else from instantiating
    }
    public static GameManager getInstance(){
        if (instance == null){
            instance = new GameManager();
        }
        return instance;
    }

    public void deleteGameType(String type){
        // Use its own object to find itself and remove itself from the arraylist
        gameTypes.removeIf(gameType -> gameType.getType().equals(type));
    }

    public void addGameType(String type, int goodScore, int badScore){
        GameType gameType = new GameType(type, goodScore, badScore);
        gameTypes.add(gameType);
    }

    public ArrayList<PlayedGame> getSpecificPlayedGames(String type){
        // Will return an arraylist of all the played games for a certain game type
        ArrayList<PlayedGame> specificPlayedGames = new ArrayList<>();
        for (PlayedGame playedGame : playedGames){
            if (playedGame.getType().equals(type)){
                specificPlayedGames.add(playedGame);
            }
        }
        return specificPlayedGames;
    }

    public ArrayList<GameType> getGameTypes() {
        return gameTypes;
    }

    public ArrayList<PlayedGame> getPlayedGames() {
        return playedGames;
    }
}
