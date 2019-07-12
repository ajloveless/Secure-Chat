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


	public void log(String msg) throws IOException
	{
		this.os.writeUTF("[" + msg + "]");
	}

	public void post(String msg) throws IOException
	{
		for (ClientHandler recipient : Server.users)
			recipient.os.writeUTF(msg);
	}



	@Override
	public void run()
	{
		String message;

		try 
		{
			post("[" + this.name + " has entered the chat" + "]");
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



				if(message.startsWith("/"))
				{
					String command = message.split(" ", 0)[0].substring(1).toLowerCase();
					switch(command)
					{
						case "logout":
							this.loggedIn = false;
							post("[" + this.name + " has logged out" + "]");
							Server.users.remove(Server.users.indexOf(this));


							//this.s.close();
						break;

						
						case "name":
							Boolean unique = true;
							if (message.length() > command.length() + 2)
							{
								String newName = message.substring(command.length() + 2);
								for (ClientHandler usr : Server.users)
								{
									if (usr.name.equals(newName))
									{
										unique = false;
										log("The name \"" + newName + "\" is already taken");
									}
								}
								if (unique)
								{
									post("[" + this.name + " changed their name to " + newName + "]");
									this.name = newName;
								}
							}
							else
							{
								log("Please enter a name");
							}
						break;

						default:
							log("Command not found");
						break;


					}
				}
				else
				{
					post(this.name + ": " + message);
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace(); 
			}
		}
		try
		{	
			//Close resources

			this.is.close();
			this.os.close();
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}