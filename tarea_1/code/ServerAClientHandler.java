import java.io.*;
import java.nio.ByteBuffer;

// Clase que se encarga de manejar cada cliente para el servidor A
public class ServerAClientHandler extends ClientHandler 
{
    private static final int LONG_SIZE = 8;
    private long number, startNumber, finalNumber;

    @Override
    protected void clientBehavior() throws Exception
    {
        DataOutputStream clientDOS = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream clientDIS = new DataInputStream(clientSocket.getInputStream());

        // Leemos los longs que manda el cliente
        byte inBytes[] = new byte[3 * LONG_SIZE];
        clientDIS.read(inBytes, 0, 3 * LONG_SIZE);

        // Asignamos estos longs a las variables de la clase
        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        readNumberValues(buffer);
        System.out.println("Leyendo los números...");

        // Comprobamos si el numero divide y enviamos la respuesta
        String response = isDivisible();
        clientDOS.writeUTF(response);
        System.out.println("Se ha enviado la respuesta al cliente.");
    }
    
    private String isDivisible()
    {
        for (long i =  startNumber; i <= finalNumber; i++) 
        {
            if(number % i == 0)
            {
                System.out.println("En el intervalo [" + startNumber + "," + 
                                finalNumber + "] se ha dividido a " + number);
                return "DIVIDE";
            }
        }

        System.out.println("En el intervalo [" + startNumber + "," +  finalNumber + 
                        "] no se ha dividido a " + number);
        return "NO DIVIDE";
    }

    private void readNumberValues(ByteBuffer buffer)
    {
        number = buffer.getLong();
        startNumber = buffer.getLong();
        finalNumber = buffer.getLong();
    }
}
