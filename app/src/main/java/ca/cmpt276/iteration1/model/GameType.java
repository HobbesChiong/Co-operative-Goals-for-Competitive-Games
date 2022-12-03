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
    private String imagePath;

    // Names for different achievement levels, sorted from worst to best
    private static final String[][] achievementLevels = {
            { // Animals
                "Cowardly Cows", // Below a bad score
                "Lowly Lamas",
                "Dead Dodos",
                "Average Alligators",
                "Fragrant Fish",
                "Excellent Eggs",
                "Beautiful Bears",
                "Godly Goats" // Above a good score
            },
            {
                "Devious Dragons",
                "Beautiful Basilisks",
                "Crafty Chimeras",
                "Subversive Sirens",
                "Keen Krakens",
                "Venomous Vampires",
                "Menacing Minotaurs",
                "Wonderful Werewolves"
            },
            { // Spongebob
                "Placid Patricks",
                "Sluggish Squidwards",
                "Standard Sandies",
                "Lethargic Larrys",
                "Pitiful Plankton",
                "Menacing Mr. Krabs",
                "Marvelous Mermaid Man",
                "Super Spongebob"
            }
    };

    public GameType(String type, int goodScore, int badScore, String imagePath) {
        this.type = type;
        this.goodScore = goodScore;
        this.badScore = badScore;
        this.imagePath = imagePath;

        if (goodScore < badScore) {
            throw new IllegalArgumentException("Bad score should be less than the good score.");
        }

        if (goodScore == badScore) {
            throw new IllegalArgumentException("Bad and good scores should not be equal!");
        }
    }

    /**
     * Gets the name of a specific achievement.
     *
     * Static so it can be called anywhere.
     * @param achievementIndex Index of the achievement earned (0 - max # of achievements)
     * @param achievementTheme Theme of the achievement to get
     * @return Name of an achievement
     */
    public static String getAchievementName(int achievementIndex, int achievementTheme) {
        return achievementLevels[achievementTheme][achievementIndex];
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

    public String getImagePath() { return imagePath; }

    public void editGameType(String type, int goodScore, int badScore, String imagePath) {
        this.type = type;
        this.goodScore = goodScore;
        this.badScore = badScore;
        this.imagePath = imagePath;

        if (goodScore < badScore){
            throw new IllegalArgumentException("Bad score should be less than the good score.");
        }
    }

    /**
     * Maps a value from one range to another
     *
     * Ex: x = 10, [0,100] -> [0,10] = 1
     */
    private int map(int val, float oldMinimum, float oldMaximum, int newMinimum, int newMaximum) {
        // Math from https://math.stackexchange.com/questions/914823/shift-numbers-into-a-different-range
        float valueScale = (newMaximum - newMinimum)/(oldMaximum - oldMinimum);
        float endpointShift = val - oldMinimum;
        int newValue = (int) Math.floor(newMinimum + (valueScale * endpointShift));

        return newValue;
    }

    /**
     * Gets the index of the achievement that was earned
     * @param score Score of the game
     * @param playerNumber Number of players in a game
     * @return 0 - max number of achievements, whichever one was earned by the player
     */
    public int getAchievementIndex(int score, int playerNumber, String difficulty) {
        int achievementTheme = GameManager.getInstance().getAchievementTheme();
        float scaling = getDifficultyMultiplier(difficulty);

        // Number of achievements
        int achievementCount = achievementLevels[achievementTheme].length;

        score /= playerNumber;

        // If worse than a bad score, return the worst achievement levels
        if (score < badScore*scaling) {
            return 0;
        }

        if (score > goodScore*scaling) {
            return achievementCount - 1;
        }

        // Scale the score to range from 1 to the number of achievements - 1
        int achievementIndex = map(score,badScore*scaling, goodScore*scaling, 1, achievementCount - 2);
        return achievementIndex;
    }


    public String getAchievementLevel(int score, int playerNumber, String difficulty) {
        int achievementTheme = GameManager.getInstance().getAchievementTheme();

        // Index of the achievement in a list of achievements
        int achievementIndex = getAchievementIndex(score, playerNumber, difficulty);

        return getAchievementName(achievementIndex,achievementTheme);
    }

    public String getSpecificAchievement(int theme, int level){
        return achievementLevels[theme][level];
    }

    public ArrayList<String> getAchievementLevelScoreRequirements(int playerNumber, String difficulty){
        int achievementTheme = GameManager.getInstance().getAchievementTheme();

        float scaling = getDifficultyMultiplier(difficulty);

        ArrayList<String> res = new ArrayList<>();
        // gets the good score - bad score and divides by 5 to get the intervals between achievements
        float difference = (float)(goodScore-badScore)/5;

        int max = achievementLevels[achievementTheme].length;

        res.add(achievementLevels[achievementTheme][0] + " <" + (int) ((badScore*playerNumber) * scaling));
        res.add(achievementLevels[achievementTheme][1] + " " + (int) ((badScore*playerNumber) * scaling));
        for(int i = 2; i<=max-3; i++){
            // below algo uses the formula from https://math.stackexchange.com/questions/914823/shift-numbers-into-a-different-range
            // Isolated t which is the score requirement for i which is the index of the achievement levels
            // if the game score divided by player number is >= to t then that is the achievement the game gets.
            int minScoreRequirement = (int) (Math.ceil((((i-1)*difference) + badScore)) * scaling);
            minScoreRequirement = (minScoreRequirement*playerNumber);
            res.add(achievementLevels[achievementTheme][i] + " " + (minScoreRequirement));
        }
        res.add(achievementLevels[achievementTheme][max-2] + " " + (int) ((goodScore*playerNumber) * scaling));
        res.add(achievementLevels[achievementTheme][max-1] + " >" + (int) ((goodScore*playerNumber) * scaling));
        return res;
    }

    private float getDifficultyMultiplier(String difficulty){
        float scaling = 0;
        switch (difficulty) {
            case "Easy":
                scaling = 0.75F;
                break;
            case "Normal":
                scaling = 1;
                break;
            case "Hard":
                scaling = 1.25F;
                break;
        }
        return scaling;
    }
}
