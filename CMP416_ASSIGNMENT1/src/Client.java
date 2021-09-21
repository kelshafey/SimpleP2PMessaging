import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Client extends Thread
{
    private String IP;
    private String port;
    private Socket serverSocket;
    private ChatGUI gui;
    private BufferedReader in;
    private PrintWriter out;
    
    Client(String IP, String port, ChatGUI gui)
    {
        this.IP = IP;
        this.port = port;
        this.gui = gui;
    }
    
    public void sendMessage(String message)
    {
        out.println(message);
    }
    
    public void closeConnection()
    {
        try 
        {
            gui.disableMessageOptions();
            out.println("EXIT");
            serverSocket.close();
            in.close();
            out.close();
        } 
        catch(IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {
        try
        {
        if(!(IP.isEmpty()) && !(port.isEmpty()))
            {
                serverSocket = new Socket(IP, Integer.parseInt(port));
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);
                System.out.println("A CLIENT HAS STARTED");
                gui.enableMessageOptions();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Please Enter IP Address and Port", "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }  
        } 
        catch(IOException ex) 
        {
            JOptionPane.showMessageDialog(null, "Connection Could not be Established :(", "Connection Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}
