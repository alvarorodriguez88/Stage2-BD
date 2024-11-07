package org.example;

public class BlockMatrixMultiplication {
    public static double[][] multiply(double[][] a, double[][] b, int blockSize) {
        int n = a.length;
        double[][] c = new double[n][n];
        for (int i = 0; i < n; i += blockSize) {
            for (int j = 0; j < n; j += blockSize) {
                for (int k = 0; k < n; k += blockSize) {
                    for (int ii = i; ii < Math.min(i + blockSize, n); ii++) {
                        for (int jj = j; jj < Math.min(j + blockSize, n); jj++) {
                            double sum = 0.0;
                            for (int kk = k; kk < Math.min(k + blockSize, n); kk++) {
                                sum += a[ii][kk] * b[kk][jj];
                            }
                            c[ii][jj] += sum;
                        }
                    }
                }
            }
        }
        return c;
    }
}
