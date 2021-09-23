import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class Server extends Thread
{
    private ChatGUI gui;
    private ServerSocket server;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    boolean clientConnected = true;
    
    Server(ChatGUI gui)
    {
        this.gui = gui;
        try 
        {
            server = new ServerSocket(2047);
            System.out.println("A SERVER HAS STARTED");
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection()
    {
        try 
        {
            server.close();
            clientSocket.close();
            in.close();
            out.close();
            System.out.println("SERVER CLOSING");
            clientConnected = false;
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
            System.out.println("STARTED LISTENING");
            clientSocket = server.accept();
            System.out.println("CONNECTED");
            gui.updateMessageLog("** CONNECTION RECEIVED (" + clientSocket.getInetAddress().getHostAddress() + ") **");
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }
        catch (IOException ex) 
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        while(clientConnected) 
        {
            try 
            {
                String line = in.readLine();
                if(!line.isEmpty()) 
                {
                    if(line.equals("EXIT")) //to signal that other peer has shutdown the connection
                    {                       //should use something that users wouldn't type in real application, but this is fine for now
                        gui.updateMessageLog("** PEER CLOSED CONNECTION **");
                        gui.updateMessageLog("** EXITING IN 5 SECONDS **");
                        gui.disableMessageOptions();
                        this.closeConnection();
                    }
                        
                    else
                    {
                        gui.updateMessageLog("> Received: " + line);
                    }
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Timer timer = new Timer(5000, new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                System.exit(0);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
