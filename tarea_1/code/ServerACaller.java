import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.*;

// Clase intermediaria entre el servidor B y el servidor A
public class ServerACaller implements Runnable
{
    private static final int LONG_SIZE = 8;
    private String response;
    private int port;
    private Socket connection;
    private long number, lowEndpoint, topEndpoint;

    public ServerACaller(int port, long number, long lowEndpoint, long topEndpoint) 
    {
        this.port = port;
        this.number = number;
        this.lowEndpoint = lowEndpoint;
        this.topEndpoint = topEndpoint;
    }

    private void connect() throws IOException
    {
        // Iniciamos la conexión con alguna instancia del servidor A
        connection = new Socket("localhost", port);

        DataInputStream connDIS = new DataInputStream(connection.getInputStream());
        DataOutputStream connDOS = new DataOutputStream(connection.getOutputStream());

        // Convertimos los longs a bytes y se los enviamos al servidor
        byte longsToSend[] = longsToByteArray();
        connDOS.write(longsToSend, 0, longsToSend.length);

        // Asignamos la respuesta del servidor a nuestra variable local
        response = connDIS.readUTF();
    }
    
    public String getResponse() { return response; }

    private byte[] longsToByteArray()
    {
        ByteBuffer tempBuffer = ByteBuffer.allocate(3 * LONG_SIZE);

        tempBuffer.putLong(number);
        tempBuffer.putLong(lowEndpoint);
        tempBuffer.putLong(topEndpoint);

        return tempBuffer.array();
    }

    @Override
    public void run() 
    {
        try
        {
            connect();
        }
        catch(Exception e) { e.printStackTrace(); }
        finally
        {
            try{ connection.close(); }
            catch(IOException e) { e.printStackTrace(); }
        }
    }
}
