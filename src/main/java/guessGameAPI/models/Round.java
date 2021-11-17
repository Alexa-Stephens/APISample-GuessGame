package guessGameAPI.models;

import java.time.LocalDateTime;

public class Round {



    private int gameID;
    private int guess;
    private String timeOfGuess;
    private int exactMatch;
    private int partialMatch;

    //setters
    public void setGuess(int guess) {
        this.guess = guess;
    }
    public void setExactMatch(int exactMatch) {
        this.exactMatch = exactMatch;
    }
    public void setPartialMatch(int partialMatch) {
        this.partialMatch = partialMatch;
    }
    public void setGameId(int gameID) { this.gameID = gameID;}
    public void setTimeOfGuess(String timeOfGuess){ this.timeOfGuess = timeOfGuess;}

    //getters
    public int getGameID() { return gameID; }
    public int getGuess() { return guess; }
    public String getTimeOfGuess() { return timeOfGuess; }
    public int getExactMatch() { return exactMatch; }
    public int getPartialMatch() { return partialMatch; }

    //getresult method returns the string form of the
}
