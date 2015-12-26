package Indy;

import cs015.fnl.SketchySupport.FileIO;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * This is the SinglePlayer class. It updates the current game based on
 * TimeAttack rules. It handles highscoring, changes time left based on events,
 * determines when the game is over.
 */
public class SinglePlayer extends Game {
    private Pane _bottom;
    private Ball[] _balls;
    private double _time;
    private Label _timer;
    private Label _ballsPocketedLabel;
    private Label _highscore;
    private int _ballsPocketed;
    private int _ballChecker;
    private boolean _alreadyGameOver;
    
    public SinglePlayer(BorderPane root, Stage stage) {
	super(root,stage);
	_balls = this.getBalls();
	_time = 90;
	_ballsPocketed = 0;
	_ballChecker = 0;
	_alreadyGameOver = false;
	_bottom = new Pane();
	_bottom.setStyle("-fx-background-image: url('Indy/bottom.jpg')");
	this.createText();
	root.setCenter(_bottom);
    }
    //Updates the game constantly, checking for collisions, updating time left, etc.
    public void update() {
	_time -= .005;
	this.checkGameOver();
	this.updateLabels();
	this.checkCollision();
	for (int i=0;i<16;i++) {
	    _balls[i].updatePosition();
	    _balls[i].updateRotation();
	}
	this.checkCue();
	if (_alreadyGameOver) {
	    this.setCueColor(Color.TRANSPARENT);
	}
	this.checkRack();
    }
    //If all the balls are pocketed, create a new rack of balls and set count
    //back to 0
    private void checkRack() {
	if (_ballChecker>14) {
	    _balls[15].removeGraphically();
	    @SuppressWarnings("unused")
	    BallMachine machine = new BallMachine(_balls,this.getCenter());
	    _ballChecker=0;
	}
    }
    //Saves the new highscore if it is greater than the previous one
    private void setHighscore() {
	_highscore = new Label();
	FileIO io = new FileIO();
	io.openRead("Indy/save/highscore.txt");
	int highscore = io.readInt();
	io.closeRead();
	if (highscore < _ballsPocketed) {
	    io.openWrite("Indy/save/highscore.txt");
	    io.writeInt(_ballsPocketed);
	    io.closeWrite();
	    highscore = _ballsPocketed;
	}
	_highscore.setText("Highscore: " + highscore);
    }
    //Checks to see if time is <0 and if so it initiates gameOver
    public void checkGameOver() {
	if (_time <= 0) {
	    this.gameOver();
	    _bottom.getChildren().removeAll(_timer,_ballsPocketedLabel);
	}
    }
    //Changes the labels based on the _time value and balls pocketed
    public void updateLabels() {
	int minutes = (int) _time/60;
	int seconds = (int) _time - 60 * minutes;
	if (seconds <10) {
	    _timer.setText("Time Left: " + minutes + ":0" + seconds);
	} else {
	    _timer.setText("Time Left: " + minutes + ":" + seconds);
	}
	_ballsPocketedLabel.setText("Balls Pocketed: " +_ballsPocketed);
    }
    //Removes/adds time based on the type of ball pocketed
    public void pocketBall(int i) {	
	if (i == 15) { //Checks for cue ball
	    this.setScratch();
	    _time -= 25;
	    _balls[i].setPositionX(-50);
	    _balls[i].setPositionY(-50);
	    _balls[i].setVelocityX(0);
	    _balls[i].setVelocityY(0);
	} else { //Checks for striped and solid balls
	     _time += 15;
	     _ballsPocketed++;
	     _ballChecker++;
	     _balls[i].removeGraphically();
	     _balls[i].setPositionY(-50);
	     _balls[i].setVelocityX(0);
	     _balls[i].setVelocityY(0);
	     _balls[i].setPocketed();
	    }
    }
    //Generating the bottom text labels
    public void createText() {
	Font font = Font.font("Alegreya",30);
	Font font2 = Font.font("Alegreya",15);
	Font font3 = Font.font("Alegreya",FontWeight.EXTRA_BOLD,15);
	_timer = new Label("Seconds Left: " + _time);
	_ballsPocketedLabel = new Label("Balls Pocketed: " + _ballsPocketed);
	Label singlePlayer = new Label("Time Attack");
	singlePlayer.setLayoutX(10);
	singlePlayer.setLayoutY(10);
	singlePlayer.setFont(font3);
	singlePlayer.setTextFill(Color.BLACK);
	Button button1 = new Button("Menu");
	    button1.setLayoutX(470);
	    button1.setLayoutY(20);
	    button1.setFont(font2);
	    button1.setOnAction(new MenuHandler());
	_timer.setTextFill(Color.WHITE);
	_ballsPocketedLabel.setTextFill(Color.WHITE);
	_timer.setLayoutX(250);
	_timer.setLayoutY(15);;
	_ballsPocketedLabel.setLayoutX(560);
	_ballsPocketedLabel.setLayoutY(15);
	_timer.setFont(font);
	_ballsPocketedLabel.setFont(font);
	_bottom.getChildren().addAll(_timer,_ballsPocketedLabel,button1,singlePlayer);
	
    }
    //When the game is over, saves highscore, removes balls, and displays
    //a gameover screen with highscore,balls pocketed, etc.
    public void gameOver() {
	if (_alreadyGameOver == false) {
	    this.setHighscore();
	    for (int i=0;i<16;i++) {
		_balls[i].removeGraphically();
	    }
	    _ballsPocketedLabel.setLayoutX(400);
	    _ballsPocketedLabel.setLayoutY(400);
	    Rectangle rectangle = new Rectangle(1500,600,Color.WHITE);
	    	rectangle.setOpacity(0);
	    Font font = Font.font("Vladimir Script",FontWeight.EXTRA_BOLD,150);
	    Label gameOver = new Label();
	    	gameOver.setLayoutX(230);
	    	gameOver.setLayoutY(120);
	    	gameOver.setFont(font);
	    	gameOver.setOpacity(0);
	    	gameOver.setTextFill(Color.BLACK);
	    	gameOver.setText("GameOver");
	    Font font2 = Font.font("Vladimir Script",FontWeight.EXTRA_BOLD,70);
	    	_ballsPocketedLabel.setOpacity(0);
	    	_ballsPocketedLabel.setTextFill(Color.BLACK);
	    FadeTransition ft = new FadeTransition(Duration.seconds(3));
		ft.setNode(gameOver);
		ft.setCycleCount(1);
		ft.setFromValue(0);
		ft.setToValue(1);
	    FadeTransition ft2 = new FadeTransition(Duration.seconds(3));;
		ft2.setNode(_ballsPocketedLabel);
		ft2.setCycleCount(1);
		ft2.setFromValue(0);
		ft2.setToValue(1);
	    FadeTransition ft3 = new FadeTransition(Duration.seconds(3));
		ft3.setNode(rectangle);
		ft3.setToValue(.3);
	    FadeTransition ft4= new FadeTransition(Duration.seconds(3));;
	    	_highscore.setOpacity(0);
	    	_highscore.setFont(font2);
	    	_highscore.setTextFill(Color.BLACK);
	    	_highscore.setLayoutX(350);
	    	_highscore.setLayoutY(300);
		ft4.setNode(_highscore);
		ft4.setCycleCount(1);
		ft4.setFromValue(0);
		ft4.setToValue(1);
	    this.getCenter().getChildren().addAll(rectangle,gameOver,_ballsPocketedLabel,_highscore);
	    ft2.play();
	    ft.play();
	    ft3.play();
	    ft4.play();
	    _alreadyGameOver =true;
	}
    }
    //Restarts the application when triggered
    private class MenuHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		App app = new App();
	    	app.start(SinglePlayer.this.getStage());
	    }
    }  
}
 
