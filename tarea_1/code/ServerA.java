import java.io.IOException;
import java.lang.reflect.Constructor;

// Clase ejecutable para el servidor A
public class ServerA extends TCPServer 
{
    public static void main(String[] args) throws IOException
    {
        if (args.length < 1) 
        {
            System.err.println("Uso: java ServerA <puerto Servidor A>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        ServerAClientHandler handler = new ServerAClientHandler();
        ServerA server = new ServerA(port, "Servidor A", handler);

        server.runServer();
    }
    
    public ServerA(int port, String serverName, ClientHandler baseClientHandler) throws IOException 
    {
        super(port, serverName, baseClientHandler);
    }
    
    // Sobre escribimos el siguiente método para poder trabajar con instancias de
    // ServerAClientHandler cada que delegamos un cliente al Thread
    @Override
    protected ClientHandler getNewHandlerInstance(Class<?> handlerClass) throws Exception
    {
        Constructor<?> handleConstructor = handlerClass.getConstructor();
        return (ServerAClientHandler) handleConstructor.newInstance();
    }
}
