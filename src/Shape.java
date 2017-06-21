import java.awt.Color;
import java.util.Random;

public class Shape {
	
	String[] shapeTypes = {"T-shape","L-shape","I-shape","Z-shape", "O-shape"};
	
	public int[][] shapeMap = 	{{0, 0, 0, 0},
								{0, 0, 0, 0},
								{0, 0, 0, 0},
								{0, 0, 0, 0}};
	public Block[][] blocksMap = 	{{null, null, null, null},
									 {null, null, null, null},
									 {null, null, null, null},
									 {null, null, null, null}};
	public int[][][] TshapeRotations = {
									   {{1,1,1,0},
										{0,1,0,0},
										{0,0,0,0},
										{0,0,0,0}},
										
									   {{1,0,0,0},
										{1,1,0,0},
										{1,0,0,0},
										{0,0,0,0}},
									   
									   {{0,1,0,0},
										{1,1,1,0},
										{0,0,0,0},
										{0,0,0,0}},
									   
									   {{0,1,0,0},
										{1,1,0,0},
										{0,1,0,0},
										{0,0,0,0}}
									   };
	public int[][][] LshapeRotations = {
									   {{1,1,1,0},
										{1,0,0,0},
										{0,0,0,0},
										{0,0,0,0}},
										
									   {{1,1,0,0},
										{0,1,0,0},
										{0,1,0,0},
										{0,0,0,0}},
									   
									   {{0,0,1,0},
										{1,1,1,0},
										{0,0,0,0},
										{0,0,0,0}},
									   
									   {{1,0,0,0},
										{1,0,0,0},
										{1,1,0,0},
										{0,0,0,0}}
									   };
	public int[][][] IshapeRotations = {
									   {{1,1,1,1},
										{0,0,0,0},
										{0,0,0,0},
										{0,0,0,0}},
										
									   {{1,0,0,0},
										{1,0,0,0},
										{1,0,0,0},
										{1,0,0,0}}
									   };
	public int[][][] ZshapeRotations = {
									   {{0,1,1,0},
										{1,1,0,0},
										{0,0,0,0},
										{0,0,0,0}},
										
									   {{1,0,0,0},
										{1,1,0,0},
										{0,1,0,0},
										{0,0,0,0}}
									   };
	public int[][][] OshapeRotations = {
									   {{1,1,0,0},
										{1,1,0,0},
										{0,0,0,0},
										{0,0,0,0}}
									   };

	private boolean isActive = false;
	private int currRotationNum, 
				rotationsQuantity;
	private String shapeName;
	private Color shapeColor;
	private int topLeftCornerX, topLeftCornerY; 
	
	public Shape(){
		
		shapeName  = randShape();
		System.out.println("[LOG] Randomized " + shapeName);
		this.currRotationNum = 0;
		System.out.println("[LOG] START rotation: "+this.currRotationNum);
		
		if(shapeName == "T-shape"){
			shapeMap = TshapeRotations[currRotationNum];
	
			this.rotationsQuantity = 3; 
		}else if(shapeName == "L-shape"){
			shapeMap = LshapeRotations[currRotationNum];
			this.rotationsQuantity = 3; 
		}else if(shapeName == "I-shape"){
			shapeMap = IshapeRotations[currRotationNum];
			this.rotationsQuantity = 1; 
		}else if(shapeName == "Z-shape"){			
			shapeMap = ZshapeRotations[currRotationNum];
			this.rotationsQuantity = 1; 
		}else if(shapeName == "O-shape"){
			shapeMap = OshapeRotations[currRotationNum];
			this.rotationsQuantity = 0;
		}
		
		System.out.println("Map 0 of the shape: ");
		activateShape();
		this.shapeMapPrint();
		
		initBlockMap();
	}
	public Shape(Shape original){
		this.shapeColor = original.shapeColor;
		this.shapeName = original.shapeName;
		this.shapeMap = original.shapeMap;
		this.isActive = original.isActive; //???
		this.rotationsQuantity = original.rotationsQuantity;
		this.currRotationNum = original.currRotationNum;
		this.topLeftCornerX = original.topLeftCornerX;
		this.topLeftCornerY = original.topLeftCornerY;
	}
	public void initBlockMap(){
		
		float red = (float)Math.random(),
			  green = (float)Math.random(),
			  blue = (float)Math.random();
		
		shapeColor = new Color(red,green,blue);
		
		boolean gotCornerXY = false;
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				
				if(shapeMap[i][j] == 1){
					blocksMap[i][j] = new Block(i , 4+j, shapeColor);
					
					if(gotCornerXY == false){
						topLeftCornerX = blocksMap[i][j].getX();
						topLeftCornerY = blocksMap[i][j].getY();
						gotCornerXY = true;
					}
				}	
			}
		}	
	}
	public void activateShape(){
		this.isActive = true;
		System.out.println("[LOG] Shape activated");
	}
	public void deactivateShape(){
		this.isActive = false;
		System.out.println("[LOG] Shape deactivated");
	}
	public boolean getIsActive(){return this.isActive;}
	public void shapeMapPrint(){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				System.out.print(shapeMap[i][j] + " ");	
			}
			System.out.println("\n");
		}
	}
	public int getTopLeftCornerX(){return this.topLeftCornerX;};
	public int getTopLeftCornerY(){return this.topLeftCornerY;};
	public void decreaseTopLeftCornerX(){this.topLeftCornerX += 1;};
	public void moveLeft(){
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(this.shapeMap[i][j] == 1){
					Block block = this.blocksMap[i][j];
					
					block.moveLeft();
				}
			}
		}
		topLeftCornerY -= 1;
	}
	public void moveRight(){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(this.shapeMap[i][j] == 1){
					Block block = this.blocksMap[i][j];
					
					block.moveRight();
				}
			}
		}
		topLeftCornerY += 1;
	}
	public String randShape(){
		Random rand = new Random();	
		return shapeTypes[rand.nextInt(shapeTypes.length) + 0];
	}
	public void changeRotation(){
		
		if(currRotationNum >=  this.rotationsQuantity)
			currRotationNum = 0;
		else
			currRotationNum++;
  		
		if(shapeName == "T-shape"){
			this.shapeMap = TshapeRotations[currRotationNum];
		}else if(shapeName == "L-shape"){
			this.shapeMap = LshapeRotations[currRotationNum];
		}else if(shapeName == "I-shape"){
			this.shapeMap = IshapeRotations[currRotationNum];
		}else if(shapeName == "Z-shape"){			
			this.shapeMap = ZshapeRotations[currRotationNum];
		}
		
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				if(shapeMap[i][j] == 1)
					blocksMap[i][j] = new Block(topLeftCornerX+i, topLeftCornerY+j, shapeColor);

		System.out.println("[LOG] Rotated " + shapeName +". Rotation num: "+currRotationNum);
	}
	public int getNextRotationShapeWidth(){
		
		int[][] rotation = {{0,0,0,0},
							{0,0,0,0},
							{0,0,0,0},
							{0,0,0,0}};
		int currRot = currRotationNum;
		if(currRot != this.rotationsQuantity)
			currRot++;
		else
			currRot = 0;
		
		if(shapeName == "T-shape"){
			rotation = TshapeRotations[currRot];
		}else if(shapeName == "L-shape"){
			rotation = LshapeRotations[currRot];
		}else if(shapeName == "I-shape"){
			rotation = IshapeRotations[currRot];
		}else if(shapeName == "Z-shape"){			
			rotation = ZshapeRotations[currRot];
		}
		
		int width = 0;
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(rotation[i][j] == 1 && j > width)
					width = j;
				
			}
		}
		width++;
		
		//System.out.println("[LOG] Width of shape is in next rotation is: "+width);
		//System.out.println("\t It\'s shape is: ");
		//this.shapeMapPrint();
		return width;
	
	}
	public int getNextRotationShapeHeight(){
		
		int[][] rotation = {{0,0,0,0},
							{0,0,0,0},
							{0,0,0,0},
							{0,0,0,0}};
		int currRot = currRotationNum;
		currRot++;
		if(currRot > this.rotationsQuantity)
			currRot = 0;
		
		if(shapeName == "T-shape"){
			rotation = TshapeRotations[currRot];
		}else if(shapeName == "L-shape"){
			rotation = LshapeRotations[currRot];
		}else if(shapeName == "I-shape"){
			rotation = IshapeRotations[currRot];
		}else if(shapeName == "Z-shape"){			
			rotation = ZshapeRotations[currRot];
		}
		
		int height = 0;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(rotation[i][j] == 1 && i > height)
					height = i;
			}
		}
		height++;
		
		//System.out.println("[LOG] Height of shape is in next rotation is: "+height);
		//System.out.println("\t It\'s shape is: ");
		//this.shapeMapPrint();
		return height;
	
	}
}