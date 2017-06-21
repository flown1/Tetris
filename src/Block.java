import java.awt.*;

public class Block{					
 
	private int x, y;
	private Color color;
	
	public Block(int myX, int myY, Color myColor){
		super();
		x = myX;
		y = myY;
		color = myColor;
		
		System.out.format("[LOG] Block created at (%d, %d)\n",x,y);
	}
	public int getX(){return this.x;};
	public int getY(){return this.y;};
	public Color getColor(){return this.color;};

	public void moveDown(){this.x = this.x+1;}
	public void moveLeft(){this.y = this.y-1;}
	public void moveRight(){this.y = this.y+1;}
}
