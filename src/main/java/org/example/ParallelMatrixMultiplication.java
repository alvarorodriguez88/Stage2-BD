package org.example;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ParallelMatrixMultiplication {
    private static final ForkJoinPool pool = new ForkJoinPool();

    public static double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];
        pool.invoke(new MatrixMultiplyTask(a, b, c, 0, n));
        return c;
    }

    static class MatrixMultiplyTask extends RecursiveAction {
        private final double[][] a, b, c;
        private final int startRow, endRow;
        private static final int THRESHOLD = 64;

        MatrixMultiplyTask(double[][] a, double[][] b, double[][] c, int startRow, int endRow) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            if (endRow - startRow <= THRESHOLD) {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < b[0].length; j++) {
                        double sum = 0.0;
                        for (int k = 0; k < b.length; k++) {
                            sum += a[i][k] * b[k][j];
                        }
                        c[i][j] = sum;
                    }
                }
            } else {
                int mid = (startRow + endRow) / 2;
                MatrixMultiplyTask left = new MatrixMultiplyTask(a, b, c, startRow, mid);
                MatrixMultiplyTask right = new MatrixMultiplyTask(a, b, c, mid, endRow);
                invokeAll(left, right);
            }
        }
    }
}
