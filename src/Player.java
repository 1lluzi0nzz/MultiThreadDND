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
			this.type = "warrior";
			this.health = 10;
			this.attackDMG = 4;
			this.armor = 3;
		}else if(i == 2) {
			this.type = "wizard";
			this.health = 7;
			this.attackDMG = 8;
			this.armor = 0;
		}else if(i == 3) {
			this.type = "paladin";
			this.health = 14;
			this.attackDMG = 2;
			this.armor = 5;
		}
				
	}
	public boolean hasShield() {
		if(items.contains("Shield")) {
			this.armor = this.armor + Items.shieldArmor;
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
	public boolean canAttack(ArrayList<Enemy> e) {
		boolean b = false;
		if(turn) {
			if(!e.isEmpty()) {
				return true;
			}
		}
		return b;
	}
	public void attack(Enemy e) {

	}
	public void useItem(String s) {
		
	}
}
