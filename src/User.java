import java.io.Serializable;

public class User implements Serializable {
	
	//static final long serialVersionUID = 1;
	
	private String name;
	private int score;
	
	public User(String name, int score){
		this.name = name;
		this.score = score;
	}
	public void setScore(int score){
		this.score = score;
	}
	public String getName(){return this.name;}
	public int getScore(){return this.score;}
	
}
