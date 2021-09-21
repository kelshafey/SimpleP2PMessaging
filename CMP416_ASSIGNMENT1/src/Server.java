import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread
{
    private ChatGUI gui;
    private ServerSocket server;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    
    Server(ChatGUI gui)
    {
        this.gui = gui;
        try 
        {
            server = new ServerSocket(2009);
            System.out.println("A SERVER HAS STARTED");
        } 
        catch (IOException ex) 
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
            gui.updateMessageLog("** CONNECTION RECEIVED ** \n");
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }
        catch (IOException ex) 
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        while (true) 
        {
            try 
            {
                String line = in.readLine();
                if(!line.isEmpty()) 
                {
                    if(line.equals("EXIT"))
                    {
                        gui.updateMessageLog("** CONNECTION CLOSED **");
                        clientSocket.close();
                        in.close();
                        out.close();
                    }
                        
                    else
                    {
                        gui.updateMessageLog("Received: " + line + "\n");
                    }
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
