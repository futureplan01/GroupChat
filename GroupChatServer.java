import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * I"m going to make it so that my Client Will automatically connect with my Server
 * Should use two serves one for reading and one for righting, the other do not have to wait
 * @author jprayor100
 *
 */

public class GroupChatServer {
	private static ServerSocket listener;
	private static Socket client;
	private static int port = 1738;
	public static ArrayList <Socket> allClients; // Should Store all Sockets in LinkList for faster Iterations
	public static ArrayList <ClientHandler> ClientsInfo;
	private static ClientHandler clientControl;
	
	public static void main (String [] args){
		try {
			System.out.println("Running...");
			listener = new ServerSocket(port);
			System.out.println("Listening...");
		} catch (IOException e) {
			System.out.println("Could not make server");
			System.exit (0);
		}
		//Open an Array List so that I could store all the connections
		allClients = new ArrayList<Socket> ();
		ClientsInfo = new ArrayList <ClientHandler> ();
		
		while (true){
			try {
				client = listener.accept();
				allClients.add(client);
			} catch (IOException e) {
				System.out.println("Could not make a client");
			}
			clientControl = new ClientHandler (client, allClients);
			// Update the dataBase for clients...
			ClientsInfo.add(clientControl);
			clientControl.updateClients(ClientsInfo);
		
		}
	}

}
/**
 * This class handles the connection between the client and the server
 * @author jprayor100
 *
 */
class ClientHandler extends Thread{
	Socket client;
	BufferedReader in;
	ArrayList <Socket> allClients;
	Writing sendText;
	
	public ClientHandler (Socket newClient, ArrayList <Socket> allConnections){
		client = newClient;
		allClients = allConnections;
		start();
	}
	
	public synchronized void openStreams (){
		try {
			in = new BufferedReader (new InputStreamReader (client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Let's the other users know that a new Client has entered the GroupChat...
	 * Let me see this in A Gui first....
	 */
	public synchronized void updateClients (ArrayList <ClientHandler> newComers){
		int i = 0;
		while (i < newComers.size()){
			newComers.get(i).allClients = allClients;
			i++;
		}
		// then Update the Gui Once I figure out what the Gui Looks like...
		
	}
	/**
	 * All threads will be constantly reading waiting for the client to write something
	 * 
	 * All connections are stored in the ArrayList..., First Figure out which connection the arrayList then
	 * Send the input to the rest of the classes
	 * @param input
	 */
	public class Writing extends Thread{
		String input;
		Socket client;
		PrintWriter out;
		
		public Writing (String input, Socket client){
			this.input = input;
			this.client = client;
			start();
		}
		public void run (){
			sendToOthers();
		}
		/**
		 * Why doesn't this work...
		 */
		public synchronized void sendToOthers ( ){
			int i = 0;
			while (i < allClients.size()){
				if(allClients.get(i).equals(client)){ // close the connection..
					
					
				}else{
					try {
						out = new PrintWriter(allClients.get(i).getOutputStream());
						out.println(input);
						out.flush();
					} catch (IOException e) {
						System.out.println("Can't print to other sockets");
					}
				}
				i++; //go to the next Socket;
			}
		}
	}
	/**
	 * if run isn't called is it ever processed? lets find out...
	 * 
	 */
	public void run (){
		openStreams ();
		while (client.isConnected()){
			try {
				String input = in.readLine();
				if(input == null){
					// close Connection
					client.close();
				}else{
					sendText = new Writing (input,client);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}