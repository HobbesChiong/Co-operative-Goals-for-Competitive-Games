package ca.cmpt276.iteration1.model;

import java.util.ArrayList;

/**
 * Represents a type of game. Stores the name (type) of the game, what a good score (per player)
 * would look like and what a bad score (per player) would look like
 */
public class GameType {
    private int goodScore;
    private int badScore;
    private String type;

    // Names for different achievement levels, sorted from worst to best
    private static final String[] achievementLevels = {
            "Cowardly Cows", // Below a bad score
            "Lowly Lamas",
            "Dead Dodos",
            "Average Alligators",
            "Fragrant Fish",
            "Excellent Eggs",
            "Beautiful Bears",
            "Godly Goats" // Above a good score
    };

    public GameType(String type, int goodScore, int badScore) {
        this.type = type;
        this.goodScore = goodScore;
        this.badScore = badScore;

        if (goodScore < badScore) {
            throw new IllegalArgumentException("Bad score should be less than the good score.");
        }
    }

    public String getGameType() {
        return type;
    }

    public int getGoodScore() {
        return goodScore;
    }

    public int getBadScore() {
        return badScore;
    }

    public void editGameType(String type, int goodScore, int badScore){
        this.type = type;
        this.goodScore = goodScore;
        this.badScore = badScore;
    }

    /**
     * Maps a value from one range to another
     *
     * Ex: x = 10, [0,100] -> [0,10] = 1
     */
    private int map(int val, int oldMinimum, int oldMaximum, int newMinimum, int newMaximum) {
        // Math from https://math.stackexchange.com/questions/914823/shift-numbers-into-a-different-range
        float valueScale = (newMaximum - newMinimum)/(float)(oldMaximum - oldMinimum);
        float endpointShift = val - oldMinimum;
        int newValue = Math.round(newMinimum + (valueScale * endpointShift));

        return newValue;
    }

    public String getAchievementLevel(int score, int playerNumber) {
        // Number of achievements
        int achievementCount = achievementLevels.length;

        score /= playerNumber;

        // If worse than a bad score, return the worst achievement levels
        if (score < badScore) {
            return achievementLevels[0];
        }

        if (score > goodScore) {
            return achievementLevels[achievementCount - 1];
        }

        // Scale the score to range from 1 to the number of achievements - 1
        int achievementIndex = map(score,goodScore, badScore, 1, achievementCount - 2);
        return achievementLevels[achievementIndex];
    }

    public ArrayList<String> getAchievementLevelScoreRequirements(int playerNumber){
        ArrayList<String> res = new ArrayList<>();
        int middleScore = Math.round(((float)((goodScore-badScore)/5))*playerNumber);

        res.add(achievementLevels[0] + " <" + badScore*playerNumber);
        res.add(achievementLevels[1] + " " + badScore*playerNumber);
        for(int i = 2; i<=5; i++){
            res.add(achievementLevels[i] + " " + (middleScore*(i-1))*playerNumber);
        }
        res.add(achievementLevels[6] + " " + goodScore*playerNumber);
        res.add(achievementLevels[7] + " >" + goodScore*playerNumber);
        return res;
    }
}
