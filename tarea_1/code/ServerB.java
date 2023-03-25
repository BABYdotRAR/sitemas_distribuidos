import java.io.IOException;
import java.lang.reflect.Constructor;

// Clase ejecutable para el servidor B
public class ServerB extends TCPServer
{
    public static void main(String[] args) throws IOException
    {
        if (args.length < 4) 
        {
            System.err.println("Uso: java ServerB <puerto Servidor B> <puerto Servidor A1> <puerto Servidor A2> <puerto Servidor A3>");
            System.exit(1);
        }

        int serverPort = Integer.parseInt(args[0]);
        int portA1 = Integer.parseInt(args[1]);
        int portA2 = Integer.parseInt(args[2]);
        int portA3 = Integer.parseInt(args[3]);

        ServerBClientHandler handler = new ServerBClientHandler(portA1, portA2, portA3);
        ServerB server = new ServerB(serverPort, "Servidor B", handler);

        server.runServer();
    }
    public ServerB(int port, String serverName, ClientHandler baseClientHandler) throws IOException 
    {
        super(port, serverName, baseClientHandler);
    }
    
    // Sobre escribimos el siguiente método para poder trabajar con instancias de
    // ServerBClientHandler cada que delegamos un cliente al Thread
    @Override
    protected ClientHandler getNewHandlerInstance(Class<?> handlerClass) throws Exception
    {
        ServerBClientHandler bHandler = (ServerBClientHandler) baseClientHandler;
        int ports[] = bHandler.getPorts();

        Constructor<?> handleConstructor = handlerClass.getConstructor(int.class, int.class, int.class);
        return (ServerBClientHandler) handleConstructor.newInstance(ports[0], ports[1], ports[2]);
    }
}
