package Indy;

import javafx.event.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * This is the Menu class. It contains the Timeline that updates at 60fps.
 * It also sets up the visuals of the menu, which has buttons, labels, and
 * a 3D rotating sphere.
 */
public class Menu {
    private BorderPane _root;
    private Game _game;
    private Stage _stage;
    private Pane _menuPane;
    private Button _rulesButton;
    
    public Menu(Stage stage) {
	this.setupMenu();
	_stage = stage;
    }
    //Creates 3 buttons, multiple labels, and a rotating sphere
    private void setupMenu() {
	_root = new BorderPane();
	_menuPane = new Pane();
	Button button1 = new Button("8Ball");
	Button button2 = new Button("Time Attack");
	_rulesButton = new Button("Rules");
	Label title = new Label("Billiards");
	Label author = new Label("By Todd Ashley");
	Font font = Font.font("Vladimir Script",FontWeight.EXTRA_BOLD,120);
	Font font2 = Font.font("Vladimir Script",FontWeight.EXTRA_BOLD,30);
	Font font3 = Font.font("Times New Roman",FontWeight.EXTRA_BOLD,15);
		title.setTextFill(Color.BLACK);
		title.setFont(font);
		title.setLayoutX(300);
		title.setLayoutY(10);
		author.setLayoutX(410);
		author.setLayoutY(150);
		author.setTextFill(Color.BLACK);
		author.setFont(font2);
		button1.setLayoutX(360);
		button1.setLayoutY(450);
		button2.setLayoutX(580);
		button2.setLayoutY(450);
		_rulesButton.setLayoutX(470);
		_rulesButton.setLayoutY(450);
		button1.setFont(font3);
		button2.setFont(font3);
		_rulesButton.setFont(font3);
		button1.setOnAction(new EightBallHandler());
		button2.setOnAction(new SinglePlayerHandler());
		_rulesButton.setOnAction(new RulesHandler());
	Sphere ball = new Sphere(100);
    		String image = "Indy/skins/8ball.jpg";
    		double width  = 8192 / 2d / 5;
    		double height = 4092 / 2d / 5;
    		PhongMaterial ballMaterial = new PhongMaterial();
    		ballMaterial.setDiffuseMap(new Image(image,width,height,true,true));
    		ball.setMaterial(ballMaterial);
    		ball.setLayoutX(500);
    		ball.setLayoutY(300);
        RotateTransition rotate = new RotateTransition(Duration.seconds(8),ball);
        	rotate.setAxis(Rotate.Y_AXIS);
        	rotate.setFromAngle(360);
        	rotate.setToAngle(0);
        	rotate.setInterpolator(Interpolator.LINEAR);
        	rotate.setCycleCount(RotateTransition.INDEFINITE);
        	rotate.play();
	_menuPane.setStyle("-fx-background-image: url('Indy/menu2.jpg')");
	_menuPane.getChildren().addAll(ball,button1,button2,_rulesButton,title,author);
	_root.setCenter(_menuPane);
	
    }
    //Creates a timeline that controls the refreshing of all the games
    private void setupTimeline() {
	KeyFrame key = new KeyFrame(Duration.seconds(Constants.DURATION),
		new TimeHandler());
	Timeline timeline = new Timeline(key); 
	timeline.setCycleCount(Timeline.INDEFINITE);
	timeline.play();
    }
    //Displays the rules
    private void showRules() {
	Label eightBall = new Label();
	eightBall.setText(
		"8 Ball Rules: \n" +
		"The first ball potted determines whether you are\n" +
		"Stripes or Solids. \n" +
		"To Win: 1. Pot the 8 ball after potting all your balls. \n" +
		"To Lose: 1. Pot the 8 ball before all your balls are potted. \n" +
		"                2. Pot the cue ball and the 8 ball together. \n"
		);
	Label time = new Label();
	time.setText(
		"Time Attack Rules: \n" +
		"Pot any ball to gain time on the clock \n" +
		"Potting the cue ball loses time \n" +
		"To Win: 1. Survive as long as possible \n" +
		"                  and beat the Highscore \n"
		);
	Font font = Font.font("Times New Roman",FontWeight.EXTRA_BOLD,15);
	Font font2 = Font.font("Times New Roman",FontWeight.EXTRA_BOLD,15);
	eightBall.setTextFill(Color.BLACK);
	eightBall.setFont(font);
	eightBall.setLayoutX(20);
	eightBall.setLayoutY(150);
	time.setLayoutX(20);
	time.setLayoutY(280);
	time.setTextFill(Color.BLACK);
	time.setFont(font2);
	_menuPane.getChildren().addAll(eightBall,time);
    }
    //Creates an EightBallGame
    private void setupVisuals8Ball() {
	_game = new EightBallGame(_root,_stage);
    }
    //Creates a TimeAttack game
    private void setupVisualsSinglePlayer() {
	_game = new SinglePlayer(_root,_stage);
    }
    public Pane getRoot() {
	return _root;
    }
    private class TimeHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		if (_game != null) {
		    _game.update();
		}
	    }
    }  
    private class EightBallHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		Menu.this.setupVisuals8Ball();
		Menu.this.setupTimeline();
	    }
    }  
    private class SinglePlayerHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		Menu.this.setupVisualsSinglePlayer();
		Menu.this.setupTimeline();
	    }
    }  
    private class RulesHandler implements EventHandler<ActionEvent>{
	    @Override
	    public void handle(ActionEvent event) {
		Menu.this.showRules();
		_rulesButton.setDisable(true);
	    }
    }  
}
