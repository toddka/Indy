package Indy;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
/**
 * This is the BallMachine class. It creates 7 stripe/solid balls,
 * a cue ball, and 8 ball. The machine sets the skin for each 
 * ball's sphere and sets each balls initial position.
 */
public class BallMachine {
    private Ball[] _balls;
    private Pane _center;
    private Ball _cueBall;
    
    public BallMachine(Ball[] balls, Pane center) {
	_balls = balls;
	_center = center;
	this.createBalls();
	this.positionBalls();
	this.createSphere();
    }
    //Creating 15 balls, setting 7 as stripes
    public void createBalls() {
	for (int i =0; i<15; i++) {
	    Ball ball = new Ball(_center);
	    _balls[i] = ball;
	}
	for (int j=8;j<15;j++) {
	    _balls[j].setStripe();
	}
	_cueBall = new Ball(_center);
	_balls[15]=_cueBall;
    }
    //Setting the skin for each ball by iterating i and adding i to
    //the string used to find the image file
    public void createSphere() {
	for (int i =1; i<=16; i++) {
	    Sphere sphere = _balls[i-1].getSphere();
	    	String image = "Indy/skins/"+i+"ball.jpg";
	    	double width  = 8192 / 2d / 5;
	    	double height = 4092 / 2d / 5;
	    	PhongMaterial ballMaterial = new PhongMaterial();
	    	ballMaterial.setDiffuseMap(new Image(image,width,height,true,true));
	    	sphere.setMaterial(ballMaterial);
	}
    }
    //Positioning each ball at the start of a game
    public void positionBalls() {
	_balls[15].setPositionX(200);
	_balls[15].setPositionY(230);
	_balls[14].setPositionX(800);
	_balls[14].setPositionY(150);
	_balls[13].setPositionX(800);
	_balls[13].setPositionY(185);
	_balls[12].setPositionX(800);
	_balls[12].setPositionY(220);
	_balls[11].setPositionX(800);
	_balls[11].setPositionY(255);
	_balls[10].setPositionX(800);
	_balls[10].setPositionY(290);
	_balls[9].setPositionX(770);
	_balls[9].setPositionY(170);
	_balls[8].setPositionX(770);
	_balls[8].setPositionY(205);
	_balls[7].setPositionX(770);
	_balls[7].setPositionY(240);
	_balls[6].setPositionX(770);
	_balls[6].setPositionY(275);
	_balls[5].setPositionX(740);
	_balls[5].setPositionY(190);
	_balls[4].setPositionX(740);
	_balls[4].setPositionY(225);
	_balls[3].setPositionX(740);
	_balls[3].setPositionY(260);
	_balls[2].setPositionX(710);
	_balls[2].setPositionY(210);
	_balls[1].setPositionX(710);
	_balls[1].setPositionY(245);
	_balls[0].setPositionX(680);
	_balls[0].setPositionY(230);
	
    }
}
