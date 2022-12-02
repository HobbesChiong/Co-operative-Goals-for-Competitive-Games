package ca.cmpt276.iteration1.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Assert;
import org.junit.Test;

import ca.cmpt276.iteration1.model.GameType;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameTypeTest {
    /*

    @Test
    public void createGameObject() {
        GameType g = new GameType("My type", 100, 0);
        GameType g2 = new GameType("My type", -1, -100);

        assertThrows(IllegalArgumentException.class, () -> {
            GameType g3 = new GameType("My type", 0, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GameType g4 = new GameType("My type", 0, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GameType g5 = new GameType("My type", -1, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GameType g6 = new GameType("My type", -100, -1);
        });
    }

    @Test
    public void getAchievementName(){
        Assert.assertEquals("Cowardly Cows", GameType.getAchievementName(0,0));
        Assert.assertEquals("Devious Dragons", GameType.getAchievementName(0,1));
        Assert.assertEquals("Placid Patricks", GameType.getAchievementName(0,2));
    }

    @Test
    public void getGameType(){
        GameType g = new GameType("My type", 100, 0);
        Assert.assertEquals("My type", g.getGameType());

        GameType g2 = new GameType("", 100, 0);
        Assert.assertEquals("", g2.getGameType());
    }

    @Test
    public void getGoodScore(){
        GameType g = new GameType("My type", 100, 0);
        Assert.assertEquals(100, g.getGoodScore());
    }

    @Test
    public void getBadScore(){
        GameType g = new GameType("My type", 100, 0);
        Assert.assertEquals(0, g.getBadScore());
    }

    @Test
    public void editGameType(){
        GameType g = new GameType("My type", 100, 0);
        g.editGameType("UNO", 10,1);

        Assert.assertEquals("UNO", g.getGameType());
        Assert.assertEquals(10, g.getGoodScore());
        Assert.assertEquals(1, g.getBadScore());
    }

    @Test
    public void getAchievementIndex(){
        GameType g = new GameType("My type", 100, 0);
        // tests lower and upper bound
        Assert.assertEquals(0,g.getAchievementIndex(-1,1,"Easy"));
        Assert.assertEquals(7,g.getAchievementIndex(76,1,"Easy"));
        Assert.assertEquals(0,g.getAchievementIndex(-1,1,"Normal"));
        Assert.assertEquals(7,g.getAchievementIndex(101,1,"Normal"));
        Assert.assertEquals(0,g.getAchievementIndex(-1,1,"Hard"));
        Assert.assertEquals(7,g.getAchievementIndex(126,1,"Hard"));

        // tests for score equal to good and bad score
        Assert.assertEquals(6,g.getAchievementIndex(75,1,"Easy"));
        Assert.assertEquals(6,g.getAchievementIndex(100,1,"Normal"));
        Assert.assertEquals(6,g.getAchievementIndex(125,1,"Hard"));
        Assert.assertEquals(1,g.getAchievementIndex(0,1,"Easy"));
        Assert.assertEquals(1,g.getAchievementIndex(0,1,"Normal"));
        Assert.assertEquals(1,g.getAchievementIndex(0,1,"Hard"));

        // tests for multiple player scaling
        Assert.assertEquals(6,g.getAchievementIndex(150,2,"Easy"));
        Assert.assertEquals(6,g.getAchievementIndex(200,2,"Normal"));
        Assert.assertEquals(6,g.getAchievementIndex(250,2,"Hard"));

    }

    */
}