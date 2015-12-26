package Indy;

import java.io.File;
import cs015.fnl.SketchySupport.FileIO;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * This is the EightBallGame class. It updates the current game based on
 * eight ball pool rules. It handles save/load features, switches pool cue
 * colors, fades in labels depending on whose turn it is, determines when
 * the game is over, and displays the winner based on the current game conditions.
 */
public class EightBallGame extends Game {
    private Pane _bottom;
    private Ball[] _balls;
    private Button _button2;
    private Button _button3;
    private int _numberStripesPocketed;
    private int _numberSolidsPocketed;
    private boolean _currentBallIsSolid;
    private boolean _correctBallPocketed;
    private boolean _alreadyCheckedTurn;
    private boolean _alreadyGameOver;
    private boolean _eightBallPocketed;
    private boolean _firstBallPocketed;
    
    public EightBallGame(BorderPane root, Stage stage) {
	super(root,stage);
	_balls = this.getBalls();
	_numberStripesPocketed = 0;
	_numberSolidsPocketed = 0;
	_alreadyGameOver = false;
	_alreadyCheckedTurn = false;
	_firstBallPocketed = false;
	_eightBallPocketed = false;
	_correctBallPocketed = false;
	_currentBallIsSolid = false; //false = stripes
	_bottom = new Pane();
	_bottom.setStyle("-fx-background-image: url('Indy/bottom.jpg')");
	this.createText();
	this.createBallHolders();
	root.setCenter(_bottom);
    }
    //Updates the game constantly, checking for collisions, cue colors, gameover
    //turn switching, etc.
    public void update() {
	this.checkCollision();
	for (int i=0;i<16;i++) {
	    _balls[i].updatePosition();
	    _balls[i].updateRotation();
	}
	//Check if balls are stopped
	boolean isStopped = true;
	for (int i=0;i<16;i++) {
	    if (_balls[i].getVelocityX() + _balls[i].getVelocityY() != 0.0) {
		isStopped = false;
		_alreadyCheckedTurn = false; //Allows turn switching 
	    }
	}
	//If they are stopped:
	if (isStopped == true) {
	    this.checkGameOver();
	    this.checkTurnSwitch();
	}
	this.updateCueColor();
	this.checkCue();
    }
    //Pockets solids, strips, 8ball, and cue with separate conditions
    public void pocketBall(int i) {	
	if (i == 15) { //Checks for Cue Ball and sets scratch 
	    this.setScratch();
	    _balls[i].setPositionX(-50);
	    _balls[i].setPositionY(-50);
	    _balls[i].setVelocityX(0);
	    _balls[i].setVelocityY(0);
	} else if (i == 7) { //Checks for Eight Ball and sets eightBallPocketed
	     _eightBallPocketed = true;
	     _balls[i].removeGraphically();
	     _balls[i].setPositionX(-50);
	     _balls[i].setPositionY(-50);
	     _balls[i].setVelocityX(0);
	     _balls[i].setVelocityY(0);
	} else { //Checks for striped and solid balls
	    if ((_currentBallIsSolid == _balls[i].isSolid())
	       && _numberSolidsPocketed + _numberStripesPocketed >0) {
		    //If solids and not the first pocketed ball
		    _correctBallPocketed = true;
	    } else if (_numberSolidsPocketed + _numberStripesPocketed == 0){
		//Sets current ball to the first ball pocketed's stripe/solid
		_currentBallIsSolid = _balls[i].isSolid();
		_correctBallPocketed = false;
	    }
	    //Adds the ball depending on the order it is pocketed and stripe/solid
	    //to the rack of pocketed balls
	    if (_balls[i].isSolid() ==false) {
		_numberStripesPocketed++; //Recording amount of stripes
		switch (_numberStripesPocketed) {
	    	    case 1: _balls[i].setPositionX(600);
	    		break;
	    	    case 2: _balls[i].setPositionX(650);
	    	    	break;
	    	    case 3: _balls[i].setPositionX(700);
	    	    	break;
	    	    case 4: _balls[i].setPositionX(750);
	    	    	break;
	    	    case 5: _balls[i].setPositionX(800);
	    	    	break;
	    	    case 6: _balls[i].setPositionX(850);
	    	    	break;
	    	    case 7: _balls[i].setPositionX(900);
	    	    	break;
		}
	     } else {
		_numberSolidsPocketed++; //Recording amount of solids
		switch (_numberSolidsPocketed) {
	    	    case 1: _balls[i].setPositionX(Constants.BALL_HOLDER);
	    		break;
	    	    case 2: _balls[i].setPositionX(Constants.BALL_HOLDER + 50);
	    	    	break;
	    	    case 3: _balls[i].setPositionX(Constants.BALL_HOLDER + 100);
	    	    	break;
	    	    case 4: _balls[i].setPositionX(Constants.BALL_HOLDER + 150);
	    	    	break;
	    	    case 5: _balls[i].setPositionX(Constants.BALL_HOLDER + 200);
	    	    	break;
	    	    case 6: _balls[i].setPositionX(Constants.BALL_HOLDER + 250);
	    	    	break;
	    	    case 7:  _balls[i].setPositionX(Constants.BALL_HOLDER + 300);
	    	    	break;
		}
	     }
	     //Removing the ball from the center pane and adding it to the bottom rack
	     _balls[i].removeGraphically();
	     _bottom.getChildren().add(_balls[i].getCircle());
	     _bottom.getChildren().add(_balls[i].getSphere());
	     _balls[i].setPositionY(70);
	     _balls[i].setVelocityX(0);
	     _balls[i].setVelocityY(0);
	     _balls[i].setPocketed();
	     _balls[i].getSphere().setRotate(0);
	    }
    }
    //Creating labels, buttons,  etc. on the bottom pane
    private void createText() {
	Font font = Font.font("Alegreya",FontWeight.EXTRA_BOLD,30);
	Font font2 = Font.font("Alegreya",FontWeight.EXTRA_BOLD,15);
	Label playerOne = new Label("Solids");
	Label playerTwo = new Label("Stripes");
	Label eightBall = new Label("Eight Ball");
	Button button1 = new Button("Menu");
	_button2 = new Button("Save");
	_button3 = new Button("Open");
	eightBall.setLayoutX(10);
	eightBall.setLayoutY(10);
	eightBall.setFont(font2);
	eightBall.setTextFill(Color.BLACK);
	button1.setLayoutX(470);
	button1.setLayoutY(20);
	_button2.setLayoutX(445);
	_button2.setLayoutY(60);
	_button3.setLayoutX(505);
	_button3.setLayoutY(60);
	_button2.setOnAction(new SaveHandler());
	_button3.setOnAction(new LoadHandler());
	button1.setOnAction(new MenuHandler());
	button1.setFont(font2);
	_button2.setFont(font2);
	_button3.setFont(font2);
	playerOne.setTextFill(Color.WHITE);
	playerTwo.setTextFill(Color.BLUE);
	playerOne.setLayoutX(200);
	playerTwo.setLayoutX(700);
	playerOne.setFont(font);
	playerTwo.setFont(font);
	_bottom.getChildren().addAll(playerOne,playerTwo,button1,_button2,_button3,eightBall);	
    }
    //Changes the pool cue's color based on the current turn's color and stripe/solid
    private void updateCueColor() {
	if (_numberSolidsPocketed + _numberStripesPocketed >0) {
	    if (_currentBallIsSolid) {
		this.setCueColor(Color.WHITE);
	    } else {
		this.setCueColor(Color.BLUE);
	    }
	}
	if (_eightBallPocketed) { //Hide the cue if gameover
	    this.setCueColor(Color.TRANSPARENT);
	}
    }
    //If current ball isn't same as pocketed ball, switch turns
    private void checkTurnSwitch() {
	if ((_correctBallPocketed == false 
	   && _alreadyCheckedTurn == false) //If correct ball wasn't pocketed 
	   || (this.getScratch()) 
	   && _alreadyCheckedTurn == false){ //Or scratch, then switch turns:
	    
	   if ((_numberSolidsPocketed + _numberStripesPocketed >0) 
	      && _eightBallPocketed == false) {
	       if (_firstBallPocketed) {
		   _currentBallIsSolid = !_currentBallIsSolid; //Switch current ball solid/stripe
	       }
	       _firstBallPocketed = true;
	       this.createTurnLabel();
	   }
	}
	_alreadyCheckedTurn = true;
	_correctBallPocketed = false;
    }
    //Fades a label colored depending on whose turn it is
    private void createTurnLabel() {
	Label turn = new Label();
	turn.setOpacity(0);
	    turn.setLayoutX(415);
	    turn.setLayoutY(400);
	    turn.setTextFill(Color.BLACK);
	FadeTransition ft = new FadeTransition(Duration.seconds(1));
	    ft.setNode(turn);
	    ft.setCycleCount(2);
	    ft.setFromValue(0);
	    ft.setToValue(1);
	    ft.setAutoReverse(true);
	Font font = Font.font("Vladimir Script",FontWeight.EXTRA_BOLD,40);
	    turn.setFont(font);
	if (_currentBallIsSolid) {
	    turn.setText("Solids' Turn");
	    turn.setTextFill(Color.WHITE);
	} else {
	    turn.setText("Stripes' Turn");
	    turn.setTextFill(Color.BLUE);
	}
	this.getCenter().getChildren().add(turn);
	ft.play();
    }
    //Layout for empty circles that represent ball holders
    private void createBallHolders() {
	double X = Constants.BALL_HOLDER;
	for (int i=0;i<7;i++) {
	    Circle circle = new Circle(Constants.BALL_RADIUS);
	    circle.setFill(Color.GREY);
	    circle.setOpacity(.2);
	    circle.setStroke(Color.BLACK);
	    circle.setCenterX(X);
	    circle.setCenterY(70);
	    _bottom.getChildren().add(circle);
	    X += 50;
	}
	X = 600;
	for (int i=0;i<7;i++) {
	    Circle circle = new Circle(Constants.BALL_RADIUS);
	    circle.setFill(Color.GREY);
	    circle.setOpacity(.2);
	    circle.setStroke(Color.BLACK);
	    circle.setCenterX(X);
	    circle.setCenterY(70);
	    _bottom.getChildren().add(circle);
	    X += 50;
	}
    }
    //Initiates gameover if the eightball is pocketed
    private void checkGameOver() {
	if (_eightBallPocketed && _alreadyGameOver == false) {
	    this.gameOver();
	    _alreadyGameOver =true;
	}
    }
    //Creates a gameover screen depending on current game conditions
    private void gameOver() {
	    for (int i=0;i<16;i++) {
		_balls[i].removeGraphically();
	    }
	    _button2.setDisable(true);
	    _button3.setDisable(true);
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
	    Label winner = new Label();
	    	winner.setLayoutX(345);
	    	winner.setLayoutY(300);
	    	winner.setFont(font2);
	    	winner.setOpacity(0);
	    FadeTransition ft = new FadeTransition(Duration.seconds(3));
		ft.setNode(gameOver);
		ft.setCycleCount(1);
		ft.setFromValue(0);
		ft.setToValue(1);
	    FadeTransition ft2 = new FadeTransition(Duration.seconds(3));;
		ft2.setNode(winner);
		ft2.setCycleCount(1);
		ft2.setFromValue(0);
		ft2.setToValue(1);
	    FadeTransition ft3 = new FadeTransition(Duration.seconds(3));
		ft3.setNode(rectangle);
		ft3.setToValue(.3);
	    //Solids win if the current ball is solid, there are 7 solids pocketed, and there was
            //no scratch made. Any other situation means that stripes must have won.
	    if ((_currentBallIsSolid && _numberSolidsPocketed == 7 && this.getScratch()==false)
		    || (_currentBallIsSolid == false && _numberStripesPocketed != 7 && this.getScratch()==false)) {
			winner.setTextFill(Color.BLACK);
			winner.setText("Solids Win");
	    } else {
			winner.setTextFill(Color.BLUE);
			winner.setText("Stripes Win");
	    }
	    this.getCenter().getChildren().addAll(rectangle,winner,gameOver);
	    ft2.play();
	    ft.play();
	    ft3.play();
    }
    //Opens .txt and records the x,y,isPocketed for every ball and
    //if the current ball is solid/stripe. Makes a "saved" label.
    private void save() {
	FileChooser fileChooser = new FileChooser();
	File directory = new File("Indy/save");
	fileChooser.setInitialDirectory(directory);
	fileChooser.setTitle("Create Save File");
	fileChooser.getExtensionFilters().addAll(
		new ExtensionFilter("Text Files", "*.txt"));
	File saveFile = fileChooser.showSaveDialog(this.getStage());
	//Stopping save if no file
	if (saveFile == null) {
	    Font font2 = Font.font("Alegreya",FontWeight.NORMAL,30);
	    Label failed = new Label("Cancelled Save");
		   failed.setLayoutX(410);
		   failed.setLayoutY(380);
		   failed.setFont(font2);
		   failed.setTextFill(Color.BLACK);
		   failed.setOpacity(0);
	    FadeTransition ft = new FadeTransition(Duration.seconds(1));
		    ft.setNode(failed);
		    ft.setCycleCount(2);
		    ft.setFromValue(0);
		    ft.setToValue(1);
		    ft.setAutoReverse(true);
		    ft.play();
		    this.getCenter().getChildren().add(failed);
	    return;
	}
	FileIO io = new FileIO();
	io.openWrite(saveFile.getAbsolutePath());
	for (int i=0;i<16;i++) {
	    io.writeDouble(_balls[i].getPositionX());
	    io.writeDouble(_balls[i].getPositionY());
	    int j = 0;
	    if (_balls[i].isPocketed() == true) {
		j = 1;
	    }
	    io.writeInt(j);
	}
	int currentBallIsSolid = 0;
	if (_currentBallIsSolid == true) {
	    currentBallIsSolid = 1;
	}
	io.writeInt(currentBallIsSolid);
	io.closeWrite();
	    Font font2 = Font.font("Alegreya",FontWeight.NORMAL,30);
	    Label saved = new Label();
	    	saved.setText("Game Saved");
	    	saved.setLayoutX(418);
	    	saved.setLayoutY(380);
	    	saved.setFont(font2);
	    	saved.setTextFill(Color.BLACK);
	    	saved.setOpacity(0);
	    FadeTransition ft = new FadeTransition(Duration.seconds(1));
		ft.setNode(saved);
		ft.setCycleCount(2);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.setAutoReverse(true);
		ft.play();
		this.getCenter().getChildren().add(saved);
    }
    //Opens .txt and reads the x,y,isPocketed for every ball and
    //sets the current ball as solid/stripe. Makes a "load" label.
    private void load() {
	FileChooser fileChooser = new FileChooser();
	File directory = new File("Indy/save");
	fileChooser.setInitialDirectory(directory);
	fileChooser.setTitle("Selected Save File");
	fileChooser.getExtensionFilters().addAll(
		new ExtensionFilter("Text Files", "*.txt"));
	File selectedFile = fileChooser.showOpenDialog(this.getStage());
	//Stopping load if no file selected
	if (selectedFile == null) {
	    Font font2 = Font.font("Alegreya",FontWeight.NORMAL,30);
	    Label failed = new Label("Cancelled Open");
		   failed.setLayoutX(410);
		   failed.setLayoutY(380);
		   failed.setFont(font2);
		   failed.setTextFill(Color.BLACK);
		   failed.setOpacity(0);
	    FadeTransition ft = new FadeTransition(Duration.seconds(1));
		    ft.setNode(failed);
		    ft.setCycleCount(2);
		    ft.setFromValue(0);
		    ft.setToValue(1);
		    ft.setAutoReverse(true);
		    ft.play();
		    this.getCenter().getChildren().add(failed);
	    return;
	}
	//Reading data
	FileIO io = new FileIO();
	io.openRead(selectedFile.getAbsolutePath());
	//Resetting values and restoring save settings
	_numberStripesPocketed = 0;
	_numberSolidsPocketed = 0;
	for (int i=0;i<16;i++) { 
	    //Adding back any balls that were pocketed in previous game and need to be 
	    //in the center pane
	    if (this.getCenter().getChildren().contains(_balls[i].getCircle()) == false){
		_balls[i].addGraphically();
		_balls[i].setOutOfPocket();
	    }
	    double x = io.readDouble();
	    double y = io.readDouble();
	    int pocketed = io.readInt();
	    _balls[i].setPositionX(x);
	    _balls[i].setPositionY(y);
	    _balls[i].setVelocityX(0);
	    _balls[i].setVelocityY(0);
	    //Handling pocketed balls and adding them to the ball rack depending on
	    //stripe or solid
	    if (pocketed == 1 && _balls[i].isSolid() && (i != 7) && (i != 15)) {
		_balls[i].setPocketed();
		_balls[i].removeGraphically();
		_bottom.getChildren().add(_balls[i].getSphere());
		_numberSolidsPocketed ++;
	    } else if (pocketed == 1 && (_balls[i].isSolid() == false) && (i != 7) && (i != 15)) {
		_balls[i].setPocketed();
		_balls[i].removeGraphically();
		if (_bottom.getChildren().contains(_balls[i].getSphere()) == false) {
		    _bottom.getChildren().add(_balls[i].getSphere());
		}
		_numberStripesPocketed ++;
	    }
	}
	//Setting cue color based on the conditions read
	int currentBallIsSolid = io.readInt();
	if (this.getCue() != null) {
	    this.getCue().removeCue();
	}
	if (currentBallIsSolid == 1) {
	    _currentBallIsSolid = true;
	    this.setCue(Color.WHITE);
	} else {
	    _currentBallIsSolid = false;
	    if (this.getCue() != null) {
		this.getCue().removeCue();
		this.setCue(Color.BLUE);
	    }
	}
	io.closeRead();
	Font font2 = Font.font("Alegreya",FontWeight.NORMAL,30);
	Label opened = new Label();
	    opened.setText("Game Opened");
	    opened.setLayoutX(410);
	    opened.setLayoutY(380);
	    opened.setFont(font2);
	    opened.setTextFill(Color.BLACK);
	    opened.setOpacity(0);
	FadeTransition ft = new FadeTransition(Duration.seconds(1));
	    ft.setNode(opened);
	    ft.setCycleCount(2);
	    ft.setFromValue(0);
	    ft.setToValue(1);
	    ft.setAutoReverse(true);
	    ft.play();
	this.getCenter().getChildren().add(opened);
    }
    //Save to .txt when triggered
    private class SaveHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		EightBallGame.this.save();
	    }
    }  
    //Loads from .txt when triggered
    private class LoadHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		EightBallGame.this.load();
	    }
    }  
    //Resets the whole application when triggered
    private class MenuHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		App app = new App();
	    	app.start(EightBallGame.this.getStage());
	    }
    }  
}
 
