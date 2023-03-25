import java.net.*;
import java.io.*;

// Clase que se encarga de enviar un número al servidor B
public class Client 
{
    public static void main(String[] args) 
    {
        if (args.length != 2) 
        {
            System.err.println("Uso: java Client <puerto Servidor B> <número a evaluar>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        long target = Long.parseLong(args[1]);

        try (
            Socket socket = new Socket("localhost", port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
        ) {
            // Se envía el número al servidor B y esperamos la respuesta
            out.writeLong(target);
            String response = in.readUTF();
            System.out.println("El número que se envió al servidor: " + response);
        } 
        catch (IOException e) 
        {
            System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
            System.exit(1);
        }
    }
}
