import javafx.application.Platform;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Timer;
import java.util.TimerTask;


public class Board extends Pane{

	private int gridCellHeight = 30,
				gridCellWidth = 30,
				boardHeight = 22,
				boardWidth = 10;
	
	Color normalBlockColor = Color.GRAY;
	private long timeInterval = 500;
	private long delayInterval = 0;
	private String difficulty = "";
	private int pointsMultiplier = 1;
	private boolean isGameEnded = false;
	private boolean isMovingDownActive = false;
	private int playerPoints;
	public static Shape fallingShape;
	
	private User user;
	private Rectangle board[][];
	
	Pane gamePanel = new Pane();
	final String usersDBPath = "users/";
	
	int[][] placedBlocksMap = new int[boardHeight][boardWidth];
	
	public Board(StackPane root, Stage primaryStage, Scene scene){
		
		GridPane gridPane = new GridPane();
		
		Label title = new Label("Tetris");
		title.setFont(new Font("Tahoma", 43));
		
		Label loginLabel = new Label("Please login:");
		loginLabel.setFont(new Font("Tahoma", 13));
		
		TextField loginField = new TextField();
		loginField.setEditable(true);
		
		Label loginStatus = new Label("User status: NOT logged in");
		loginStatus.setFont(new Font("Tahoma", 13));

		Label difficultyLabel = new Label("Set difficulty:");
		difficultyLabel.setFont(new Font("Tahoma", 13));
		difficultyLabel.setDisable(true);
		
        final ComboBox difficultyCombo = new ComboBox();
        difficultyCombo.getItems().addAll(
            "normal",
            "hard",
            "insane"
        );
        difficultyCombo.setDisable(true);
        
        difficultyCombo.getSelectionModel().selectFirst();
		Button startBTN = new Button("Start");
		startBTN.setTranslateX(30);
		startBTN.setDisable(true);

		playerPoints = 0;
		Label pointsLabel = new Label("Points: " + playerPoints);
		pointsLabel.setFont(new Font("Tahoma", 20));
		updatePoints(pointsLabel);
		
		Label gameStatusLabel = new Label();
		gameStatusLabel.setFont(new Font("Tahoma", 20));
	
		Label userHighScoreLabel = new Label();
		userHighScoreLabel.setFont(new Font("Tahoma", 18));
		
		Button loginBTN = new Button("Login");
		
		
		loginBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	String userName = loginField.getText().toString();
            	if(!userName.equals("")){
	            	System.out.println("[LOG] Clicked loginBTN");
	            	
	            	if(!userExists(userName)){
	            		System.out.println("[LOGIN] User "+userName+" doesnt exists. Created a new one");
	            		Platform.runLater(new Runnable() {
			    		    @Override
			    		    public void run() {
			    		    	loginStatus.setText(userName + " not found. Created profile");
			    		    }
			    		});
	            		createUser(userName, 0);
	            	}else{
	            		System.out.println("[LOGIN] Found user "+userName+".");
	            		loadUserInfo(userName);
	            		
	            		Platform.runLater(new Runnable() {
	            		    @Override
	            		    public void run() {
	            		    	loginStatus.setText("Welcome back, "+userName+"!");
	            		    	loginStatus.setTextFill(Color.GREEN);
	            		    }
	            		});
	            	}
	            	Platform.runLater(new Runnable() {
            		    @Override
            		    public void run() {
            		    	userHighScoreLabel.setText("Highscore: " + user.getScore());
            		    }
            		});
	            	loginBTN.setDisable(true);
	            	loginField.setDisable(true);
	            	startBTN.setDisable(false);
	            	difficultyCombo.setDisable(false);
	            	difficultyLabel.setDisable(false);
            	}
            }
	    });
		startBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

            	String diffLevel = difficultyCombo.getSelectionModel().getSelectedItem().toString();
				setDiffLevel(diffLevel);
				
				isGameEnded = true;
				initBoard(root, primaryStage, scene);
				gridPane.getChildren().add(pointsLabel);
				
				final Timer timer = new Timer();
				final TimerTask MoveLoop = new TimerTask(){
			    	Shape shape = new Shape();
			    	
				    public void run(){
				    	
				        if(isMovingDownActive){  
				        	fallingShape = shape;			
				        	putShape(shape);
				        	if(!checkEnd()){
					        	
					        	shapeMoveDown(shape);
					        	System.out.println("[LOG] Top Left Corner of Shape is: row: "+ shape.getTopLeftCornerX() + " col: "+ shape.getTopLeftCornerY());
					        	System.out.println("- - - - - - END OF ITERATION - - - - - -");
					        }else{
					        	System.out.println("[LOG] GAME OVER !!");
					        	Platform.runLater(new Runnable() {
					    		    @Override
					    		    public void run() {
					    		    	gameStatusLabel.setText("GAMEOVER!");
					    		    }
					        	});
					        	timer.cancel();
					        	isGameEnded = true;
					        	isMovingDownActive = false;
					        	shape.deactivateShape();
					        	
					        	updateUserInfo();
				        	}	
				        }else{	
				        	shape = new Shape();
				        	isMovingDownActive = true;

				        	int pointsToAdd = checkLines();
				        	playerPoints += (pointsToAdd * pointsMultiplier);
				        	updatePoints(pointsLabel);
				        }
				    }
			    };
			    timer.scheduleAtFixedRate(MoveLoop, delayInterval, timeInterval);
			    startBTN.setDisable(true);
			    difficultyCombo.setDisable(true);
            }
	    });
		
		gridPane.setPadding(new Insets(20,0,0,350));
		
		gridPane.setConstraints(title,0,0);
		gridPane.getChildren().add(title);
		
		gridPane.setConstraints(loginLabel,0,1);
		gridPane.getChildren().add(loginLabel);
		
		gridPane.setConstraints(loginField,1,1);
		gridPane.getChildren().add(loginField);
		
		gridPane.setConstraints(loginBTN,2,1);
		gridPane.getChildren().add(loginBTN);
		
		gridPane.setConstraints(loginStatus,1,2);
		gridPane.getChildren().add(loginStatus);
		
		gridPane.setConstraints(difficultyLabel,0,3);
		gridPane.getChildren().add(difficultyLabel);
		
		gridPane.setConstraints(difficultyCombo, 1,3);
		gridPane.getChildren().add(difficultyCombo);
		
		gridPane.setConstraints(startBTN, 0,4);
		gridPane.getChildren().add(startBTN);
		
		gridPane.setConstraints(userHighScoreLabel, 0, 5);
		gridPane.getChildren().add(userHighScoreLabel);
		
		gridPane.setConstraints(pointsLabel, 0,6);

		gridPane.setConstraints(gameStatusLabel,0,7);
		gridPane.getChildren().add(gameStatusLabel);
		
		root.getChildren().add(gridPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	};
	public void initBoard(StackPane root, Stage primaryStage, Scene scene){
		
		System.out.println("[LOG] Initialising board");
		
		GridPane gameGrid = new GridPane();
		gameGrid.setPadding(new Insets(10, 0, 10, 0));
		gameGrid.setVgap(1);
		gameGrid.setHgap(1);
		gameGrid.setMaxHeight(800);
		gameGrid.setMinWidth(300);

		board = new Rectangle[boardHeight][boardWidth];
        
		for(int i = 0; i < boardHeight; i++)
			for(int j = 0; j < boardWidth; j++){
				Rectangle rec = new Rectangle();
				rec.setWidth(gridCellWidth);
				rec.setHeight(gridCellHeight);
				rec.setFill(normalBlockColor);
				GridPane.setConstraints(rec, j, i);
				gameGrid.getChildren().add(rec);
				board[i][j] = rec;
				placedBlocksMap[i][j] = 0;
			}
		root.getChildren().add(gameGrid);
		primaryStage.setScene(scene);
        primaryStage.show();
	}
	public void emptyBoard(StackPane root, Stage primaryStage, Scene scene){
		System.out.println("[LOG] Emptying board");
		for(int i = 0; i<boardHeight; i++){
			for(int j = 0; j<boardWidth; j++){
				board[i][j].setFill(normalBlockColor);
				gamePanel.getChildren().add(board[i][j]);
			}
		}
		root.getChildren().add(gamePanel);
		primaryStage.setScene(scene);
        primaryStage.show();
	}
	public void setDiffLevel(String diffLevel){
		difficulty = diffLevel;
		if(diffLevel.equals("normal")){
			timeInterval = 1000/1;
			pointsMultiplier = 1;
		}else if(diffLevel.equals("hard")){
			timeInterval = 1000/6;
			pointsMultiplier = 6;
		}else if(diffLevel.equals("insane")){
			timeInterval = 1000/12;
			pointsMultiplier = 12;
		}else{
			timeInterval = 4000;
			pointsMultiplier = 99;
		}
		System.out.println("[LOG] Set game difficulty level to: "+ diffLevel);
	}
	public void createUser(String name, int score){
		
		user = new User(name, score);
		String usersCatalogue = usersDBPath+name+".bin";
		
		try{
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(usersCatalogue));
			output.writeObject(user);
		}catch(FileNotFoundException err){
			System.out.println(err);
		}catch(IOException err){
			System.out.println(err);
		}
	}
	public boolean userExists(String userName){
		
		File usersDBFolder = new File(usersDBPath);

		for(File file : usersDBFolder.listFiles()){
			if(file.isFile())
				if(file.getName().equals(userName + ".bin"))
					return true;
		}
		return false;
	}
	public void loadUserInfo(String name){
		
		File usersDBFolder = new File(usersDBPath);
		for(File file : usersDBFolder.listFiles()){
			if(file.isFile()){
				String filePath = usersDBPath + file.getName();
				
				try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath))){
					User tmpUser = (User)input.readObject();
					if(tmpUser.getName().equals(name))
						user = tmpUser;
				}catch(FileNotFoundException err){
					System.out.println("[BLAD] Nieodnaleziono pliku z baza danych Kont Uzytkownikow!");
				}catch(IOException err){
					System.out.println("[BLAD] Nieprawidlowy odczyt z pliku!");
				}catch(ClassNotFoundException err){
					System.out.println("[BLAD] Brak wpisów do odczytu!");
				}
				
			}
		}
		
	}
	public void updateUserInfo(){
		if(user.getScore() < playerPoints){
			User tmp = user;
			deleteUser(user.getName());
			System.out.println(tmp.getName()+" "+tmp.getScore());
			createUser(tmp.getName(), playerPoints);

			System.out.println("[LOG] User highscore updated. New personal highscore: "+user.getScore());
		}
	}
	public void deleteUser(String userName){
		
		File clientsDBFolder = new File(usersDBPath);
		for(File file : clientsDBFolder.listFiles()){
			if(file.isFile()){
				if(file.getName().equals(userName + ".bin")){
					System.out.println("JUST DELETED"+ file.getName());
					file.delete();
				}
			}
		}
	}
 	public boolean checkEnd(){
		System.out.println("[LOG] Checking for end of game");
		
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(fallingShape.shapeMap[i][j] == 1){
					
					Block block = fallingShape.blocksMap[i][j];
					int currX = block.getX(),
						currY = block.getY();
					
					if(placedBlocksMap[currX][currY] == 1){
						return true;
					}
				}
		return false;
	}
	public void clearLine(int row){
		for(int i = row; i > 1; i--){
			for(int j = 0; j < boardWidth; j++){
				Color upperColor = (Color)board[i-1][j].getFill();
				board[i][j].setFill(upperColor);
				
				placedBlocksMap[i][j] = placedBlocksMap[i-1][j];
			}
		}
		for(int j = 0; j < boardWidth; j++){					//ustawianie pol dla wiersza nr 0
			board[0][j].setFill(normalBlockColor);
			placedBlocksMap[0][j] = 0;
		}
	}
	public int checkLines(){
		System.out.println("[LOG] Checking lines for points");
		
		int howManyLines = 0;
		boolean blocksRow = true;
		
		for(int i = 0; i < boardHeight; i ++){
			blocksRow = true;
			
			for(int j = 0; j < boardWidth; j++){
				if(placedBlocksMap[i][j] == 0)
					blocksRow = false;
			}
			if(blocksRow == true){
				howManyLines += 1;
				clearLine(i);
			}
		}
		System.out.println("[LOG] Detected lines:" + howManyLines);

		return howManyLines;
	}
	public void updatePoints(Label pointsLabel){
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	System.out.println("[UPDATE] Points label");
				String val = Integer.toString(playerPoints);
				pointsLabel.setText("Points: " + val);
		    }
		});
			
	}
	public void putBlock(Block block){
		int x = block.getX(),
			y = block.getY();
		Color color = Color.rgb(block.getColor().getRed(), block.getColor().getGreen(), block.getColor().getBlue());
		if(x >= 0)
			board[x][y].setFill(color);
	}
	public void putShape(Shape shape){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(shape.shapeMap[i][j] == 1)
					putBlock(shape.blocksMap[i][j]);
			}
		}
	}
	public void shapeMoveDown(Shape shape){
		
		if(collisionWithBottomDetected(shape) || collisionWithAnotherBlockDetected(shape)){
			stopShape(shape);
		}else{
			clearShape(shape);
			shape.decreaseTopLeftCornerX();
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if(shape.shapeMap[i][j] == 1){
						
						Block block = shape.blocksMap[i][j];
						block.moveDown();
						putBlock(block);
					}
				}
			}
			putShape(shape);
		}
	}
	public boolean collisionWithBottomDetected(Shape shape){
		for(int i = 0; i<4; i++){
			for(int j = 0; j < 4; j++){
				if(shape.shapeMap[i][j] == 1){
					
					Block block = shape.blocksMap[i][j];
					
					int currX = block.getX();
					if(currX + 1 == boardHeight){
						System.out.println("[LOG] Collison with bottom detected");
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean collisionWithAnotherBlockDetected(Shape shape){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(shape.shapeMap[i][j] == 1 ){

					Block block = shape.blocksMap[i][j];
					
					int currX = block.getX(),
						currY = block.getY();
					
					if(currX < boardHeight-1)
						if(placedBlocksMap[currX+1][currY] == 1){
							System.out.println("[LOG] Collison with OTHER BLOCK at bottom detected");
							return true;
						}
				}
			}
		}
		return false;
	}
	public boolean collisionWithAnotherBlockLeftSideDetected(Shape shape){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(shape.shapeMap[i][j] == 1 ){

					Block block = shape.blocksMap[i][j];
					int currX = block.getX(),
						currY = block.getY();
					
					if(placedBlocksMap[currX][currY-1] == 1){
						System.out.println("[LOG] Collison with OTHER BLOCK at LEFT detected");
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean collisionWithAnotherBlockRightSideDetected(Shape shape){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(shape.shapeMap[i][j] == 1 ){

					Block block = shape.blocksMap[i][j];
					
					int currX = block.getX(),
						currY = block.getY();
					
					if(placedBlocksMap[currX][currY+1] == 1){
						System.out.println("[LOG] Collison with OTHER BLOCK at RIGHT detected");
						return true;
					}
				}
			}
		}
		return false;
	}
	public void moveLeft(Shape shape){
		
		try{
				if(shape.getIsActive() == true){
					int nearestToLeftBorder = boardWidth,
						currX = 0;
					
					for(int i = 0; i < 4; i++){
						for(int j = 0; j < 4; j++){
							if(shape.shapeMap[i][j] == 1){
								Block block = shape.blocksMap[i][j];
								if(block.getY() < nearestToLeftBorder){
									nearestToLeftBorder = block.getY();
									currX = block.getX();
								}
							}
						}
					}
					
					if(nearestToLeftBorder > 0){
						if(!collisionWithAnotherBlockLeftSideDetected(shape)){
							clearShape(shape);
							shape.moveLeft();
							putShape(shape);
						}else
							System.out.println("[LOG] Collision with OTHER BLOCK on left detected");
					}else
						System.out.println("[LOG] Collision with LEFT BORDER detected");			
				}else{
					System.out.println("[LOG] Cannot Move Left - shape deactivated");
				}
		}catch(NullPointerException err){
			System.out.println("[LOG] First choose difficulty!");
		}
	}
	public void moveRight(Shape shape){
	
		try{
			if(shape.getIsActive() == true){
				int nearestToRightBorder = 0,
					currX = 0;
				
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						if(shape.shapeMap[i][j] == 1){
							Block block = shape.blocksMap[i][j];
							if(block.getY() > nearestToRightBorder){
								nearestToRightBorder = block.getY();
								currX = block.getX();
							}
						}
	
				if(nearestToRightBorder < boardWidth - 1){
					if(!collisionWithAnotherBlockRightSideDetected(shape)){
						clearShape(shape);
						shape.moveRight();
						putShape(shape);
					}else
						System.out.println("[LOG] Collision with OTHER BLOCK on right detected");
				}else
					System.out.println("[LOG] Collision with right border detected");
			}else{
				System.out.println("[LOG] Cannot Move Right - shape deactivated");
			}
		}catch(NullPointerException err){
			System.out.println("[LOG] First choose difficulty!");
		}
	}
	public void clearShape(Shape shape){
		
		try{
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if(shape.shapeMap[i][j] == 1){
						Block block = shape.blocksMap[i][j];
						int currX = block.getX(),
							currY = block.getY();			
						board[currX][currY].setFill(normalBlockColor);
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException err){
			System.out.println("[EXCEPTION CAUGHT] "+err);
		}
	}
	public void stopShape(Shape shape){
		
		shape.deactivateShape();
		isMovingDownActive = false;
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){	
				if(shape.shapeMap[i][j] == 1){
					Block block = shape.blocksMap[i][j];
					
					int currX = block.getX(),
						currY = block.getY();
					
					placedBlocksMap[currX][currY] = 1;
				}
			}	
		}
		printPlacedBlocksMap();			
	}
	public void printPlacedBlocksMap(){
		for(int i = 0; i < boardHeight; i++){
			for(int j = 0; j < boardWidth; j++){
				System.out.print(placedBlocksMap[i][j] + " ");
			}
			System.out.println("");
		}
	}
	public void rotateShape(Shape fallingShape){
		
		if(checkSpaceForRotation(fallingShape)){ 
			clearShape(fallingShape);
			fallingShape.changeRotation();
			System.out.println("[LOG] ShapeMap after rotation:");
			fallingShape.shapeMapPrint();
			putShape(fallingShape);
		}else
			System.out.println("[LOG] No space for rotations");
		}
	public boolean checkSpaceForRotation(Shape shape){

		if(shape.getNextRotationShapeWidth()+shape.getTopLeftCornerY() > boardWidth){	 //czy Y?
			System.out.println("[LOG] NOT Enough space for rotating [board edge]");
			return false;
		}
		if(shape.getNextRotationShapeHeight()+shape.getTopLeftCornerX() >= boardHeight){	 
			System.out.println("[LOG] NOT Enough space for rotating [board bottom edge]");
			return false;
		}
		
		Shape tmp = new Shape(shape);
		
		tmp.changeRotation();
		
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4; j++){
				if(tmp.shapeMap[i][j] == 1){
					Block block = tmp.blocksMap[i][j];
					
					int currX = block.getX(),
						currY = block.getY();
					try{
						if(placedBlocksMap[currX][currY] == 1){
							System.out.println("[LOG] Collision with other blocks detected");
							return false;
						}
					}catch(ArrayIndexOutOfBoundsException err){
						System.out.println("[Exception] "+err);
					}
				}
			}
		return true;	
	}
	public void keyLeftPressed(){
		System.out.println("[LOG] KEY_LEFT pressed");
		moveLeft(fallingShape);
	}
	public void keyRightPressed(){
		System.out.println("[LOG] KEY_RIGHT pressed");
		moveRight(fallingShape);
	}
	public void keySpacePressed(){
		System.out.println("[LOG] KEY_SPACE pressed");
		rotateShape(fallingShape);
	}

}