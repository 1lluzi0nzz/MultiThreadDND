import java.net.*;
import java.io.*;
import java.util.*;
public class Client  {
	
	private String notif = " *** ";
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	private String server, username;
	private static Player p;
	private int port;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	//Client(String server, int port, String username) {
	Client(String server, int port, Player p) {
		this.server = server;
		this.port = port;
		this.username = p.name;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		}catch(Exception ec) {
			System.out.println("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		System.out.println(msg);
	
		try{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}catch (IOException eIO) {
			System.out.println("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		new ListenFromServer().start();
		try{
			//sOutput.writeObject(username);
			sOutput.writeObject(p);
		}
		catch (IOException eIO) {
			System.out.println("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		return true;
	}
	void sendMessage(String s) {
		try {
			sOutput.writeObject(s);
		}
		catch(IOException e) {
			System.out.println("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try {
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
			
	}
	//java Client username portNumber serverAddr
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";
		int type = 1;
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Enter Character Name: ");
		userName = scan.nextLine();
		System.out.println("Type '1' to be a warrior");
		System.out.println("Type '2' to be a wizard");
		System.out.println("Type '3' to be a paladin");
		type = Integer.parseInt(scan.nextLine());

		switch(type) {
			case 1:
				p = new Player(userName, 1);
				break;
			case 2:
				p = new Player(userName, 2);
				break;
			case 3:
				p = new Player(userName, 3);
				break;
			default:
				p = new Player(userName, 1);
				break;
		}

		Client client = new Client(serverAddress, portNumber, p);
		if(!client.start())
			return;
		
		System.out.println("Instructions:");
		System.out.println("1. Simply type the message to send broadcast to all active clients");
		System.out.println("2. Type 'LIST PARTY' to see who is in the game");
		System.out.println("3. Type 'STATUS' to show your stats.");
		System.out.println("4. Type 'TURN' to show your parties remaining turns.");
		System.out.println("5. Type 'QUIT' to close the game\n");
		
		while(p.health > 0) {
			System.out.print("> ");
			String msg = scan.nextLine();
			if(msg.equalsIgnoreCase("QUIT")) {
				client.sendMessage("QUIT");
				break;
			}

			else {
				client.sendMessage(msg);
			}
		}
		if(p.health <= 0) {
			System.out.println("You have died");
			client.sendMessage("QUIT");
		}
		scan.close();
		client.disconnect();	
	}

	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					System.out.println(msg);
					System.out.print("> ");
				}
				catch(IOException e) {
					System.out.println(notif + "Server has closed the connection: " + e + notif);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
