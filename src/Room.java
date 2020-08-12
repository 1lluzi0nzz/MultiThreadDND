import java.util.*;
public class Room {
	
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Enemy> itemsInRoom = new ArrayList<Enemy>();

	public Room() {
		createEnemies();
		createItems();
	}
	public boolean roomIsCleared() {
		if(enemies.isEmpty()) {
			//Add Items to the players' inventory
			//Add Gold to the players' inventory
			return true;
		}else {
			return false;
		}
	}
	public void removeDeadEnemies() {
		for(int i = 0; i < enemies.size(); i++) {
			if(enemies.get(i).health <= 0) {
				enemies.remove(i);
				i--;
			}
		}
	}
	public void createEnemies() {
		Random r2 = new Random();
		int size = r2.nextInt(4) + 1;
		System.out.println("Enemies in room: "+size);
		Random r = new Random();
		for(int i = 0; i < size; i++) {
			int c = r.nextInt(100);
			if(c < 35) {
				enemies.add(new Enemy(i, 0));
			}else if(c >= 35 && c < 60) {
				enemies.add(new Enemy(i, 1));
			}else if(c >= 60 && c < 75) {
				enemies.add(new Enemy(i, 2));
			}else if(c >= 75 && c < 90) {
				enemies.add(new Enemy(i, 3));
			}else if(c >= 90) {
				enemies.add(new Enemy(i, 4));
			}
		}
	}
	public String displayEnemiesInRoom() {
		String s = "";
		for(int i = 0; i < enemies.size(); i++) {
			s += enemies.get(i).showStatus();
		}
		return s;
	}
	public void createItems() {
		
	}
}
