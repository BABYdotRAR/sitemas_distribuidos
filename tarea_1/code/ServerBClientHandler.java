import java.io.*;

// Clase que se encarga de manejar cada cliente para el servidor B
public class ServerBClientHandler extends ClientHandler 
{
    private long target, firstEndpoint, secondEndpoint;
    private int portA1, portA2, portA3;
    private ServerACaller caller1, caller2, caller3;
    boolean isPrime;

    public ServerBClientHandler(int portA1, int portA2, int portA3) 
    {
        this.portA1 = portA1;
        this.portA2 = portA2;
        this.portA3 = portA3;
        isPrime = false;
    }

    @Override
    protected void clientBehavior() throws Exception
    {
        DataInputStream clientDIS = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream clientDOS = new DataOutputStream(clientSocket.getOutputStream());

        // Leemos el número del cliente y evaluamos intervalos
        // iniciamos los objetos que enviaran las peticiones al server A
        target = clientDIS.readLong();
        System.out.println("Se recibió el número " + target + ", creando los intervalos...");
        assignEndpoints();
        initCallers();

        // Corremos los hilos que realizan peticiones al servidor A
        System.out.println("Enviado los intervalos a los servidores A en paralelo");
        runThreads();

        // Comprobamos si el numero es primo
        System.out.println("Se ha recibido respuesta de todos los intervalos, determinando si " 
                            + target + " es primo...");
        isPrime = (caller1.getResponse().equalsIgnoreCase(caller2.getResponse()) && 
                caller2.getResponse().equalsIgnoreCase(caller3.getResponse()) && 
                caller3.getResponse().equalsIgnoreCase("NO DIVIDE"));
        
        String res = "NO ES PRIMO";
        if(isPrime)
            res = "ES PRIMO";

        // Le enviamos si es o no primo al cliente   
        System.out.println("Enviando respuesta al cliente..."); 
        clientDOS.writeUTF(res);
    }

    private void assignEndpoints()
    {
        firstEndpoint =(long) (target / 3);
        secondEndpoint = firstEndpoint * 2;
    }

    private void initCallers()
    {
        caller1 = new ServerACaller(portA1, target, 2, firstEndpoint);
        caller2 = new ServerACaller(portA2, target, firstEndpoint + 1, secondEndpoint);
        caller3 = new ServerACaller(portA3, target, secondEndpoint + 1, target - 1);
    }

    private void runThreads() throws Exception
    {
        Thread tCall1 = new Thread(caller1);
        Thread tCall2 = new Thread(caller2);
        Thread tCall3 = new Thread(caller3);

        tCall1.start();
        tCall2.start();
        tCall3.start();

        tCall1.join();
        tCall2.join();
        tCall3.join();
    }

    public int[] getPorts()
    {
        int[] ports = {portA1, portA2, portA3};
        return ports;
    }
}
