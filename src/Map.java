import java.util.*;
public class Map {

	public int roomCount;
	public int currentIndex = 0;
	public ArrayList<Room> rooms = new ArrayList<Room>();
	
	public Map(int roomCount) {
		this.roomCount = roomCount;
		for(int i = 0; i < roomCount; i++) {
			rooms.add(new Room());
		}
	}

}
