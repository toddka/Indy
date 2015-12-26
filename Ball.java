package Indy;

import javafx.geometry.Point3D;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
/**
 * This is the Ball class. A ball can be striped/solid, has a 
 * position and velocity, has a sphere and circle, and can be pocketed.
 */
public class Ball {
    private Pane _center;
    private Circle _circle;
    private Sphere _sphere;
    private double _positionX;
    private double _positionY;
    private double _velocityX;
    private double _velocityY;
    private boolean _isSolid;
    private boolean _isPocketed;

    public Ball(Pane center) {
	_positionX = 0;
	_positionY = 0;
	_velocityX = 0;
	_velocityY = 0;
	_center = center;
	_isSolid = true;
	_isPocketed = false;
	this.createCircle();
	this.createSphere();
    }
    //Creating a circle for collision detection and shadowing
    public void createCircle() {
	_circle = new Circle();
	DropShadow shadow = new DropShadow(Constants.BALL_RADIUS,2,-2,Color.BLACK);
	_circle.setEffect(shadow);
	_circle.setRadius(Constants.BALL_RADIUS);
	_center.getChildren().add(_circle);
    }
    //Creating a sphere
    public void createSphere() {
	_sphere = new Sphere(Constants.BALL_RADIUS);
	_sphere.setTranslateZ(-10);
	_center.getChildren().addAll(_sphere);

    }
    //Updating the position based on velocity and acceleration
    public void updatePosition() {
	double previousPositionX = _positionX;
	double previousPositionY = _positionY;
	//x=x0+v0t
	_positionX = _positionX + _velocityX * Constants.DURATION;
	_positionY = _positionY + _velocityY * Constants.DURATION;
	_circle.setCenterX(_positionX);
	_circle.setCenterY(_positionY);
	_sphere.setLayoutX(_positionX);
	_sphere.setLayoutY(_positionY);
	//Calculating deceleration components (Fk=ma splits into x and y components
	//based on angle of the ball's total velocity).
	double angle = Math.atan(Math.abs((_positionY-previousPositionY))/Math.abs((_positionX-previousPositionX)));
	double newVelocityX = 0.0;
	double newVelocityY = 0.0;
	//New velocitis depend on the angle and direction of travel according to
	//the equations shown below
	if (_velocityX > 0) {
	    newVelocityX = _velocityX - Math.cos(angle)*Constants.DECELERATION * 
		    Constants.DURATION;
	} else if (_velocityX < 0) {
	    newVelocityX = _velocityX + Math.cos(angle)*Constants.DECELERATION * 
		    Constants.DURATION;
	}
	if (_velocityY > 0) {
	    newVelocityY = _velocityY - Math.sin(angle)*Constants.DECELERATION * 
		    Constants.DURATION;
	} else if (_velocityY < 0) {
	    newVelocityY = _velocityY + Math.sin(angle)*Constants.DECELERATION * 
		    Constants.DURATION;
	}
	//Rounds the velocity to zero if they are very small to tell the game that
	//everything has stopped
	if (Math.abs(_velocityX) < .3 && Math.abs(_velocityY) < .3) {
	    newVelocityX = 0.0;
	    newVelocityY = 0.0;
	}
	_velocityX = newVelocityX;
	_velocityY = newVelocityY;
    }
    //Sets the amount of rotation of the sphere depending on the speed and
    //axis of rotation depending on the velocity direction
    public void updateRotation() {
	//Rotation around X axis corresponding to Velocity Y
	double nextPositionX = _positionX + _velocityX * Constants.DURATION;
	double nextPositionY = _positionY + _velocityY * Constants.DURATION;
	//Angle describing the direction of travel
	double angle = Math.atan(Math.abs((nextPositionY-_positionY))/Math.abs((nextPositionX-_positionX)));
	double velocityMagnitude = Math.sqrt(
			Math.pow(_velocityX,2.0) +
			Math.pow(_velocityY,2.0));
	//Sets the rotational axis based on the +/- sign of the 
	//velocityX and velocityY and the amount of rotation based the magnitude
	if (_velocityX > 0 && _velocityY >0) {
		Point3D axis = new Point3D(-Math.sin(angle),Math.cos(angle),0); //perpendicular to ball velocity
		_sphere.setRotationAxis(axis);
		_sphere.setRotate(360 * velocityMagnitude / (2*Math.PI*Constants.BALL_RADIUS));
	} else if (_velocityX > 0 && _velocityY <0) {
		Point3D axis = new Point3D(Math.sin(angle),Math.cos(angle),0); //perpendicular to ball velocity
		_sphere.setRotationAxis(axis);
		_sphere.setRotate(360 * velocityMagnitude / (2*Math.PI*Constants.BALL_RADIUS));
	} else if (_velocityY > 0 && _velocityX<0) {
	    	Point3D axis = new Point3D(-Math.sin(angle),-Math.cos(angle),0); //perpendicular to ball velocity
		_sphere.setRotationAxis(axis);
		_sphere.setRotate(360 * velocityMagnitude / (2*Math.PI*Constants.BALL_RADIUS));
	} else if (_velocityY<0 && _velocityX<0){
	    	Point3D axis = new Point3D(Math.sin(angle),-Math.cos(angle),0); //perpendicular to ball velocity
		_sphere.setRotationAxis(axis);
		_sphere.setRotate(360 * velocityMagnitude / (2*Math.PI*Constants.BALL_RADIUS));
	}
    }
    //Using momentum and restitution formulas to calculate the new velocity
    //after impact for each ball involved in the collision
    public void impactBall(Ball ball2) {
	double CVx1 = _velocityX; //Ball 1 current velocityX
	double CVx2 = ball2.getVelocityX(); //Ball 2 current velocityX
	double CVy1 = _velocityY; //Ball 1 current velocityY
	double CVy2 = ball2.getVelocityY(); //Ball 2 current velocityY
	double Px1 = _positionX; //Ball 1 current positionX
	double Px2 = ball2.getCircle().getCenterX(); //Ball 2 current positionX
	double Py1 = _positionY; //Ball 1 current positionY
	double Py2 = ball2.getCircle().getCenterY(); //Ball 2 current positionY
	double Vn = ((CVx1 - CVx2)*(Px1-Px2)+(CVy1-CVy2)*(Py1-Py2))/(2*Constants.BALL_RADIUS);
	//New Ball 1 and Ball 2 X,Y velocities
	double UVx1 = CVx1 - (0.5*(1+Constants.eB)*Vn*(Px1-Px2))/(2*Constants.BALL_RADIUS);
	double UVx2 = CVx2 + (0.5*(1+Constants.eB)*Vn*(Px1-Px2))/(2*Constants.BALL_RADIUS);
	double UVy1 = CVy1 - (0.5*(1+Constants.eB)*Vn*(Py1-Py2))/(2*Constants.BALL_RADIUS);
	double UVy2 = CVy2 + (0.5*(1+Constants.eB)*Vn*(Py1-Py2))/(2*Constants.BALL_RADIUS);
	_velocityX = UVx1;
	_velocityY = UVy1;
	ball2.setVelocityX(UVx2);
	ball2.setVelocityY(UVy2);
    }
    //Reverses the X Velocity
    public void impactWallX() {
	_velocityX = -1 * Constants.eW * _velocityX;
    }
    //Reverses the Y Yelocity
    public void impactWallY() {
	_velocityY = -1 * Constants.eW * _velocityY;
    }
    //If the ball reaches coordinates within the walls' bounds it rebounds
    public void impactPocketWall() {
	double storingVelocityX = _velocityX;
	//Mimicks impact with a 45 degree wall by switching X,Y velocities 
	if (this.getPositionX() <100 && this.getPositionY() < 100){
	    _velocityX = _velocityY;
	    _velocityY = storingVelocityX;
	} else if (this.getPositionX() <100 && this.getPositionY() > 100) {
	    _velocityX = -_velocityY;
	    _velocityY = - storingVelocityX;
	} else if (this.getPositionX() > 700 && this.getPositionY() < 100) {
	    _velocityX = -_velocityY;
	    _velocityY = storingVelocityX;
	} else if (this.getPositionX() > 700 && this.getPositionY() > 100) {
	    _velocityX = _velocityY;
	    _velocityY = storingVelocityX;
	} else if (this.getPositionX() > 475 && this.getPositionY() < 80) {
	    _velocityX = -_velocityY;
	    _velocityY = storingVelocityX;
	} else if (this.getPositionX() > 475 && this.getPositionY() > 440) {
	    _velocityX = -_velocityY;
	    _velocityY = storingVelocityX;
	}
    }
    public Circle getCircle() {
	return _circle;
    }
    public Sphere getSphere() {
	return _sphere;
    }
    public double getVelocityX() {
	return _velocityX;
    }
    public double getVelocityY() {
	return _velocityY;
    }
    public double getPositionX() {
	return _positionX;
    }
    public double getPositionY() {
	return _positionY;
    }
    public void setVelocityX(double x) {
	_velocityX = x;
    }
    public void setVelocityY(double y) {
	_velocityY = y;
    }
    public void setPositionX(double x) {
	_positionX = x;
    }
    public void setPositionY(double y) {
	_positionY = y;
    }
    public void setStripe() {
	_isSolid = false;
    }
    public void setPocketed() {
	_isPocketed = true;
    }
    public void setOutOfPocket() {
	_isPocketed = false;
    }
    public void removeGraphically() {
	_center.getChildren().remove(_sphere);
	_center.getChildren().remove(_circle);
    }
    public void addGraphically() {
 	_center.getChildren().add(_circle);
 	_center.getChildren().add(_sphere);
     }
    public boolean isSolid() {
	return _isSolid;
    }
    public boolean isPocketed() {
	return _isPocketed;
    }
}
