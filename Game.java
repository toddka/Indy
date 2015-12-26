package Indy;

import javafx.event.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
/**
  * This is the  Game class. It provides many of the general 
  * methods for both 8Ball and Time Attack. It contains a table,
  * pool cue, and all the pool balls. Essentially, this class
  * knows when certain events happen (scratches, pocketing, etc.)
  * and its subclasses specify what happens once those events
  * occur.
  */
public abstract class Game {
    private BorderPane _root;
    private Pane _center;
    private Ball[] _balls;
    private Table _table;
    private PoolCue _cue;
    private Color _cueColor;
    private Stage _stage;
    private boolean _isScratch;
    private boolean _isStopped;
    
    public Game(BorderPane root, Stage stage) {
	_isScratch = false;
	_isStopped = false;
	_stage = stage;
	_root = root;
	_cueColor = Color.BLACK; //Starts black b/c stripes/solids is not yet specified
	_balls = new Ball[16];
	_center = new Pane();
	    _center.setMaxHeight(500);
	    _center.setMaxWidth(1000);
	    _center.setStyle("-fx-background-image: url('Indy/surface.jpg')");
	    _root.setTop(_center);
	_table = new Table(_center);
	this.setupBalls();
	_root.setOnMouseMoved(new MouseMovedHandler());
	_root.setOnMouseClicked(new MouseClickedHandler());
	_cue = new PoolCue(_balls[15],_center,_cueColor);
    }
    public abstract void update();
    public abstract void pocketBall(int i);
    //Sets up the pool balls to begin the game
    public void setupBalls() {
	@SuppressWarnings("unused")
	BallMachine machine = new BallMachine(_balls,_center);
    }
    //Constantly checking each ball to see if its next position will be a collision
    //with either another ball, a pocket, or a wall.
    public void checkCollision() {
	for (int i=0;i<16;i++) {
	    boolean isByPocket = false; 
	    double Y = _balls[i].getPositionY();
	    double X = _balls[i].getPositionX();
	    /** If ball is in either of the four corners and if it hits a wall, rebound off at an angle.
	      * Detects if it hits a wall by creating a Shape out of the intersection of the angled wall
	      * and ball, and if that intersection has an area, then the ball must rebound. Shape.intersect
	      * is very memory intensive, so the method is only run when the ball is by a pocket to save mem.
	      */
	    if ((Y <70 && X <70) //upperleft
		    || (Y <70 && X > 930) //upperright
		    || (Y >430 && X <70) //lowerleft
		    || (Y >430 && X >930) //lowerright
		    || (Y <80 && X>475 && X<525) //uppermiddle
		    || (Y >440 && X>475 && X<525)){ //lowermiddle
		if (Shape.intersect(_table.getWall(4),_balls[i].getCircle()).getBoundsInLocal().isEmpty() == false) {
		    _balls[i].impactPocketWall();
		}
		isByPocket = true;
		this.checkPocket();
	    } 
	    //Bumper collision can only happen if the ball is not in the bounds of a pocket.
	    if (isByPocket == false) {
		this.checkBumperCollision(i);
	    }
	    //Checks the balls position, and if next position overlaps any ball it initiates collision
	    for (int j=0;j<16;j++) {
		double distance = Math.sqrt(
			Math.pow(_balls[j].getPositionX() + Constants.DURATION * _balls[j].getVelocityX() 
				- _balls[i].getPositionX() - Constants.DURATION * _balls[i].getVelocityX() ,2.0) +
			Math.pow(_balls[j].getPositionY() + Constants.DURATION * _balls[j].getVelocityY() 
				-_balls[i].getPositionY() - Constants.DURATION * _balls[i].getVelocityY() ,2.0));
		//i<j condition stops double counting of the same collision
		if (distance <= 2 * Constants.BALL_RADIUS && i < j 
			&& _balls[i].isPocketed() == false && _balls[j].isPocketed() == false) {
		    _balls[i].impactBall(_balls[j]);
		}
	    }   
	}
    }
    //If the specific ball's next position is within the bounds of any of the bumpers, rebound
    public void checkBumperCollision(int i) {
	    if (_balls[i].getPositionY() + Constants.DURATION * _balls[i].getVelocityY() <= 40 + Constants.BALL_RADIUS 
		    || _balls[i].getPositionY() + Constants.DURATION * _balls[i].getVelocityY() >= 460 - Constants.BALL_RADIUS ) {
		    	_balls[i].impactWallY();
	    } else if (_balls[i].getPositionX() + Constants.DURATION * _balls[i].getVelocityX() <= 40  + Constants.BALL_RADIUS  
		     || _balls[i].getPositionX() + Constants.DURATION * _balls[i].getVelocityX() >= 960  - Constants.BALL_RADIUS ) {
		    	_balls[i].impactWallX();  		
	    }
    }
    //If the distance between the any ball and any pocket is greater than 1.51x the ball's
    //radius, pocket the ball
    public void checkPocket() {
	for (int i=0;i<16;i++) {
	    for (int j=0;j<6;j++) {
		double distance = Math.sqrt(
			Math.pow(_table.getPocket(j).getCenterX()
				- _balls[i].getPositionX(),2.0) +
			Math.pow(_table.getPocket(j).getCenterY() 
				-_balls[i].getPositionY(),2.0));
		if (distance <= Constants.BALL_RADIUS * 1.51) {    
		    this.pocketBall(i);
		}
	    }
	}
    }
    //Adds the pool cue depending on if there is a scratch and if the
    //balls are all stopped
    public void checkCue() {
	_isStopped = true;
	for (int i=0;i<16;i++) {
	    if (_balls[i].getVelocityX() + _balls[i].getVelocityY() != 0.0) {
		_isStopped = false; //If any ball is moving this is false
	    }
	}
	if (_isStopped == true && _cue == null && _isScratch == false) {
	    _cue = new PoolCue(_balls[15],_center,_cueColor); 
	}
    }
    public Ball[] getBalls() {
	return _balls;
    }
    public PoolCue getCue() {
	return _cue;
    }
    public boolean getScratch() {
	return _isScratch;
    }
    public Stage getStage() {
	return _stage;
    }
    public Pane getCenter() {
	return _center;
    }
    public void setCueColor(Color color) {
	_cueColor = color;
    }
    public void setCue(Color color) {
	_cue = new PoolCue(_balls[15],_center,color);
    }
    public void setScratch() {
	_isScratch = true;
    }
    //Checks if the cue ball can move to a specific mouse position during a scratch
    private boolean isLegalCueMove(double X, double Y) {
	boolean isLegal = false;
	//If ball is in the center of board and not pocket corner:
	if ((Y >53 && X >53)  //upperleft
		&& (Y >53 && X < 947) //upperright
		&& (Y <447 && X >53) //lowerleft
		&& (Y <447 && X <947)) { //lowerright
	    	    isLegal = true;
		    //Checks each ball besides cue to see if they are overlapping
		    for (int j=0;j<15;j++) {
			double distance = Math.sqrt(
				Math.pow(_balls[j].getPositionX() - X ,2.0) +
				Math.pow(_balls[j].getPositionY() - Y ,2.0));
			if (distance <= 2 * Constants.BALL_RADIUS) {
			    isLegal=false;
			}
		    } 	
	} 
	return isLegal;
    }
    //Move cue ball to the mouse position if there is a scratch, all balls
    //are stopped, and nothing is in the way. Also moves the pool cue.
    private class MouseMovedHandler implements EventHandler<MouseEvent> {
	 @Override
	 public void handle(MouseEvent e) {
	     double X = e.getX();
	     double Y = e.getY();
	     if (_isScratch && Game.this.isLegalCueMove(X,Y) && _isStopped) {
		 _balls[15].setPositionX(X);
		 _balls[15].setPositionY(Y);
	     } else if (_cue != null) {
		 _cue.setPosition(X,Y);
		 e.consume();
	     }
	 }
    }
    //Launches the cue ball based on the pool cue's power and position. Removes the 
    //the pool cue and turns off the scratch.
    private class MouseClickedHandler implements EventHandler<MouseEvent> {
	 @Override
	 public void handle(MouseEvent e) {
	     	if (_cue != null) {
	     	    double power = _cue.getPower();
	     	    double angle = _cue.getAngle();
	     	    _balls[15].setVelocityX(power * Math.cos(angle));
	     	    _balls[15].setVelocityY(power * Math.sin(angle));
	     	    _cue.removeCue();
	     	    _cue = null;
	     	}
	     	_isScratch = false;
		e.consume();
	 }
   }
}