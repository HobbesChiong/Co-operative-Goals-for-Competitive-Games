Scoring system:
	A good score is an expected good score (not the best!) for a game
	A bad score is an expected bad score (not the worst!) for a game

Creating games and game types:
	When creating a new game, the expected scores for good and bad score are per player.
	When creating a new game play, the expected score is CUMULATIVE for all players, your achievement level is based on the cumulative score, scaled by
	the number of players who played the game.



icon picture reference: retrieved 3 Nov, 2022 from https://www.pngegg.com/en/png-zkzwq
background image reference: retrieved 20 Nov, 2022 from https://www.pexels.com/photo/close-up-photo-of-monopoly-board-game-776654/

----------------------------------------------------------------------------------------------------------------------------------------------------
User Story Estimations:

1 unit = Implement PlayedGame Class 

1.) Setup Game Played Difficulty (5 Units), Setup difficulty settings for each gameplay and reflect the difficulty settings in achievement list and gameplay list. We estimated 5 units of work due to figuring out how to apply the difficulty and reflect it in the UI.

2.) Animation and Effects when achievement is earned (6 units), Play a image with an animation and sound after a game is played. 

3.) Themes for Achievements (6 units), Create themes for achievements and a screen for users to choose which one they want.

4.) Edit Game Played (5 units), Add the ability for users to change the scores or difficulty of a previous game played.

5.) Setup Junit tests (4 units), Add test cases to the GameType class to check for proper achievment output.

6.) Score calculator (5 units), Let the app take in each player's score to find total score and store each player's score per game.
