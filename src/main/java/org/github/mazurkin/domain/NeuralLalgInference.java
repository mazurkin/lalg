package org.github.mazurkin.domain;

import org.github.mazurkin.lalg.LalgMatrix;
import org.github.mazurkin.lalg.LalgOperations;
import org.github.mazurkin.lalg.LalgRowVector;
import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;

import java.util.List;
import java.util.stream.Collectors;

@ThreadSafe
public class NeuralLalgInference implements NeuralInference {

    private final List<LalgMatrix> embeddingMatrices;

    private final LalgMatrix transposedLayer1;

    private final LalgMatrix transposedLayer2;

    private final LalgRowVector bias1;

    private final LalgRowVector bias2;

    private final ThreadLocal<Buffers> contextThreadLocal;

    public NeuralLalgInference(List<double[][]> embeddingList,
                               double[][] transposedLayer1,
                               double[][] transposedLayer2,
                               double[] bias1,
                               double[] bias2)
    {
        this.embeddingMatrices = embeddingList.stream()
            .map(LalgMatrix::new)
            .collect(Collectors.toUnmodifiableList());

        final int embeddingSize = this.embeddingMatrices.stream()
            .mapToInt(m -> m.columns)
            .sum();

        this.transposedLayer1 = new LalgMatrix(transposedLayer1);
        Preconditions.checkArgument(embeddingSize == this.transposedLayer1.columns,
            "Layer 1 column mismatch: %s <> %s", embeddingSize, this.transposedLayer1.columns);

        this.transposedLayer2 = new LalgMatrix(transposedLayer2);
        Preconditions.checkArgument(this.transposedLayer1.rows == this.transposedLayer2.columns,
            "Layer 2 column mismatch: %s <> %s", this.transposedLayer1.rows, this.transposedLayer2.columns);

        this.bias1 = new LalgRowVector(bias1);
        Preconditions.checkArgument(this.bias1.columns == this.transposedLayer1.rows,
            "Layer 2 column mismatch: %s <> %s", this.bias1.columns, this.transposedLayer1.rows);

        this.bias2 = new LalgRowVector(bias2);
        Preconditions.checkArgument(this.bias2.columns == this.transposedLayer2.rows,
            "Layer 2 column mismatch: %s <> %s", this.bias2.columns, this.transposedLayer2.rows);

        this.contextThreadLocal = ThreadLocal.withInitial(
            () -> new Buffers(embeddingSize, this.transposedLayer1.rows, this.transposedLayer2.rows)
        );
    }

    @Override
    public double[] compute(int[] input) {
        Buffers buffers = contextThreadLocal.get();

        LalgRowVector embedding = buffers.embedding;
        computeEmbedding(input, embedding);

        LalgRowVector postLayer1 = buffers.postLayer1;
        embedding.multiplyByTransposedMatrix(transposedLayer1, postLayer1);

        LalgOperations.inplaceAdd(postLayer1.data, bias1.data, postLayer1.data);

        LalgOperations.inplaceRelu(postLayer1.data);

        LalgRowVector postLayer2 = buffers.postLayer2;
        postLayer1.multiplyByTransposedMatrix(transposedLayer2, postLayer2);

        LalgOperations.inplaceAdd(postLayer2.data, bias2.data, postLayer2.data);

        LalgOperations.inplaceSoftMax(postLayer2.data);

        LalgOperations.inplaceCumSum(postLayer2.data);

        return postLayer2.data;
    }

    private void computeEmbedding(int[] input, LalgRowVector target) {
        if (input.length != this.embeddingMatrices.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Input vector size doesn't the number of embedding matrices: %d <> %d",
                    input.length, this.embeddingMatrices.size()
                )
            );
        }

        for (int i = 0, targetOffset = 0; i < input.length; i++) {
            LalgMatrix sourceMatrix = this.embeddingMatrices.get(i);

            int sourceRow = input[i];

            System.arraycopy(
                sourceMatrix.data, sourceRow * sourceMatrix.columns,
                target.data, targetOffset,
                sourceMatrix.columns
            );

            targetOffset += sourceMatrix.columns;
        }
    }

    /**
     * Per-thread allocated buffers
     */
    private static class Buffers {

        private final LalgRowVector embedding;

        private final LalgRowVector postLayer1;

        private final LalgRowVector postLayer2;

        private Buffers(int embedding, int postLayer1, int postLayer2) {
            this.embedding = new LalgRowVector(embedding);
            this.postLayer1 = new LalgRowVector(postLayer1);
            this.postLayer2 = new LalgRowVector(postLayer2);
        }
    }
}
