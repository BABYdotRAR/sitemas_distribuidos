import java.io.*;
import java.net.*;

public class Program {
    public static int PORT = 8000;
    private static String host1 = "";
    private static String host2 = "";
    private static String host3 = "";
    public static void main(String[] args) throws Exception {
        int N = Integer.parseInt(args[0]);
        int node = Integer.parseInt(args[1]);

        if(node == 0){
            TCPClientAndMatrixHandler(host1, host2, host3, N);
        } else {
            TCPServer(node);
        }
    }

    public static void TCPServer(int node) throws Exception{
        ServerSocket server = new ServerSocket(PORT);
        server.setReuseAddress(true);
        int checkpoint = 0;

        System.out.println("Waiting connections from client in node " + node + "...");
        Socket client = server.accept();
        System.out.println("Client connected from " + client.getInetAddress().getHostAddress());

        System.out.println("Server " + node + " checkpoint " + ++checkpoint + ": client connected successfully.");
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        
        double[][] A_current = (double[][])in.readObject();
        double[][] B1 = (double[][])in.readObject();
        double[][] B2 = (double[][])in.readObject();
        double[][] B3 = (double[][])in.readObject();

        System.out.println("Server " + node + " checkpoint " + ++checkpoint + ": matrices' reading completed.");
        double[][] C1 = multiplyMatrices(A_current, B1);
        double[][] C2 = multiplyMatrices(A_current, B2);
        double[][] C3 = multiplyMatrices(A_current, B3);

        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

        System.out.println("Server " + node + " checkpoint " + ++checkpoint + ": multiply matrices completed.");
        out.writeObject(C1);
        out.writeObject(C2);
        out.writeObject(C3);

        System.out.println("Server " + node + " checkpoint " + ++checkpoint + ": matrices sended to client.");
        in.close();
        out.close();
        client.close();
        server.close();
    }

    public static void TCPClientAndMatrixHandler(String host1, String host2, String host3, int N) throws Exception {
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        double[][] C = new double[N][N];
        int checkpoint =  0;

        System.out.println("Checkpoint " + ++checkpoint + ": running.");
        initializeMatrices(A, B, C, N);
        B = transpose(B);

        System.out.println("Checkpoint " + ++checkpoint + ": matrices initialized.");
        double[][] A_1 = getThirdPartFromMatrix(A, 1);
        double[][] A_2 = getThirdPartFromMatrix(A, 2);
        double[][] A_3 = getThirdPartFromMatrix(A, 3);
        double[][] B_1 = getThirdPartFromMatrix(B, 1);
        double[][] B_2 = getThirdPartFromMatrix(B, 2);
        double[][] B_3 = getThirdPartFromMatrix(B, 3);

        System.out.println("Checkpoint " + ++checkpoint + ": matrices divided.");
        ServerConnection conn1 = new ServerConnection(host1, PORT, A_1, B_1, B_2, B_3);
        ServerConnection conn2 = new ServerConnection(host2, PORT, A_2, B_1, B_2, B_3);
        ServerConnection conn3 = new ServerConnection(host3, PORT, A_3, B_1, B_2, B_3);

        System.out.println("Checkpoint " + ++checkpoint + ": Connections created.");
        Thread tConn1 = new Thread(conn1);
        Thread tConn2 = new Thread(conn2);
        Thread tConn3 = new Thread(conn3);

        tConn1.start();
        tConn2.start();
        tConn3.start();

        tConn1.join();
        tConn2.join();
        tConn3.join();

        System.out.println("Checkpoint " + ++checkpoint + ": Connections finalized with the servers.");
        double[][] C1 = conn1.getC1();
        double[][] C2 = conn1.getC2();
        double[][] C3 = conn1.getC3();
        double[][] C4 = conn2.getC1();
        double[][] C5 = conn2.getC2();
        double[][] C6 = conn2.getC3();
        double[][] C7 = conn3.getC1();
        double[][] C8 = conn3.getC2();
        double[][] C9 = conn3.getC3();

        System.out.println("Checkpoint " + ++checkpoint + ": Ci's matrices obtained.");
        C = mergeMatrices(C1, C2, C3, C4, C5, C6, C7, C8, C9);
        double checksumC = checksum(C); 

        System.out.println("Checkpoint " + ++checkpoint + ": matrices merged and checksum obtained.");
        if(N <= 12) {
            System.out.println("A Matrix:");
            printMatrix(A, N);
            System.out.println("B Matrix:");
            printMatrix(B, N);
            System.out.println("C Matrix:");
            printMatrix(C, N);
        }
        System.out.println("C's Matrix checksum: " + checksumC);
    }

    public static class ServerConnection implements Runnable{
        private String server;
        private int port;
        private double[][] A_temp;
        private double[][] B_1;
        private double[][] B_2;
        private double[][] B_3;
        private double[][] C_1;
        private double[][] C_2;
        private double[][] C_3;

        public ServerConnection(String server, int port, double[][] A_temp, 
                                double[][] B_1, double[][] B_2, double[][] B_3){
            this.server = server;
            this.port = port;
            this.A_temp = A_temp;
            this.B_1 = B_1;
            this.B_2 = B_2;
            this.B_3  = B_3;
        }

        public double[][] getC1() { return C_1; }

        public double[][] getC2() { return C_2; }

        public double[][] getC3() { return C_3; }

        @Override
        public void run(){
            int checkpoint = 0;
            System.out.println("Thread checkpoint " + ++checkpoint + ": method run called.");
            try(Socket socket = new Socket(server, port)){
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                System.out.println("Thread checkpoint " + ++checkpoint + ": connection successfully established with the server.");
                out.writeObject(A_temp);
                out.writeObject(B_1);
                out.writeObject(B_2);
                out.writeObject(B_3);

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Thread checkpoint " + ++checkpoint + ": matrices sended.");
                C_1 = (double[][])in.readObject();
                C_2 = (double[][])in.readObject();
                C_3 = (double[][])in.readObject();

                System.out.println("Thread checkpoint " + ++checkpoint + ": matrices received.");
                out.close();
                in.close();
                socket.close();
            } catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }

    //region Matrix Operations
    public static void printMatrix(double[][] M, int N){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(M[i][j] + "\t");
            }
            System.out.print("\n");
        }
    }

    public static void initializeMatrices(double[][] A, double[][] B, double[][] C, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i + j;
                B[i][j] = 3 * i - j;
                C[i][j] = 0;
            }
        }
    }

    public static double[][] transpose(double[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        
        double[][] transposedMatrix = new double[numCols][numRows];
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }
        
        return transposedMatrix;
    }

    public static double checksum(double[][] matrix) {
        double sum = 0.0;
        
        for (double[] row : matrix) {
            for (double element : row) {
                sum += element;
            }
        }
        
        return sum;
    }

    public static double[][] getThirdPartFromMatrix(double[][] M, int part) {
        int numRows = M.length;
        int numCols = M[0].length;
        double[][] res = new double[numRows/3][numCols];
        
        int startRow, endRow;
        if (part == 1) {
            startRow = 0;
            endRow = numRows/3;
        } else if (part == 2) {
            startRow = numRows/3;
            endRow = 2*numRows/3;
        } else {
            startRow = 2*numRows/3;
            endRow = numRows;
        }
        
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < numCols; j++) {
                res[i-startRow][j] = M[i][j];
            }
        }
        
        return res;
    }

    public static double[][] multiplyMatrices(double[][] M1, double[][] M2) {
        int N = M1[0].length;// * 3;
        
        double[][] M3 = new double[N/3][N/3];
        
        for (int i = 0; i < N/3; i++) {
            double[] V1i = M1[i];
            for (int j = 0; j < N/3; j++) {
                double[] V2j = M2[j];
                double dotProduct = 0.0;
                for (int k = 0; k < N; k++) {
                    dotProduct += V1i[k] * V2j[k];
                }
                M3[i][j] = dotProduct;
            }
        }
        
        return M3;
    }

    public static double[][] mergeMatrices(double[][] C1, double[][] C2, double[][] C3, 
                                       double[][] C4, double[][] C5, double[][] C6,
                                       double[][] C7, double[][] C8, double[][] C9) {
        int n = C1.length * 3; // Calculate the size of the final matrix
        double[][] C = new double[n][n]; // Create the final matrix

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < n/3 && j < n/3) {
                    C[i][j] = C1[i][j];
                } else if (i < n/3 && j < 2*n/3) {
                    C[i][j] = C2[i][j-n/3];
                } else if (i < n/3 && j < n) {
                    C[i][j] = C3[i][j-2*n/3];
                } else if (i < 2*n/3 && j < n/3) {
                    C[i][j] = C4[i-n/3][j];
                } else if (i < 2*n/3 && j < 2*n/3) {
                    C[i][j] = C5[i-n/3][j-n/3];
                } else if (i < 2*n/3 && j < n) {
                    C[i][j] = C6[i-n/3][j-2*n/3];
                } else if (i < n && j < n/3) {
                    C[i][j] = C7[i-2*n/3][j];
                } else if (i < n && j < 2*n/3) {
                    C[i][j] = C8[i-2*n/3][j-n/3];
                } else if (i < n && j < n) {
                    C[i][j] = C9[i-2*n/3][j-2*n/3];
                }
            }
        }

        return C;
    }
    //endregion
}
