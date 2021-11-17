package guessGameAPI.data;

import guessGameAPI.models.Round;

import java.util.List;

public interface RoundDAO {

    Round guess(Round round);
    List<Round> findByGameId(int id);
}
