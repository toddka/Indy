package Indy;

import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
/**
 * This is the Table class. It contains 6 circular pockets and
 * 4 walls with skewed edges near the pockets. 
 */
public class Table {
    private Pane _center;
    private Shape[] _walls;
    private Circle[] _pockets;
    
    public Table(Pane center) {
	_center = center;
	_walls = new Shape[5]; //5th entry is a union of all walls
	_pockets = new Circle[6];
	this.createWalls();
	this.createPockets();
	this.createLights();
    }
    /**
     * The walls were made using 4 rectangles as the base (easy) and then
     * cuts were made in each wall with lines to make edging near the pockets
     * (hard). This was done using multiple Shape.subtract shapes on top of each
     * other. Last, a single border was made with Shape.union. The single
     * shape is necessary for collision detection new pockets.
     */
    public void createWalls() {	
	//Left wall
	Rectangle wall1 = new Rectangle();
	wall1.setX(0);
	wall1.setY(0);
	wall1.setHeight(500);
	wall1.setWidth(40);
        Line line0 = new Line(30.0,30.0,50.0,50.0);
        line0.setStrokeWidth(Constants.BALL_RADIUS * 3.45);
	Line line1 = new Line(30.0,470.0,50.0,450.0);
	line1.setStrokeWidth(Constants.BALL_RADIUS * 3.45);
	Shape subtract1 = Shape.subtract(wall1, line0);
	_walls[1] = Shape.subtract(subtract1, line1);
	_walls[1].setFill(Color.rgb(102, 51, 0));
	//Bottom wall
	Rectangle wall2 = new Rectangle();
	wall2.setX(0);
	wall2.setY(460);
	wall2.setHeight(40);
	wall2.setWidth(1000);
	Shape subtract21 = Shape.subtract(wall2, line1);
	Line line2 = new Line(503.0,470.0,523.0,420.0);
	line2.setStrokeWidth(Constants.BALL_RADIUS * 2.6);
	Shape subtract22 = Shape.subtract(subtract21,line2);
	Line line3 = new Line(970.0,470.0,950.0,450.0);
	line3.setStrokeWidth(Constants.BALL_RADIUS * 3.45);
	Shape subtract23 = Shape.subtract(subtract22, line3);
	Line line4 = new Line(497.0,470.0,477.0,420.0);
	line4.setStrokeWidth(Constants.BALL_RADIUS * 2.6);
	_walls[2] = Shape.subtract(subtract23, line4);
	_walls[2].setFill(Color.rgb(102, 51, 0));
	//Right wall
	Rectangle wall3 = new Rectangle();
	wall3.setX(960);
	wall3.setY(0);
	wall3.setHeight(500);
	wall3.setWidth(40);
	line1.setStrokeWidth(Constants.BALL_RADIUS * 3.45);
	Shape subtract31 = Shape.subtract(wall3, line3);
	Line line5 = new Line(970.0,30.0,950.0,50.0);
	line5.setStrokeWidth(Constants.BALL_RADIUS * 3.45);
	_walls[3] = Shape.subtract(subtract31, line5);
	_walls[3].setFill(Color.rgb(102, 51, 0));
	//Top wall
	Rectangle wall0 = new Rectangle();
	wall0.setX(0);
	wall0.setY(0);
        wall0.setHeight(40);
        wall0.setWidth(1000);
        Shape subtract01= Shape.subtract(wall0, line0);
        Shape subtract02 = Shape.subtract(subtract01, line5);
        Line line6 = new Line(503.0,30.0,523.0,80.0);
        line6.setStrokeWidth(Constants.BALL_RADIUS * 2.6);
        Shape subtract03 = Shape.subtract(subtract02, line6);
        Line line7 = new Line(497.0,30.0,477.0,80.0);
        line7.setStrokeWidth(Constants.BALL_RADIUS * 2.6);
        _walls[0] = Shape.subtract(subtract03, line7);
	_walls[0].setFill(Color.rgb(102, 51, 0));
	//Unifying all aspects into a single border structure:
	Shape union1 = Shape.union(_walls[0], _walls[1]);
	Shape union2 = Shape.union(union1, _walls[2]);
	_walls[4]  = Shape.union(union2, _walls[3]);
	Image wood = new Image("Indy/wood.jpg");
	ImagePattern woodpattern = new ImagePattern(wood);
	_walls[4].setFill(woodpattern);
	//Adding the single border to the pane
	_center.getChildren().add(_walls[4]);
    }
    //Creating the 6 pocket circles with radii depending on if corner or middle
    public void createPockets() {
	for (int i=0;i<6;i++) {
	    _pockets[i] = new Circle();
	    _pockets[i].setCenterX(500.0);
	    _pockets[i].setCenterY(500.0);
	}
	_pockets[0].setCenterX(25.0);
	_pockets[0].setCenterY(25.0);
	_pockets[0].setRadius(Constants.CORNER_POCKET_RADIUS);
	_pockets[1].setCenterX(25.0);
	_pockets[1].setCenterY(475.0);
	_pockets[1].setRadius(Constants.CORNER_POCKET_RADIUS);
	_pockets[2].setCenterX(500.0);
	_pockets[2].setCenterY(485.0);
	_pockets[2].setRadius(Constants.MIDDLE_POCKET_RADIUS);
	_pockets[3].setCenterX(975.0);
	_pockets[3].setCenterY(475.0);
	_pockets[3].setRadius(Constants.CORNER_POCKET_RADIUS);
	_pockets[4].setCenterX(975.0);
	_pockets[4].setCenterY(25.0);
	_pockets[4].setRadius(Constants.CORNER_POCKET_RADIUS);
	_pockets[5].setCenterX(500.0);
	_pockets[5].setCenterY(15.0);
	_pockets[5].setRadius(Constants.MIDDLE_POCKET_RADIUS);
	_center.getChildren().addAll(_pockets);
    } 
    //Creating the lighting above the table for the 3D spheres
    public void createLights() {
	PointLight light = new PointLight();
	light.setColor(Color.WHITE);
	Group lightGroup = new Group();
	lightGroup.setTranslateZ(-700);
	lightGroup.setTranslateX(500);
	lightGroup.setTranslateY(250);
	lightGroup.getChildren().add(light);
	_center.getChildren().add(lightGroup);
    }
    public Shape getWall(int i) {
	return _walls[i];
    }
    public Circle getPocket(int i) {
	return _pockets[i];
    }
}
