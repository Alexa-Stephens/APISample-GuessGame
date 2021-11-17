package guessGameAPI.data;

import guessGameAPI.models.Game;

import java.util.List;

public interface GameDAO {

    public Game begin(Game game);

    List<Game> getAll();

    Game findById(int id);

    //this function will query a game based on a given ID
    Game findByIdPrivate(int id);

    boolean finishGame(int id);
}
