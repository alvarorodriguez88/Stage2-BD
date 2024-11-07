package org.example;

public class CSCMatrixMultiplication {
    private final double[] values;
    private final int[] rowIndices;
    private final int[] colPointers;
    private final int rows;
    private final int cols;

    public CSCMatrixMultiplication(double[][] denseMatrix) {
        this.rows = denseMatrix.length;
        this.cols = denseMatrix[0].length;

        int nonZeroCount = 0;
        for (double[] row : denseMatrix) {
            for (double val : row) {
                if (val != 0) nonZeroCount++;
            }
        }

        values = new double[nonZeroCount];
        rowIndices = new int[nonZeroCount];
        colPointers = new int[cols + 1];

        int index = 0;
        for (int j = 0; j < cols; j++) {
            colPointers[j] = index;
            for (int i = 0; i < rows; i++) {
                if (denseMatrix[i][j] != 0) {
                    values[index] = denseMatrix[i][j];
                    rowIndices[index] = i;
                    index++;
                }
            }
        }
        colPointers[cols] = index;
    }

    public double[] getValues() {
        return values;
    }

    public int[] getRowIndices() {
        return rowIndices;
    }

    public int[] getColPointers() {
        return colPointers;
    }

    public int getRowCount() {
        return rows;
    }

    public int getColCount() {
        return cols;
    }
}
