import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.border.Border;
import java.lang.Integer;

public class Client
{
    private static int port;
    private static JFrame frame;
    private static JPanel panel;
    private static JTextField textField;
    private static JTextField textField1;
    private static JTextField textField2;
    private static JTextArea textArea;
    private static JScrollPane scrollPanel;
    private static DefaultCaret caret;
    private static JLabel label;
    private static JLabel label1;
    private static JLabel label2;
    private static JButton button;




    public static void main(String args[]) throws UnknownHostException, IOException
    {




        //Frame
        frame = new JFrame("Log in"); //Title text
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close operation
        frame.setSize(300,150); //Window size
        frame.setResizable(false); //Set to fixed size


        //Text box
        textField = new JTextField(16); //Name
        textField1 = new JTextField(16); //IP
        textField2 = new JTextField(16); //Port

        label = new JLabel("Name:");
        label1 = new JLabel("IP:");
        label2 = new JLabel("Port:");

        //Text box Panel
        panel = new JPanel(new GridLayout(0,2,5,5)); //Create panel for all elements
        panel.add(label);
        panel.add(textField);
        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);

        button = new JButton("Connect");
        button.addActionListener( (ActionEvent event) -> {
                try
                {
                    String loginName = textField.getText();
                    String loginIP = textField1.getText();
                    String loginPort = textField2.getText();
                    frame.dispose();
                    connect(loginName,loginIP,loginPort);
                }
                catch(UnknownHostException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
        });
        //Layout
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, button);
        frame.setVisible(true);
    }

    public static void connect(String loginName, String loginIP, String loginPort) throws UnknownHostException, IOException, SocketException
    {
            // getting localhost ip

            Socket s = new Socket();
            // establish the connection
            try
            {
                InetAddress ip = InetAddress.getByName(loginIP);
                int port = Integer.parseInt(loginPort);
                s = new Socket(ip, port);
            }
            catch(Exception e)
            {
                frame = new JFrame(); //Title text
                JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }



            // obtaining input and out streams
            DataInputStream is = new DataInputStream(s.getInputStream());
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            String name = new String(loginName);
            oos.writeObject(name);
            oos.flush();

            //Frame
            frame = new JFrame("Secure Chat"); //Title text
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close operation
            frame.setSize(400,400); //Window size
            frame.setResizable(false); //Set to fixed size


            //Text box
            textField = new JTextField(140); //Create text field

            //Text area
            textArea = new JTextArea(); //Text area for chat
            textArea.setColumns(1);
            textArea.setLineWrap(true);
            textArea.setRows(20);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            caret = (DefaultCaret)textArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            //Scroll bar
            scrollPanel = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            //Text box Panel
            panel = new JPanel(new SpringLayout()); //Create panel for all elements
            panel.add(textField);

            //Layout
            frame.getContentPane().add(BorderLayout.CENTER, panel);
            frame.getContentPane().add(BorderLayout.NORTH, scrollPanel);
            frame.setVisible(true);

            // sendMessage thread
            Thread sendMessage = new Thread( () -> {
            textField.addActionListener((ActionEvent event) -> {
                    String message = textField.getText();

                    try
                    {
                        // write on the output stream
                         os.writeUTF(message);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    textField.setText("");
            });
            });

            // readMessage thread
            Thread readMessage = new Thread( () ->{

                    while (true)
                    {
                        try
                        {
                            // read the message sent to this client
                            String message = is.readUTF();
                            textArea.append(message + "\n");
                        }
                        catch (IOException e)
                        {

                            //e.printStackTrace();
                        }
                    }
            });

            sendMessage.start();
            readMessage.start();

        }
}
