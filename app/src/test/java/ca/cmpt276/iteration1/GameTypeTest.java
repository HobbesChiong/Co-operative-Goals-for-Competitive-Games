package ca.cmpt276.iteration1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import ca.cmpt276.iteration1.model.GameType;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameTypeTest {
    @Test
    public void createGameObject() {
        GameType g = new GameType("My type", 100, 0);
    }

    @Test
    public void checkScoreRanges() {
        GameType g = new GameType("My type", 1000, 0);

        String achievement;
        String worstAchievement = g.getAchievementLevel(-12,1);
        String bestAchievement = g.getAchievementLevel(101,1);
        String averageAchievement = g.getAchievementLevel(50,1);

        assertNotEquals(bestAchievement,worstAchievement);
        assertNotEquals(averageAchievement,worstAchievement);
        assertNotEquals(averageAchievement,bestAchievement);
    }

    @Test
    public void checkScoreScaling() {
        GameType g = new GameType("My type", 100, 0);

        String achievementLevelOnePlayer = g.getAchievementLevel(10, 1);
        String achievementLevelEightPlayers = g.getAchievementLevel(80,8);
        assertEquals(achievementLevelEightPlayers,achievementLevelOnePlayer);
    }

    @Test
    public void checkIncorrectScoreBounds() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameType g = new GameType("My type", 0,100);
        });

    }

}