package guessGameAPI.data;

import guessGameAPI.models.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

//class is in repository to be accessed by the controller
@Repository
public class RoundDatabaseDAO implements RoundDAO{

    private final JdbcTemplate jdbcTemplate;

    //constructor that initializes the jdbctemplate object
    @Autowired
    public RoundDatabaseDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Round guess(Round round){
        //define sql query
        final String sql = "INSERT INTO round(gameId, guess, timeOfGuess, exactMatch, partialMatch) VALUES(?,?,?,?,?);";

        //use keyholder to map status and answer to the query
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        //update record in DB
        jdbcTemplate.update((Connection conn) -> {
            //use prepared statement to match attributes to db query
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, round.getGameID());
            statement.setInt(2, round.getGuess());
            statement.setString(3, round.getTimeOfGuess());
            statement.setInt(4, round.getExactMatch());
            statement.setInt(5, round.getPartialMatch());
            return statement;
        }, keyHolder);

        return round;
    }

    //this will return a list of rounds in a given game, based on game ID
    @Override
    public List<Round> findByGameId(int id) {
        final String sql = "SELECT gameId, guess, timeOfGuess, exactMatch, partialMatch " +
                "FROM round WHERE gameID = ?;";
        return jdbcTemplate.query(sql, new RoundMapper(), id);
    }


    private static final class RoundMapper implements RowMapper<Round>{
        @Override
        public Round mapRow(ResultSet rs, int index) throws SQLException {
            Round round = new Round();
            round.setGameId(rs.getInt("gameID"));
            round.setGuess(rs.getInt("guess"));
            round.setTimeOfGuess(rs.getString("timeOfGuess"));
            round.setExactMatch(rs.getInt("exactMatch"));
            round.setPartialMatch(rs.getInt("partialMatch"));
            return round;
        }
    }
}
