package ca.cmpt276.iteration1.model;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class PlayedGameTest {

    @Test
    public void testGetType() {
       PlayedGame testGame = new PlayedGame("UNO",2,100,0,"Easy");
       Assert.assertEquals("UNO",testGame.getType());
    }

    @Test
    public void testGetNumberOfPlayers() {
        PlayedGame testGame = new PlayedGame("UNO",2,100,0,"Easy");
        Assert.assertEquals(2, testGame.getNumberOfPlayers());

        PlayedGame testGame2 = new PlayedGame("UNO",0,100,0,"Easy");
        Assert.assertEquals(0, testGame2.getNumberOfPlayers());
    }

    @Test
    public void testGetScore() {
        PlayedGame testGame = new PlayedGame("UNO",2,100,0,"Easy");
        Assert.assertEquals(100, testGame.getScore());

        PlayedGame testGame2 = new PlayedGame("UNO",2,0,0,"Easy");
        Assert.assertEquals(0, testGame2.getScore());

        PlayedGame testGame3 = new PlayedGame("UNO",2,-100,0,"Easy");
        Assert.assertEquals(-100, testGame3.getScore());

    }


    @Test
    public void testEditPlayedGame() {
        PlayedGame testGame = new PlayedGame("UNO",0,100,0,"Easy");
        testGame.editPlayedGame(2,1000, 3);

        Assert.assertEquals(2,testGame.getNumberOfPlayers());
        Assert.assertEquals(1000, testGame.getScore());
    }


    @Test
    public void testGetDifficulty() {
        PlayedGame testGame = new PlayedGame("UNO",0,100,0,"Easy");

        Assert.assertEquals("Easy", testGame.getDifficulty());
    }
}