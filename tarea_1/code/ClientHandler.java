import java.io.IOException;
import java.net.*;

// Clase base para cada tipo de cliente distinto
public class ClientHandler implements Runnable 
{
    protected Socket clientSocket;

    public void run()
    {
        try
        {
            clientBehavior();
        }
        catch(Exception e){ e.printStackTrace(); }
        finally
        {
            try { clientSocket.close(); }
            catch(IOException e) { e.printStackTrace(); }
        }
    }

    // Método que hay que sobre escribir en las clases hijo para 
    // implementar la lógica
    protected void clientBehavior() throws Exception
    {
        System.out.println("Implement the client logic here.");
    }

    public Socket getClientSocket()
    {
        return clientSocket;
    }

    public void setClientSocket(Socket socket)
    {
        clientSocket = socket;
    }
}
