 package Server;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{

	private JTextField jtf;
	private JTextArea jta;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	
	public Server(){
		super("Dhruv's Instant Messenger");
		jtf=new JTextField();
		jtf.setEditable(false);
		jtf.addActionListener(
		
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage("SERVER - "+event.getActionCommand());
						jtf.setText("");
						
						
					}
				}
		);
		add(jtf,BorderLayout.NORTH);
		jta=new JTextArea();
		jta.setEditable(false);
		add(new JScrollPane(jta));
		setSize(300,400);
		setVisible(true);
	}
	
	//set up and run the server
	
	public void startRunning(){
		try{
			
			server=new ServerSocket(6789,100);
			//first argument is the port number
			//second argument is backlog, only this number of people can sit and wait to talk to you
			//this port number must be known to the client
			while(true){
				try{
					//connect and have conversation with someone else
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection.\n");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect .... \n");
		connection=server.accept();
		//its only going to create this connection if this connects to someone
		showMessage(" Now connected to "+connection.getInetAddress().getHostName()+"\n");
		//it returns your ip address
	}
	
	// get stream to send and receive data
	private void setupStreams() throws IOException{
		oos=new ObjectOutputStream(connection.getOutputStream());
		oos.flush();
		ois=new ObjectInputStream(connection.getInputStream());
		//only they can push bytes their computer to you
		showMessage("Streams are now set up.\n");
	}
	private void whileChatting() throws IOException{
		String message="You can now type message, \nPress Enter to chat...\n\n";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message=(String)ois.readObject();
				showMessage(message+"\n");
			}catch(ClassNotFoundException e){
				showMessage("\n <idk wtf that user sent.\n>");
			}
		}while(!message.equals("CLIENT - END"));
	}
	public void closeCrap(){
		showMessage("\nClosing the connection...\n");
		ableToType(false);
		try{
			oos.close();
			ois.close();
			connection.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void showMessage(final String message){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						jta.append(message);
					}
				}
		);
	}
	public void ableToType(final boolean b){
		SwingUtilities.invokeLater(
				new Runnable(){
				
					public void run(){
						jtf.setEditable(b);
					}
				}
				);
	}
	public void sendMessage(String message){
		try{
			oos.writeObject(message);
			oos.flush();
			showMessage(message+"\n");
		}catch(IOException e){
			jta.append("\nError Sending Message\n");
		}
	}
	
	
}
