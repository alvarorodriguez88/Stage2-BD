package org.example;

public class CSRMatrixMultiplication {
    public static class CSRMatrix {

        private final double[] values;
        private final int[] columnIndices;
        private final int[] rowPointers;
        private final int rows;

        public CSRMatrix(double[][] denseMatrix) {
            this.rows = denseMatrix.length;
            int nonZeroCount = 0;
            for (double[] row : denseMatrix) {
                for (double val : row) {
                    if (val != 0) nonZeroCount++;
                }
            }
            values = new double[nonZeroCount];
            columnIndices = new int[nonZeroCount];
            rowPointers = new int[rows + 1];

            int index = 0;
            for (int i = 0; i < rows; i++) {
                rowPointers[i] = index;
                for (int j = 0; j < denseMatrix[i].length; j++) {
                    if (denseMatrix[i][j] != 0) {
                        values[index] = denseMatrix[i][j];
                        columnIndices[index] = j;
                        index++;
                    }
                }
            }
            rowPointers[rows] = index;
        }

        public double[] multiply(double[] vector) {
            double[] result = new double[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++) {
                    result[i] += values[j] * vector[columnIndices[j]];
                }
            }
            return result;
        }

        public double[] getValues() {
            return values;
        }

        public int[] getColumnIndices() {
            return columnIndices;
        }

        public int[] getRowPointers() {
            return rowPointers;
        }

        public int getRows() {
            return rows;
        }
    }
}
