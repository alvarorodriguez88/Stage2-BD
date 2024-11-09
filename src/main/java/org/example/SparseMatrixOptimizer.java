package org.example;

public class SparseMatrixOptimizer {

    private final CSRMatrixMultiplication.CSRMatrix csrMatrix;
    private final CSCMatrixMultiplication cscMatrix;


    public SparseMatrixOptimizer(double[][] denseMatrix) {
        this.csrMatrix = new CSRMatrixMultiplication.CSRMatrix(denseMatrix);
        this.cscMatrix = new CSCMatrixMultiplication(denseMatrix);
    }

    public double[][] multiplyWithCSRDenseMatrix(double[][] denseMatrix) {
        int rows = csrMatrix.getRows();
        int cols = denseMatrix[0].length;

        if (denseMatrix.length != rows) {
            throw new IllegalArgumentException("El número de filas de la matriz densa no coincide con las columnas de la matriz CSR.");
        }

        double[][] result = new double[rows][cols];
        double[] values = csrMatrix.getValues();
        int[] columnIndices = csrMatrix.getColumnIndices();
        int[] rowPointers = csrMatrix.getRowPointers();

        for (int i = 0; i < rows; i++) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++) {
                int colIndex = columnIndices[j];
                for (int k = 0; k < cols; k++) {
                    result[i][k] += values[j] * denseMatrix[colIndex][k];
                }
            }
        }
        return result;
    }

    public double[][] multiplyWithCSCDenseMatrix(double[][] denseMatrix) {
        int rows = denseMatrix.length;
        int cols = cscMatrix.getColCount();

        if (denseMatrix[0].length != cols) {
            throw new IllegalArgumentException("The number of columns in the dense matrix does not match the rows of the CSC matrix.");
        }

        double[][] result = new double[cscMatrix.getRowCount()][denseMatrix[0].length];
        double[] values = cscMatrix.getValues();
        int[] rowIndices = cscMatrix.getRowIndices();
        int[] colPointers = cscMatrix.getColPointers();

        for (int j = 0; j < cols; j++) {
            for (int k = colPointers[j]; k < colPointers[j + 1]; k++) {
                int rowIndex = rowIndices[k];
                for (int i = 0; i < denseMatrix[0].length; i++) {
                    result[rowIndex][i] += values[k] * denseMatrix[j][i];
                }
            }
        }
        return result;
    }
}
