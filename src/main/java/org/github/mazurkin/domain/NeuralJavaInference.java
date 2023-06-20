package org.github.mazurkin.domain;

import com.google.common.collect.Lists;

import javax.annotation.concurrent.ThreadSafe;

import java.util.List;

@ThreadSafe
public class NeuralJavaInference implements NeuralInference {

    private final List<double[][]> embeddingList;

    private final int embeddingTotalSize;

    private final double[][] transposedLayer1;
    private final double[] bias1;

    private final double[][] transposedLayer2;
    private final double[] bias2;

    public NeuralJavaInference(List<double[][]> embeddingList,
                               double[][] transposedLayer1,
                               double[][] transposedLayer2,
                               double[] bias1,
                               double[] bias2)
    {
        this.embeddingList = Lists.newArrayList(embeddingList);
        this.embeddingTotalSize = computeEmbeddingTotalSize(this.embeddingList);
        this.transposedLayer1 = transposedLayer1;
        this.transposedLayer2 = transposedLayer2;
        this.bias1 = bias1;
        this.bias2 = bias2;
    }

    @Override
    public double[] compute(int[] input) {
        double[] vec = computeEmbedding(input);

        vec = relu(applyLayer(vec, this.transposedLayer1, this.bias1));
        vec = applyLayer(vec, this.transposedLayer2, this.bias2);
        vec = computeSoftMax(vec);
        vec = computeCumSum(vec);

        return vec;
    }

    private double[] computeEmbedding(int[] input) {
        double[] result = new double[embeddingTotalSize];

        for (int i = 0, k = 0; i < input.length; i++) {
            double[][] embeddings = this.embeddingList.get(i);

            double[] embeddingsLine = embeddings[input[i]];

            for(int j = 0; j < embeddingsLine.length; j++, k++) {
                result[k] = embeddingsLine[j];
            }
        }

        return result;
    }

    private static double[] relu(double[] input) {
        for (int i = 0; i < input.length; i++) {
            double v = input[i];

            if (v < 0.0) {
                input[i] = 0.0;
            }
        }

        return input;
    }

    private static double[] applyLayer(double[] input, double[][] layer, double[] bias) {
        double[] output = new double[layer.length];

        for (int i = 0; i < layer.length; i++) {
            double[] layerLine = layer[i];

            for(int j = 0; j < input.length; j++) {
                output[i] += input[j] * layerLine[j];
            }
        }

        for (int i = 0; i < output.length; i++) {
            output[i] += bias[i];
        }

        return output;
    }

    private static int computeArgAbsMax(double[] input) {
        double maxVal = 0;
        int maxIdx = 0;

        for (int i = 0; i < input.length; i++) {
            double v = Math.abs(input[i]);

            if (v > maxVal) {
                maxVal = v;
                maxIdx = i;
            }
        }

        return maxIdx;
    }

    private static double[] computeSoftMax(double[] input) {
        int absMaxIdx = computeArgAbsMax(input);

        double absMax = input[absMaxIdx];

        double[] expVec = new double[input.length];
        double expVecTotal = 0.0f;

        for (int i = 0; i < input.length; i++) {
            double v = Math.exp(input[i] - absMax);
            expVec[i] = v;
            expVecTotal += v;
        }

        for (int i = 0; i < input.length; i++) {
            expVec[i] /= expVecTotal;
        }

        return expVec;
    }

    private static double[] computeCumSum(double[] input) {
        double[] val = new double[input.length];

        double sum = 0.0;

        for (int i = 0; i < input.length; i++) {
            sum += input[i];
            val[i] = sum;
        }

        return val;
    }

    private static int computeEmbeddingTotalSize(List<double[][]> embeddingList) {
        int sum = 0;

        for (double[][] embedding : embeddingList) {
            sum += embedding[0].length;
        }

        return sum;
    }
}
