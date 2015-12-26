package Indy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
  * This is the main class for Pool! This game features two modes of pool, 
  * 8Ball and Time Attack. In 8Ball, two players face off in classic 8Ball pool
  * featuring 3D pool balls, scratching, turn switching, save/load, and more. 
  * In Time Attack, one player plays against the clock to try and beat the 
  * current high score by pocketing balls to keep time going. Enjoy
  * 
  * NOTE: Formatting may be formatted wrong if these files are opened in Sublime. 
  * However, Eclipse shows the correct formatting.
  * @author <tashley>
  * 
  */
public class App extends Application {
    	@Override
    	public void start(Stage stage) {
    	    //Menu takes in the stage as parameter to allow for reset
    	    Menu menu = new Menu(stage);
    	    //Creates top-level object, sets up the scene, and shows the stage
    	    Scene scene = new Scene(menu.getRoot(),Constants.SCENE_WIDTH,Constants.SCENE_HEIGHT);
    	    stage.getIcons().add(new Image("Indy/icon.jpg"));
    	    stage.setScene(scene);
    	    stage.setTitle("Billiards");
    	    stage.setResizable(false);
    	    stage.show();
    	}
	public static void main(String[] argv) {
		launch(argv);
	}
}
