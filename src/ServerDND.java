import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerDND {
	private static int uniqueId;
	private ArrayList<ClientThread> al;
	private SimpleDateFormat sdf;
	private int port;
	private boolean keepGoing;
	private String notif = " *** ";
	
	public static Map m;
	
	public ServerDND(int port) {
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(keepGoing) {
				System.out.println("Server waiting for Clients on port " + port + ".");
				Socket socket = serverSocket.accept();
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);
				al.add(t);
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}catch(IOException ioE) {}
				}
			}
			catch(Exception e) {
				System.out.println("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
		}
	}
	
	// to stop the server
	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}

	private synchronized boolean broadcast(String message) {
		String time = sdf.format(new Date());
		String[] w = message.split(" ",3);
		
		boolean isPrivate = false;
		//if(w[1].charAt(0)=='@') 
		//	isPrivate=true;
		
		if(isPrivate==true){
			String tocheck=w[1].substring(1, w[1].length());
			message=w[0]+w[2];
			String messageLf = time + " " + message + "\n";
			boolean found=false;
			for(int y=al.size(); --y>=0;){
				ClientThread ct1=al.get(y);
				String check=ct1.getUsername();
				if(check.equals(tocheck)){
					if(!ct1.writeMsg(messageLf)) {
						al.remove(y);
						System.out.println("Disconnected Client " + ct1.username + " removed from list.");
					}
					found = true;
					break;
				}

			}
			if(found!=true){
				return false; 
			}
		}else{
			String messageLf = time + " " + message + "\n";
			System.out.print(messageLf);
			for(int i = al.size(); --i >= 0;){
				ClientThread ct = al.get(i);
				if(!ct.writeMsg(messageLf)){
					al.remove(i);
					System.out.println("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}
		return true;
		
		
	}

	synchronized void remove(int id) {
		
		String disconnectedClient = "";
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if(ct.id == id) {
				disconnectedClient = ct.getUsername();
				al.remove(i);
				break;
			}
		}
		broadcast(notif + disconnectedClient + " has left the party" + notif);
	}
	
	public static void main(String[] args) {
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		ServerDND server = new ServerDND(portNumber);
		m = new Map(1);
		m.rooms.get(m.currentIndex).displayEnemiesInRoom();
		server.start();
	}

	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		Player p;
		String cm;
		String date;

		ClientThread(Socket socket) {
			id = ++uniqueId;
			this.socket = socket;
			System.out.println("Thread trying to create Object Input/Output Streams");
			
			try{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				p = (Player) sInput.readObject();
				username = p.name;
				System.out.println("\n"+username);
				broadcast(notif + username + " <"+p.type+"> has joined the party." + notif);
			}catch (IOException e) {
				System.out.println("Exception creating new Input/output Streams: " + e);
				return;
			}catch (ClassNotFoundException e) {}
            date = new Date().toString() + "\n";
		}
		
		public void run() {
			boolean keepGoing = true;
			while(keepGoing) {
				try {
					//Check if everyones turn is over for enemys
					
					if(checkPartiesTurnOver()) {
						doEnemyAttack();
						setPartiesTurn();
						continue;
					}else
						checkDeaths();
						checkEnemyDeaths();
						broadcast(printEnemiesInRoom());
					cm = (String) sInput.readObject();

					//Commands Logic Here
					if(cm.toLowerCase().startsWith("list party")) {
						for(int i = 0; i < al.size(); ++i) {
							ClientThread ct = al.get(i);
							System.out.println();
							writeMsg((i+1) + ") " + ct.username +" <"+ct.p.type+">");
						}
					}else if(cm.toLowerCase().startsWith("turn")) {
						for(int i = 0; i < al.size(); ++i) {
							ClientThread ct = al.get(i);
							System.out.println();
							if(ct.p.turn) {
								writeMsg((i+1) + ") " + ct.username +" <"+ct.p.type+"> NEEDS TO TAKE THEIR TURN");
							}
						}
					}else if(cm.toLowerCase().startsWith("status")) {
						writeMsg(p.showStatus());
					}else if(cm.toLowerCase().startsWith("enemies")) {
						writeMsg(ServerDND.m.rooms.get(m.currentIndex).displayEnemiesInRoom());
					}else if(cm.toLowerCase().startsWith("end")) {
						broadcast(p.name + " ended their turn");
						p.turn = false;
					}else if(cm.toLowerCase().equals("died")) {
						broadcast(username + " has died.");
					}else if(cm.toLowerCase().startsWith("attack")) {
						if(canAttack(m.rooms.get(m.currentIndex).enemies)) {
							Enemy target = m.rooms.get(m.currentIndex).enemies.get(Integer.parseInt(""+cm.charAt(7)));
							int dmgTaken = p.attack(target);
							m.rooms.get(m.currentIndex).enemies.get(Integer.parseInt(""+cm.charAt(7))).health -= dmgTaken;
							broadcast(p.name + " attacked "+target.type+" "+target.id+ " for " + dmgTaken);
							p.turn = false;
						}
					}
					
					else {
						broadcast(username + ": " + cm);
					}
				}catch (IOException e) {
					System.out.println(username + " Exception reading Streams: " + e);
					break;				
				}catch(ClassNotFoundException e2) {
					break;
				}
			}
			remove(id);
			close();
		}
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
				if(sInput != null) sInput.close();
				if(socket != null) socket.close();
			}catch (Exception e) {}
		}
		private boolean writeMsg(String msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}catch(IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
		public boolean checkPartiesTurnOver() {
			for(int i = 0; i < al.size(); i++) {
				ClientThread ct = al.get(i);
				if(ct.p.turn == true) {
					return false;
				}
			}
			broadcast("ALL MEMBERS HAVE TAKEN THEIR TURNS");
			return true;
		}
		public void doEnemyAttack() {
			Random r = new Random();
			for(int i = 0; i < m.rooms.get(m.currentIndex).enemies.size(); i++) {
				//Skeleton attacked 1lluzi0nzz for dmgTaken
				Player target = al.get(r.nextInt(al.size())).p;
				int dmgTaken = m.rooms.get(m.currentIndex).enemies.get(i).attack(al.get(r.nextInt(al.size())).p);
				p.health -= m.rooms.get(m.currentIndex).enemies.get(i).attack(target);
				broadcast(m.rooms.get(m.currentIndex).enemies.get(i).type + " attacked "+target.name+ " for " + dmgTaken);
			}
		}
		private ClientThread getRandomPartyMember(){
			Random r = new Random();
			return al.get(r.nextInt(al.size()));
		}
		private void setPartiesTurn() {
			for(int i = 0; i < al.size(); i++) {
					al.get(i).p.turn = true;
			}
			broadcast("ITS THE PARTIES TURN NOW.");
		}
		private void checkDeaths() {
			for(int i = 0; i < al.size(); i++) {
				if(al.get(i).p.health <= 0) {
					broadcast(al.get(i).p.name+" has died");
					remove(id);
				}
			}
		}
		private void checkEnemyDeaths() {
			for(int i = 0; i < m.rooms.get(m.currentIndex).enemies.size(); i++) {
				if(m.rooms.get(m.currentIndex).enemies.get(i).health <= 0) {
					m.rooms.get(m.currentIndex).enemies.remove(m.rooms.get(m.currentIndex).enemies.get(i));
					if(i >= m.rooms.get(m.currentIndex).enemies.size() || i < 0) {
						i--;
					}
				}
			}
		}
		private String printEnemiesInRoom() {
			String s;
			if(m.rooms.get(m.currentIndex).enemies.isEmpty()) {
				s = "\n\nNO ENEMIES FOUND - ROOM CLEARED\n\n";
			}else {
				s = "\n\nENEMIES FOUND\n\n";
				for(int i = 0; i < m.rooms.get(m.currentIndex).enemies.size(); i++) {
					s += i + ") " + m.rooms.get(m.currentIndex).enemies.get(i).type + "\n";
				}
			}
			return s;
		}
		private boolean canAttack(ArrayList<Enemy> e) {
			boolean b = true;
			if(!p.turn) {
				writeMsg("Can't Attack, not your turn!");
				b = false;
			}
			if(p.health <= 0) {
				writeMsg("Can't Attack, You are dead!");
				b = false;
			}
			if(e.isEmpty()) {
				writeMsg("Can't Attack, No enemies to attackd!");
				b = false;
			}
			return b;
		}
	}
}