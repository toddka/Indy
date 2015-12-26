package Indy;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
/**
 * This is the PoolCue class. The cue's position depends on the mouse
 * and cue ball. Its power is directly proportional to its length.
 */
public class PoolCue {
    private Line _cue;
    private Ball _cueBall;
    private Pane _center;
    private Color _color;
    public PoolCue(Ball cueBall, Pane center, Color color) {
	_cue = new Line();
	_cueBall = cueBall;
	_center = center;
	_color = color;
	_cue.setStroke(Color.TAN);
	_cue.setStrokeWidth(5);
	_center.getChildren().add(_cue);
    }
    //Sets position based on mouse and cue and changes opacity
    //based on the length. 100% opacity when the power maxes out
    public void setPosition(double x, double y) {
	_cue.setStartX(_cueBall.getPositionX());
	_cue.setStartY(_cueBall.getPositionY());
	_cue.setEndX(x);
	_cue.setEndY(y);
	double distance = Math.sqrt(
		Math.pow(_cue.getStartX() -_cue.getEndX(),2.0) +
		Math.pow(_cue.getStartY() -_cue.getEndY(),2.0));
	if (distance > Constants.MAX_CUE_SIZE) {
	    distance = Constants.MAX_CUE_SIZE;
	}
	double opacity = ((1/Constants.MAX_CUE_SIZE) * distance);
	_cue.setStroke(_color);
	_cue.setOpacity(opacity);
    }
    //Calculates power based on current length of cue
    public double getPower() {
	double distance = Math.sqrt(
		Math.pow(_cue.getStartX() -_cue.getEndX(),2.0) +
		Math.pow(_cue.getStartY() -_cue.getEndY(),2.0));
	if (distance > Constants.MAX_CUE_SIZE) {
	    distance = Constants.MAX_CUE_SIZE;
	}
	return distance * Constants.DIST_TO_POWER;
    }
    //Calculates angle of the cue wrt 0 from 0-90 degrees
    public double getAngle() {
	double degrees = Math.atan2(-1*(_cue.getEndX() - _cue.getStartX()),(_cue.getEndY() - _cue.getStartY())) + 3*Math.PI/2;
	return (degrees + 360) % 360;
    }
    public void removeCue() {
	_center.getChildren().remove(_cue);
    }
}
