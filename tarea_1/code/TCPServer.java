import java.io.*;
import java.net.*;

// Clase base para los servidores
public class TCPServer
{
    protected ServerSocket server;
    protected String serverName;
    protected ClientHandler baseClientHandler;

    // Cada vez que creemos un servidor le pasaremos el puerto donde se iniciara,
    // su nombre y el controlador para el cliente
    public TCPServer(int port, String serverName, ClientHandler baseClientHandler) throws IOException 
    {
        server = new ServerSocket(port);
        server.setReuseAddress(true);

        this.serverName = serverName;
        this.baseClientHandler = baseClientHandler;
    }

    // Método para correr el servidor
    public void runServer()
    {
        try
        {
            System.out.println("Servidor " + serverName + " iniciado en el puerto " + server.getLocalPort());
        
            while(true)
            {
                System.out.println("Esperando conexiones...");

                // Aceptamos la conexión
                Socket client = server.accept();
                System.out.println("Cliente conectado desde: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                
                // Delegamos el cliente a un nuevo hilo
                handleThread(client);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handleThread(Socket clientSocket)
    {
        // Obtenemos la clase del handler base
        Class<?> handlerClass = baseClientHandler.getClass();

        try
        {
            ClientHandler tHandler = getNewHandlerInstance(handlerClass);
            tHandler.setClientSocket(clientSocket);
            Thread clientThread = new Thread(tHandler);
            clientThread.start();
        }
        catch(Exception e){ e.printStackTrace();}
    }

    // Método a sobre escribir por los hijos, de esa forma se pueden crear
    // handlers específicos dependiendo del server
    protected ClientHandler getNewHandlerInstance(Class<?> handlerClass) throws Exception
    {
        return new ClientHandler();
    }
}