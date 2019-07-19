import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

class ClientHandler implements Runnable
{
	Scanner scan = new Scanner(System.in);
	private String name;
	final DataInputStream is;
	final DataOutputStream os;
	Socket s;
	boolean loggedIn;

	public ClientHandler(Socket s, String name, DataInputStream is, DataOutputStream os)
	{
		this.is = is;
		this.os = os;
		this.name = name;
		this.s = s;
		this.loggedIn = true;

	}


	public void log(String msg) throws IOException //Writes only to client side
	{
		this.os.writeUTF("[" + msg + "]"); //Write to own output stream
	}

	public void post(String msg) throws IOException //Writes to server side
	{
		for (ClientHandler recipient : Server.users) //For everyone
			recipient.os.writeUTF(msg); //Write to their output stream
	}


	//Commands
	public void commandName(String message, String command) throws IOException // usage: /name <name> - change display name
	{
		Boolean unique = true; //Assume the new name is unique
		if (message.length() > command.length() + 2) //Check if the command has an argument
		{
			String newName = message.substring(command.length() + 2); //What the new name should be
			for (ClientHandler usr : Server.users) //Check all clients connected
			{
				if (usr.name.equals(newName)) //If they already have the name selected
				{
					unique = false; //The name is not unique
					log("The name \"" + newName + "\" is already taken"); //Tell the user
				}
			}
			if (unique) //If the name is unique
			{
				post("[" + this.name + " changed their name to " + newName + "]"); //Tell the user
				this.name = newName; //Update the name
			}
		}
		else //If a name wasn't given
		{
			log("Please enter a name"); //Tell the user
		}
	}	


	public void commandLogout() throws IOException // usage: /logout - log out from chat
	{
		this.loggedIn = false; //Break while loop
		post("[" + this.name + " has logged out" + "]"); //Tell others on the server
		Server.users.remove(Server.users.indexOf(this)); //Remove from active users list
	}



	@Override
	public void run()
	{
		String message;

		try 
		{
			post("[" + this.name + " has entered the chat" + "]"); //When first joining, send a join message
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		while (this.loggedIn)
		{
			try
			{
				//Receive the string
				message = is.readUTF();


				//Command handling 
				if(message.startsWith("/"))
				{
					String command = message.split(" ", 0)[0].substring(1).toLowerCase(); //Get the command word given
					switch(command)
					{
						case "logout":
							commandLogout();
						break;

						
						case "name":
							commandName(message,command);
						break;

						default:
							log("Command not found");
						break;


					}
				}
				else //If no command was issued
				{
					post(this.name + ": " + message); //Print the username followed by their message
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace(); 
			}
		}
		try //When while loop is closed (user logs out)
		{				
			this.is.close();
			this.os.close();
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}