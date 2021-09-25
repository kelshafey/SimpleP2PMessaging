import java.io.IOException;
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
    private PrintWriter out;
    private boolean connectionStatus = false;
    
    Client(String IP, String port, ChatGUI gui)
    {
        this.IP = IP;
        this.port = port;
        this.gui = gui;
    }
    
    public void sendMessage(String message)
    {
        out.println(message);
        gui.updateMessageLog("> Sent: " + message);
    }
    
    public void announcePort(int port)
    {
        out.println("/port " + port);
    }
    
    public boolean getConnectionStatus()
    {
        return connectionStatus;
    }
    
    public void closeConnection()
    {
        try 
        {
            connectionStatus = false;
            gui.disableMessageOptions();
            out.println("/exit");    //could be changed to something that users would not normally type if real application
            serverSocket.close();    //but this is just to implement the logic of shutdown
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
                out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);
                connectionStatus = true;
                System.out.println("A CLIENT HAS STARTED");
                gui.enableMessageOptions();
                gui.updateMessageLog("** CONNECTION INITIATED **");
                gui.disableConnectionOptions();
                gui.setButtonDisconnect();
                announcePort(Server.getPort());
                gui.setIPandPort(IP, port); //for the case where client connects back through gui
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Please Enter IP Address and Port", "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }  
        } 
        catch(IOException ex) 
        {
            JOptionPane.showMessageDialog(null, "Connection Could not be Established", "Connection Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
