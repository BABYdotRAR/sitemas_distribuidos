import java.io.*;
import java.net.*;

public class MatrixSender {
    public static void main(String[] args) {
        int[][] matrix = new int[3000][3000]; // example matrix
        
        try (Socket socket = new Socket("localhost", 1234);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            int blockSize = 100; // size of each block
            int numBlocks = matrix.length / blockSize;
            
            for (int i = 0; i < numBlocks; i++) {
                for (int j = 0; j < numBlocks; j++) {
                    int[][] block = new int[blockSize][blockSize];
                    for (int k = 0; k < blockSize; k++) {
                        for (int l = 0; l < blockSize; l++) {
                            block[k][l] = matrix[i * blockSize + k][j * blockSize + l];
                        }
                    }
                    out.writeObject(block);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class MatrixOperations {
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int node = Integer.parseInt(args[1]);
        
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        double[][] C = new double[N][N];
        
        initializeMatrices(A, B, C, N);
        
        double checksumA = checksum(A);
        double checksumB = checksum(B);
        double checksumC = checksum(C);
        
        double[][] transposedA = transpose(A);
        double[][] transposedB = transpose(B);
        double[][] transposedC = transpose(C);
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
    
    public static double checksum(double[][] matrix) {
        double sum = 0.0;
        
        for (double[] row : matrix) {
            for (double element : row) {
                sum += element;
            }
        }
        
        return sum;
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
}

public class MatrixOperations {
    
    public static void main(String[] args) {
        int N = 9;
        
        double[][] M1 = new double[N/3][N];
        double[][] M2 = new double[N/3][N];
        
        // initialize matrices M1 and M2
        
        double[][] M3 = multiplyMatrices(M1, M2);
        
        double[][] M = new double[N][N];
        
        // initialize matrix M
        
        double[][] res1 = getThirdPartFromMatrix(M, 1);
        double[][] res2 = getThirdPartFromMatrix(M, 2);
        double[][] res3 = getThirdPartFromMatrix(M, 3);
    }
    
    public static double[][] multiplyMatrices(double[][] M1, double[][] M2) {
        int N = M1.length * 3;
        
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
}


