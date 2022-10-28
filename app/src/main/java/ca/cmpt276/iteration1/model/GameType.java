package ca.cmpt276.iteration1.model;

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

    };

    public GameType(String type, int goodScore, int badScore) {
        this.type = type;
        this.goodScore = goodScore;
        this.badScore = badScore;
    }

    public String getType() {
        return type;
    }

    public int getGoodScore() {
        return goodScore;
    }

    public int getBadScore() {
        return badScore;
    }

    /**
     * Maps a value from one range to another
     *
     * Ex: x = 10, [0,100] -> [0,10] = 1
     */
    public int map(int val, int oldMinimum, int oldMaximum, int newMinimum, int newMaximum) {
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
}
