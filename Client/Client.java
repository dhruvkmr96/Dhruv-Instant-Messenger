package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/* Here you have to import some packages, like
 * java. awt, new,io,awt.event
 * javax.swing, swing.event
 */

public class Client extends JFrame{
	
	private JTextField jtf;
	private JTextArea jta;
	private Socket connection;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String message="",serverIP;
	
	public Client(String host){
		super("Client mofo!");
		jtf=new JTextField();
		jtf.setEditable(false);
		jtf.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage("CLIENT - "+event.getActionCommand());
						jtf.setText("");
					}
				}
				);
		add(jtf,BorderLayout.NORTH);
		
		jta=new JTextArea();
		jta.setEditable(false);
		add(new JScrollPane(jta));
		setVisible(true);
		setSize(300,400);
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setUpStreams();
			whileChating();
		}catch(EOFException eofException){
			showMessage("\nClient ended the connection.\n");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	public void connectToServer() throws IOException{
		showMessage("Waiting to connect to Server...\n");
		connection=new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Connected to "+connection.getInetAddress().getHostAddress()+"\n");
	}
	
	public void setUpStreams() throws IOException{
		oos = new ObjectOutputStream(connection.getOutputStream());
		oos.flush();
		ois = new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now set up.\n");
	}
	
	public void whileChating() throws IOException{
		//message = "You can now type message, \nPress Enter to chat...\n\n";
		//sendMessage(message);
		ableToType(true);int i=0;
		do{ 
			try{
				//System.out.println(i++);
				message=(String)ois.readObject();
				showMessage(message+"\n");
			}catch(ClassNotFoundException e){
				showMessage("\n <idk wtf that user sent.>\n");
			}
		}while(!message.equals("CLIENT - END"));
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
			jta.append("\nERROR SENDING THE MESSAGE.\n");
		}
	}
	
	public void closeCrap(){
		//showMessage("Closing the Connection..\n ");
		ableToType(false);
		try{
			oos.close();
			ois.close();
			connection.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
