package org.github.mazurkin.domain;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import javax.annotation.concurrent.ThreadSafe;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JBLAS implementation
 *
 * @see <a href="http://jblas.org/javadoc/index.html">JBLAS</a>
 */
@ThreadSafe
public class NeuralBlasInference implements NeuralInference {

    private final List<DoubleMatrix> embeddingList;

    private final int[] embeddingCumSizes;

    private final int embeddingTotalSize;

    private final DoubleMatrix layer1;

    private final DoubleMatrix layer2;

    private final DoubleMatrix bias1;

    private final DoubleMatrix bias2;

    public NeuralBlasInference(List<double[][]> embeddingList,
                               double[][] transposedLayer1,
                               double[][] transposedLayer2,
                               double[] bias1,
                               double[] bias2)
    {
        this.embeddingList = embeddingList.stream()
                .map(DoubleMatrix::new)
                .collect(Collectors.toUnmodifiableList());

        this.embeddingCumSizes = computeEmbeddingCumSizes(this.embeddingList);
        this.embeddingTotalSize =  this.embeddingCumSizes[this.embeddingCumSizes.length - 1];

        this.layer1 = new DoubleMatrix(transposedLayer1).transpose();
        this.layer2 = new DoubleMatrix(transposedLayer2).transpose();

        this.bias1 = new DoubleMatrix(bias1).transpose();
        this.bias2 = new DoubleMatrix(bias2).transpose();
    }

    @Override
    public double[] compute(int[] input) {
        DoubleMatrix vec = computeEmbedding(input);

        vec = relu(applyLayer(vec, this.layer1, this.bias1));
        vec = applyLayer(vec, this.layer2, this.bias2);
        vec = computeSoftMax(vec);
        vec = computeCumSum(vec);

        return vec.toArray();
    }

    private DoubleMatrix computeEmbedding(int[] input) {
        double[] result = new double[embeddingTotalSize];

        for(int i = 0; i < input.length; ++i) {
            int fromIdx = this.embeddingCumSizes[i];
            int tillIdx = this.embeddingCumSizes[i + 1];

            DoubleMatrix embedding = this.embeddingList.get(i);

            for(int j = fromIdx, k = 0; j < tillIdx; j++, k++) {
                result[j] = embedding.get(input[i], k);
            }
        }

        return new DoubleMatrix(1, result.length, result);
    }

    private static DoubleMatrix relu(DoubleMatrix input) {
        // inplace modification
        return input.maxi(0.0f);
    }

    private static DoubleMatrix applyLayer(DoubleMatrix input, DoubleMatrix layer, DoubleMatrix bias) {
        return input.mmul(layer).add(bias);
    }

    private static double computeAbsMax(DoubleMatrix input) {
        return input.normmax();
    }

    private static DoubleMatrix computeSoftMax(DoubleMatrix input) {
        double absMax = computeAbsMax(input);

        // not sure why we should subtract absolute from a possible negative number
        // inplace modification
        DoubleMatrix sub = input.subi(absMax);

        // inplace modification
        DoubleMatrix exp = MatrixFunctions.expi(sub);

        // inplace modification
        return exp.divi(exp.sum());
    }

    private static DoubleMatrix computeCumSum(DoubleMatrix input) {
        // inplace modification
        return input.cumulativeSumi();
    }

    private static int[] computeEmbeddingCumSizes(List<DoubleMatrix> embeddingList) {
        int[] result = new int[embeddingList.size() + 1];

        for(int i = 0, limit=embeddingList.size(); i < limit; i++) {
            DoubleMatrix embedding = embeddingList.get(i);

            result[i + 1] = result[i] + embedding.columns;
        }

        return result;
    }
}
