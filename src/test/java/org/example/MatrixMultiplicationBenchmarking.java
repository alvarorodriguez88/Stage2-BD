package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {
        @Param({"10", "100", "500", "1000"})
        private int size;

        @Param({"0.0", "0.2", "0.5", "0.8"}) // Porcentajes de ceros (20%, 50%, 80%)
        private double zeroPercentage;

        private double[][] sparseMatrixA;
        private double[][] sparseMatrixB;
        private double[][] denseMatrixA;
        private double[][] denseMatrixB;
        private List<Long> memoryUsages;

        @Setup
        public void setup() {
            sparseMatrixA = createSparseMatrix(size, zeroPercentage);
            sparseMatrixB = createSparseMatrix(size, zeroPercentage);
            denseMatrixA = createDenseMatrix(size);
            denseMatrixB = createDenseMatrix(size);
            memoryUsages = new ArrayList<>();
        }

        public double calculateAverage(List<Long> values) {
            long sum = 0;
            for (long value : values) {
                sum += value;
            }
            return values.isEmpty() ? 0 : (double) sum / values.size();
        }

        @TearDown(Level.Trial)
        public void printResults() {
            double avgMemoryUsage = calculateAverage(memoryUsages);

            System.out.println("------ Benchmark Results ------");
            System.out.println("Average Memory used: " + avgMemoryUsage + " bytes");
            System.out.println("--------------------------------");
        }

        public double[][] createDenseMatrix(int size) {
            double[][] matrix = new double[size][size];
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextDouble();
                }
            }
            return matrix;
        }

        public double[][] createSparseMatrix(int size, double zeroPercentage) {
            double[][] matrix = new double[size][size];
            int totalElements = size * size;
            int zeroCount = (int) (totalElements * zeroPercentage);

            Random rand = new Random();

            // Llena la matriz con números aleatorios tipo double entre 0.1 y 9.9
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = 0.1 + (9.8 * rand.nextDouble()); // Números entre 0.1 y 9.9
                }
            }

            // Distribuye los ceros aleatoriamente
            int placedZeros = 0;
            while (placedZeros < zeroCount) {
                int randomRow = rand.nextInt(size);
                int randomCol = rand.nextInt(size);

                // Coloca un cero si la posición aún no tiene uno
                if (matrix[randomRow][randomCol] != 0.0) {
                    matrix[randomRow][randomCol] = 0.0;
                    placedZeros++;
                }
            }
            return matrix;
        }
    }

    @Benchmark
    public void naiveMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        NaiveMatrixMultiplication.multiply(operands.denseMatrixA, operands.denseMatrixB);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void blockingMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        BlockMatrixMultiplication.multiply(operands.denseMatrixA, operands.denseMatrixB, 64);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void parallelMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        ParallelMatrixMultiplication.multiply(operands.denseMatrixA, operands.denseMatrixB);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void csrSparseMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        SparseMatrixOptimizer sparseOptimizer = new SparseMatrixOptimizer(operands.sparseMatrixA);
        sparseOptimizer.multiplyWithCSRDenseMatrix(operands.sparseMatrixB);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void cscSparseMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        SparseMatrixOptimizer sparseOptimizer = new SparseMatrixOptimizer(operands.sparseMatrixA);
        sparseOptimizer.multiplyWithCSCDenseMatrix(operands.sparseMatrixB);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    private static long getMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MatrixMultiplicationBenchmarking.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
