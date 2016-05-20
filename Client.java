import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * The purpose of this Client class is to create a group chat..
 * The Server Should Already be running 
 * 
 * @author Jesaiah Prayor
 *
 */
public class Client{
	public static JFrame frame;
	public static JPanel display;
	public static JTextField showCase;
	public static JTextArea History;
	public static JTextField sendInfo;
	public static theHandler handles;
	public static String Ip = "127.0.0.1";
	public static int port = 1738;
	public static JButton ClientButton, sendButton;
	public static JTextField getIpAddress;
	public static String UserName;
	public static ConnectionHandler connect;
	
	
	public  Client (){
		// Get's UserName 
		UserName = askForName();
				
		theHandler handles = new theHandler ();
		frame = new JFrame (UserName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display = new JPanel ();
		display.setLayout(new BorderLayout(3,3));
				
		//Top of the Gui
		JPanel topDisplay = new JPanel ();
		topDisplay.setLayout (new GridLayout (1,2));
		ClientButton = new JButton ("Connects To");
		ClientButton.addActionListener(handles);
		getIpAddress = new JTextField ();
		getIpAddress.setText(Ip);
				
		topDisplay.add(ClientButton);
		topDisplay.add(getIpAddress);
				
		display.add(topDisplay, BorderLayout.NORTH);
				
		History = new JTextArea ();
		History.setText("History \n");
		History.setEditable(false);
				
		display.add(History, BorderLayout.CENTER);
		
		sendButton = new JButton ("Send");
		sendButton.addActionListener(handles);
		sendButton.setEnabled(false);
		sendInfo = new JTextField ();
				
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridLayout (1,2));
				
		newPanel.add(sendInfo);
		newPanel.add(sendButton);
		
		display.add(newPanel, BorderLayout.PAGE_END);
		
		frame.add(display);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
	public static String askForName(){
		String name = JOptionPane.showInputDialog("Please enter a userName for your group chat ");
		while (name == null ){
			name = JOptionPane.showInputDialog("Please enter a userName for your group chat ");
		}
		return name;
	}
	
	
	private class theHandler implements ActionListener{

		public void actionPerformed(ActionEvent event) {
			
			String whatAction = event.getActionCommand();
			
			if (whatAction.equals("Send")){
				connect.send();
			}
			
			else if (whatAction.equals("Connects To")){
				connect = new ConnectionHandler(port,"Socket",Ip);
			}

		}
	}
	/**
	 * This class should take care of the Communication between ChatApps
	 * 
	 * @author jesaiahprayor
	 *
	 */
	private class ConnectionHandler extends Thread{
		public int Port;
		public String typeOfServer;
		public String IP;
		public ServerSocket Server;
		public Socket client;
		public BufferedReader in;
		public PrintWriter out;
		
		public ConnectionHandler (int portNumber, String connect){
			Port = portNumber;
			typeOfServer = connect;
			History.append("Connecting \n");
			start ();
		}
		
		public ConnectionHandler (int portNumber, String connect, String ipAddress){
			Port = portNumber;
			typeOfServer = connect; 
			IP = ipAddress;
			History.append("Listening \n");
			start();
		}
		
		
		public void ConnectionFix (){
			
			ClientButton.setEnabled(false);
			sendButton.setEnabled(true);
			try {
				in = new BufferedReader (new InputStreamReader (client.getInputStream()));
				out = new PrintWriter (client.getOutputStream());
			}
			catch (IOException e ){
				History.append("These are the Errors " + e);
			}
		}
		
		public void send (){
			String info = sendInfo.getText();
			History.append(UserName + ": " +info +"\n");
			out.println(UserName + ": " +info );
			out.flush();
		}
		
		
		public void run (){
				try {
					client = new Socket (IP, Port );
					History.append("CONNECTION ESTABLISHED \n");
				}
				catch (IOException e){
					History.append("These are the Errors " + e);
				}
			
			// Connection's are Established... Fix the Gui
			// Set up the Data Streams
			ConnectionFix ();
			
			while (client.isConnected()){
				try {
					Thread.sleep(30);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try{
					String input = in.readLine();
					System.out.println("Does my code come through here????");
					if (input == null){
						// close connection??
					}
					else {
						History.append(input + "\n");
					}
				}
				catch (IOException e){
					History.append("These are the Errors " + e);
				}
			}
		}		
	}
	
	public static void main (String [] args){
		Client newClient = new Client ();
	}

}