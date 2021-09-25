import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread
{
    private static int port = 2094;
    private ChatGUI gui;
    private ServerSocket server;
    private Socket clientSocket;
    private BufferedReader in;
    private boolean clientConnected = true;
    
    Server(ChatGUI gui)
    {
        this.gui = gui;
        try 
        {
            server = new ServerSocket(port);
            System.out.println("A SERVER HAS STARTED");
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int getPort() { return port; }
    
    public void closeConnection()
    {
        try 
        {
            clientConnected = false;
            server.close();
            
            if(clientSocket != null)
                clientSocket.close();
            if(in != null)
                in.close();
            
            System.out.println("SERVER CLOSING");
        } 
        catch(IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {
        try //could result in a non-fatal exception, if trying to shutdown the server while the server thread is blocked on server.accept()
        {   
            System.out.println("STARTED LISTENING");
            clientSocket = server.accept();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("CONNECTED");
            String port = in.readLine(); //as long as the connection was successful, this is the first thing the client will always send
            String portNumber = port.split(" ")[1]; //server reads: /port xyz
            gui.updateMessageLog("** CONNECTION RECEIVED (" + clientSocket.getInetAddress().getHostAddress() + ") **");
            gui.updateMessageLog("** Peer Listening at ::" + portNumber + " **");
            gui.connectToPeer(clientSocket.getInetAddress().getHostAddress(), portNumber);
        }
        catch (IOException ex) 
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        while(clientConnected) 
        {
            try 
            {
                if(in.ready())
                {
                    String line = in.readLine();
                    if(!line.isEmpty()) 
                    {
                        if(line.equals("/exit")) //signal from other peer has shutdown the connection 
                        {                        //could use something else, this is just for a demonstration of the logic
                            gui.updateMessageLog("** PEER CLOSED CONNECTION **");
                            gui.disableMessageOptions();
                            this.closeConnection();
                            gui.enableConnectionOptions();
                            gui.setButtonConnect();
                            gui.restartClientServer();
                        }
                        else 
                        {
                            gui.updateMessageLog("> Received: " + line);
                        }
                    }
                }
            }
            catch (IOException ex) 
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("EXITED RUN()");
    }
}