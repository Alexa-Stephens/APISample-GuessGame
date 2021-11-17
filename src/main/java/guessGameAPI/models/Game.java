package guessGameAPI.models;

public class Game {

    private int gameId;
    private String status;
    private int answer;


    //getters
    public int getGameId() { return gameId; }
    public String getStatus() { return status; }
    public int getAnswer() { return answer; }

    //setters
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setStatus(String status) { this.status = status; }
    public void setAnswer(int answer) { this.answer = answer; }




}
