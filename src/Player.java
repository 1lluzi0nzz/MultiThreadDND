import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable{

	public String name = "";
	public String type = "";
	public int health = 10;
	public int attackDMG = 10;
	public int armor = 10;
	public int gold = 0;
	public boolean turn = true;
	public ArrayList<String> items = new ArrayList<String>();
	
	public Player(String name, int i) {
		this.name = name;
		if(i == 1) {
			this.type = "Warrior";
			this.health = 10;
			this.attackDMG = 4;
			this.armor = 3;
			items.add("Shield");
		}else if(i == 2) {
			this.type = "Wizard";
			this.health = 7;
			this.attackDMG = 8;
			this.armor = 0;
		}else if(i == 3) {
			this.type = "Paladin";
			this.health = 14;
			this.attackDMG = 2;
			this.armor = 5;
		}
				
	}
	public boolean hasShield() {
		if(items.contains("Shield")) {
			return true;
		}else {
			return false;
		}
		
	}
	public boolean hasFireball() {
		if(items.contains("Fireball")) {
			return true;
		}else {
			return false;
		}
		
	}
	public boolean hasPotion() {
		if(items.contains("Potion")) {
			return true;
		}else {
			return false;
		}
		
	}
	public String showStatus() {
		String s = "";
		s += "Showing Status for: "+this.name+" <"+this.type+">\n";
		s += "HEALTH: "+this.health+"\n";
		s += "ATTACK DMG:"+this.attackDMG+"\n";
		s += "ARMOR: "+this.armor+"\n";
		s += "GOLD: "+this.gold+"\n---INVENTORY---";
		for(int i = 0; i < items.size(); i++) {
			s += "\n - "+items.get(i);
		}
		return s;
	}
	public int attack(Enemy e) {
		int dmg = this.attackDMG - e.armor;
		if(e.armor >= this.attackDMG) {
			dmg = 0;
		}
		return dmg;
	}
	public void useItem(String s) {
		
	}
}
