package guessGameAPI.controllers;

import guessGameAPI.data.GameDatabaseDAO;
import guessGameAPI.data.RoundDatabaseDAO;
import guessGameAPI.models.Game;
import guessGameAPI.models.Round;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import java.util.List;

//controller that interacts with HTTP requests, using Spring to streamline input
//access repository containing GameDatabaseDAO and RoundDatabaseDAO
@RestController
@RequestMapping("/api/guessGame")
public class GuessGameController {

    private final RoundDatabaseDAO roundDao;
    private final GameDatabaseDAO gameDao;
    Random rand = new Random();

    //constructor that accesses repository for DAO inputs
    public GuessGameController(RoundDatabaseDAO roundDao, GameDatabaseDAO gameDao) {
        this.roundDao = roundDao;
        this.gameDao = gameDao;
    }


    //function that maps to gameDBDao and creates a game object
    @PostMapping("/begin")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> start() {
        ResponseEntity response = new ResponseEntity(HttpStatus.NOT_FOUND);

        //create game object
        //database auto increments gameID, so it is not specified here
        Game game = new Game();


        //generate game answer
        int digit1 = rand.nextInt(9) +1;    //first digit must at least be 1
        int digit2 = rand.nextInt(10);      //second digit, etc
        int digit3 = rand.nextInt(10);
        int digit4 = rand.nextInt(10);

        //test if digits are the same, and generate new digits as needed
        while(digit1==digit2 || digit1==digit3 || digit1==digit4 || digit2==digit3 || digit2==digit4 || digit3==digit4){
            digit1 = rand.nextInt(9) +1;
            digit2 = rand.nextInt(10);
            digit3 = rand.nextInt(10);
            digit4 = rand.nextInt(10);
        }

        //put digits together into one number, and save to game as the answer
        int number = (digit1 *1000) + (digit2 *100) + (digit3 *10) + digit4;
        game.setAnswer(number);

        //set game status, and create in database
        game.setStatus("In Progress");
        Game result = gameDao.begin(game);

        //return responseEntity object that will verify game creation in database
        if (result == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }


    //function that maps to roundDBDao and creates a round object
    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.CREATED)
    public Round guess(@RequestBody Round round) {

        //JSON input gives game ID, and guess
        //get current time and save in round object
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        round.setTimeOfGuess(dtf.format(now));

        //based on gameID, get the answer and separate into 4 digits, and get round guess
        int answer = gameDao.findByIdPrivate(round.getGameID()).getAnswer();
        int guess = round.getGuess();

        //count for exact matches and partial matches in digits
        int exactMatch = 0;
        int partialMatch = 0;

        //compare if equal, if success, then set game status to finished, and exit
        if(answer == guess){
            exactMatch = 4;
            round.setExactMatch(exactMatch);

            gameDao.finishGame(round.getGameID());
            return roundDao.guess(round);
        }

        //calculate all digits for guess and answer
        int answer1 = answer/1000;
        answer %= 1000;
        int answer2 = answer/100;
        answer %= 100;
        int answer3 = answer/10;
        int answer4 = answer%10;

        int guess1 = guess/1000;
        guess %= 1000;
        int guess2 = guess/100;
        guess %= 100;
        int guess3 = guess/10;
        int guess4 = guess%10;


        //calculate number of exact matches by comparing corresponding digits
        if(answer1 == guess1)
            exactMatch++;
        if(answer2 == guess2)
            exactMatch++;
        if(answer3 == guess3)
            exactMatch++;
        if(answer4 == guess4)
            exactMatch++;

        //save number of exact matches
        round.setExactMatch(exactMatch);

        //test for partial matches
        if(guess1 == answer2 || guess1 == answer3 || guess1 == answer4)
            partialMatch++;
        if(guess2 == answer1 || guess2 == answer3 || guess2 == answer4)
            partialMatch++;
        if(guess3 == answer1 || guess3 == answer2 || guess3 == answer4)
            partialMatch++;
        if(guess4 == answer1 || guess4 == answer2 || guess4 == answer3)
            partialMatch++;


        //save number of partial matches
        round.setPartialMatch(partialMatch);

        //give round object
        return roundDao.guess(round);
    }

    //function that uses DBDAO to get all game results
    @GetMapping
    public List<Game> all() {
        return gameDao.getAll();
    }


    //function that maps to DBDAO for game, to find by ID
    @GetMapping("/game/{id}")
    public ResponseEntity<Game> findByGame(@PathVariable int id) {
        Game result = gameDao.findById(id);
        if (result == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }


    //function that maps to DBDAO for round, to find by gameID
    @GetMapping("/round/{id}")
    public ResponseEntity<List<Round>> findByRound(@PathVariable int id) {

        //get list of the rounds by the DBDAO method
        List<Round> result = roundDao.findByGameId(id);

        //use Response entity to verify result, return list
        if (result == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }



}
