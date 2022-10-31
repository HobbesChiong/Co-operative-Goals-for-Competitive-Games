package ca.cmpt276.iteration1.model;

import java.util.ArrayList;


public class GameManager {
    private ArrayList<GameType> gameTypes = new ArrayList<>();
    private ArrayList<PlayedGame> playedGames = new ArrayList<>();

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

    public void addGameType(GameType gameType){
        gameTypes.add(gameType);
    }

    public GameType getGameTypeAtIndex(int index) {
        return gameTypes.get(index);
    }

    public GameType getGameType(String type){
        for (GameType gameType : gameTypes){
            if (gameType.getType().equals(type)){
                return gameType;
            }
        }
        // If gametype does not exist, return null value
        return null;
    }

    public void addPlayedGame(PlayedGame game){
        playedGames.add(game);
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

    public void loadGameTypeList(ArrayList<GameType> gameTypes){
        this.gameTypes = gameTypes;
    }
    public void loadGamePlayedList(ArrayList<PlayedGame> playedGames){
        this.playedGames = playedGames;
    }

    public ArrayList<GameType> getGameTypes() {
        return gameTypes;
    }

    public ArrayList<PlayedGame> getPlayedGames() {
        return playedGames;
    }
}
