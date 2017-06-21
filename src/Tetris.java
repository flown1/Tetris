import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;


import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Tetris extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        StackPane root = new StackPane();
 		Scene scene = new Scene(root, 770, 750);
        primaryStage.setTitle("Tetris");
       
		Board board = new Board(root, primaryStage, scene);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
			@Override 
			public void handle(KeyEvent event){
				switch(event.getCode()){
					case LEFT: board.keyLeftPressed(); break;
					case RIGHT: board.keyRightPressed(); break;
					case SPACE: board.keySpacePressed(); break;
					default: break;
				}
			}
		});
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t){
				Platform.exit();
				System.exit(0);
			}
		});
    }
   	public static void main(String[] args) {
        launch(args);
    }
   	
}