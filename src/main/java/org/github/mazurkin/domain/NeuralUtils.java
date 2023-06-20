package org.github.mazurkin.domain;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Random;

public final class NeuralUtils {

    private NeuralUtils() {
        // utility class
    }

    public static double[] randomArray(int vecSize, int seed) {
        Random random = new Random(seed);

        double[] result = new double[vecSize];

        for (int c = 0; c < vecSize; c++) {
            double magnitude;
            if (random.nextDouble() < 0.05) {
                magnitude = 1.0;
            } else {
                magnitude = 1e+30;
            }
                result[c] = random.nextDouble() / magnitude;
        }
        return result;
    }

    public static double[][] randomArray(int rows, int columns, int seed) {
        Random random = new Random(seed);

        double[][] result = new double[rows][];

        for (int r = 0; r < rows; r++) {
            double[] row = new double[columns];

            for (int c = 0; c < columns; c++) {
                double magnitude;
                if (random.nextDouble() < 0.05) {
                    magnitude = 1.0;
                } else {
                    magnitude = 1e+30;
                }

                row[c] = random.nextDouble() / magnitude;
            }

            result[r] = row;
        }

        return result;
    }

    public static List<double[][]> buildEmbeddingList(int embeddingSize) {
        // 9 features
        return ImmutableList.of(
            randomArray(24, embeddingSize, 0xDEAD_01),
            randomArray(3, embeddingSize, 0xDEAD_02),
            randomArray(3, embeddingSize, 0xDEAD_03),
            randomArray(13, embeddingSize, 0xDEAD_04),
            randomArray(128363, embeddingSize, 0xDEAD_05),
            randomArray(184, embeddingSize, 0xDEAD_06),
            randomArray(385, embeddingSize, 0xDEAD_07),
            randomArray(40, embeddingSize, 0xDEAD_08),
            randomArray(115402, embeddingSize, 0xDEAD_09)
        );
    }

    public static double[][] buildTransposedLayer1(int embeddingSize, int innerSize) {
        return randomArray(innerSize, 9 * embeddingSize, 0xBEAF_01);
    }

    public static double[][] buildTransposedLayer2(int innerSize) {
        return randomArray(330, innerSize, 0xBEAF_02);
    }

    public static double[] buildBias1(int innerSize) {
        return randomArray(innerSize, 0xBEAF_03);
    }

    public static double[] buildBias2() {
        return randomArray(330, 0xBEAF_03);
    }

    public static int[][] buildInputs(List<double[][]> embeddingList, int inputListSize) {
        // the stable random generator is required to provide the same vectors on each execution
        Random random = new Random(0);

        int[][] inputs = new int[inputListSize][];

        for (int i = 0; i < inputListSize; i++) {
            int[] input = new int[embeddingList.size()];

            for (int j = 0; j < input.length; j++) {
                double[][] embedding = embeddingList.get(j);

                int rows = embedding.length;

                input[j] = random.nextInt(rows);
            }

            inputs[i] = input;
        }

        return inputs;
    }
}
