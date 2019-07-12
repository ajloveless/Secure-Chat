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
	static ArrayList<ClientHandler> users = new ArrayList<ClientHandler>();

	public static void main(String[] args) throws IOException 
	{
		ServerSocket ss = new ServerSocket(1245);

		Socket s;
		String name = "" + (users.size() + 1);

		//Loop client requests
		while (true)
		{
			//Accept incoming requests
			s = ss.accept();

			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			try
			{
				name = (String) ois.readObject();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			System.out.println("New client " + s);

			//Get input and output streams
			DataInputStream is = new DataInputStream(s.getInputStream());
			DataOutputStream os = new DataOutputStream(s.getOutputStream());

			System.out.println("Creating handler for client...");

			ClientHandler handler = new ClientHandler(s, name, is, os);

			Thread t = new Thread(handler);

			System.out.println("Adding client to active client list");

			users.add(handler);

			t.start();


		}
	}
}