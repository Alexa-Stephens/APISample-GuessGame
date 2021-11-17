package guessGameAPI.data;

import guessGameAPI.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

//class is in repository to be accessed by controller
@Repository
public class GameDatabaseDAO implements GameDAO {

    private final JdbcTemplate jdbcTemplate;


    //constructor that initializes the jdbctemplate object
    @Autowired
    public GameDatabaseDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    //begin game will create record in GameDB based on values in game object input
    @Override
    public Game begin(Game game) {
        //define sql query
        final String sql = "INSERT INTO game(status, answer) VALUES(?,?);";

        //use keyholder to map status and answer to the query
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        //update record in DB
        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, game.getStatus());
            statement.setInt(2, game.getAnswer());
            return statement;
        }, keyHolder);

        //set gameObject gameID to ensure ID matches DB entry
        game.setGameId(keyHolder.getKey().intValue());

        return game;
    }

    //this function will list all the games in the game table
    @Override
    public List<Game> getAll() {
        final String sql = "SELECT gameId, status, CASE WHEN `status` = 'Finished' THEN answer END AS answer" +
                " FROM game;";
        return jdbcTemplate.query(sql, new GameMapper());
    }

    //this function will query a game based on a given ID
    @Override
    public Game findById(int id) {
        final String sql = "SELECT gameId, status, CASE WHEN `status` = 'Finished' THEN answer END AS answer " +
                "FROM game WHERE gameID = ?;";
        return jdbcTemplate.queryForObject(sql, new GameDatabaseDAO.GameMapper(), id);
    }

    //this function will query a game based on a given ID
    //results aare for functional use, so answer is not hidden
    @Override
    public Game findByIdPrivate(int id) {
        final String sql = "SELECT gameId, status, answer " +
                "FROM game WHERE gameID = ?;";
        return jdbcTemplate.queryForObject(sql, new GameDatabaseDAO.GameMapper(), id);
    }

    //method that updates the game status to finished when a match has been found
    @Override
    public boolean finishGame(int id){
        final String sql = "UPDATE game SET status = 'Finished' WHERE gameID = ?";
        return jdbcTemplate.update(sql, id) >0;
    }


    //mapper class that parses resultset input into game object
    private static final class GameMapper implements RowMapper<Game> {

        @Override
        public Game mapRow(ResultSet rs, int index) throws SQLException {
            Game game = new Game();
            game.setGameId(rs.getInt("gameID"));
            game.setStatus(rs.getString("status"));
            game.setAnswer(rs.getInt("answer"));
            return game;
        }
    }



}
