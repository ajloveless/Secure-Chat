import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Server
{
	//Arraylist of clients
	static ArrayList<ClientHandler> users = new ArrayList<ClientHandler>(); //Create array list of active users

	public static void main(String[] args) throws IOException 
	{
		ServerSocket ss = new ServerSocket(1245); //Create server socket with specified port

		Socket s;
		String name = ""; //Placeholder for client

		//Loop client requests
		while (true)
		{
			//Accept incoming requests as they come in
			s = ss.accept();

			//Get input and output streams
			DataInputStream is = new DataInputStream(s.getInputStream());
			DataOutputStream os = new DataOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			try
			{
				name = (String) ois.readObject(); //Receive name variable as given by client
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			System.out.println(name + " has connected"); //Server log

			ClientHandler handler = new ClientHandler(s, name, is, os); //Handler deals with sending and receiving messages

			Thread t = new Thread(handler); //Run handler on a new thread

			System.out.println("Adding " + name + " to active client list"); //Add user to active client list
			users.add(handler);

			t.start(); //Start the thread


		}
	}
}