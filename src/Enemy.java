import java.util.*;
public class Enemy {

	public int id;
	public String type;
	public int health;
	public int attackDMG;
	public int armor;
	public boolean turn;
	
	public Enemy(int id, int i) {
		this.turn = true;
		this.id = id;
		if(i == 0) { // Skeleton
			this.type = "Skeleton";
			this.health = 7;
			this.attackDMG = 4;
			this.armor = 1;
		}else if(i == 1) { // Goblin
			this.type = "Goblin";
			this.health = 9;
			this.attackDMG = 4;
			this.armor = 1;
		}else if(i == 2) { // Orc
			this.type = "Orc";
			this.health = 13;
			this.attackDMG = 4;
			this.armor = 1;
		}else if(i == 3) { // Necromancer
			this.type = "Necromancer";
			this.health = 18;
			this.attackDMG = 4;
			this.armor = 1;
		}else if(i == 4) { // Black Knight
			this.type = "Black Knight";
			this.health = 25;
			this.attackDMG = 10;
			this.armor = 4;
		}
	}
	public void attack() {
		
	}
	public String showStatus() {
		String s = "";
		s += "Showing Status for: "+" <"+this.type+"> ID: "+this.id+"\n";
		s += "HEALTH: "+this.health+"\n";
		s += "ATTACK DMG:"+this.attackDMG+"\n";
		s += "ARMOR: "+this.armor+"\n";
		return s;
	}
}
