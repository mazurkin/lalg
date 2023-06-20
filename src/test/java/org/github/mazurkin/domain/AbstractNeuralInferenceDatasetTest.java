package org.github.mazurkin.domain;

import com.google.common.primitives.Doubles;
import org.apache.commons.math3.stat.StatUtils;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractNeuralInferenceDatasetTest {

    private static List<double[][]> embeddings;

    private static double[][] layer1;

    private static double[][] layer2;

    private static double[] bias1;

    private static double[] bias2;

    private static int[][] inputs;

    @BeforeAll
    static void setUp() {
        int embeddingSize = 128;
        int innerSize = 2048;

        embeddings = NeuralUtils.buildEmbeddingList(embeddingSize);

        layer1 = NeuralUtils.buildTransposedLayer1(embeddingSize, innerSize);

        layer2 = NeuralUtils.buildTransposedLayer2(innerSize);

        bias1 = NeuralUtils.buildBias1(innerSize);

        bias2 = NeuralUtils.buildBias2();

        inputs = NeuralUtils.buildInputs(embeddings, 6);
    }

    protected abstract NeuralInference createInference(
        List<double[][]> embeddingList,
        double[][] layer1,
        double[][] layer2,
        double[] bias1,
        double[] bias2
    );

    @Test
    void test() {
        NeuralInference inference = createInference(embeddings, layer1, layer2, bias1, bias2);
        Assertions.assertNotNull(inference);

        int[][] inputs =  NeuralUtils.buildInputs(embeddings, 1);

        int[] input = inputs[0];

        double[] result = inference.compute(input);

        assertThat(result)
            .isNotNull()
            .hasSize(330)
            .usingComparatorWithPrecision(0.001E-8)
            .startsWith(
                1.1137818819945222E-11,
                9.942712073060735E-7,
                1.235867628537234E-6,
                1.2972402079168048E-6,
                1.3520661586529995E-6
            );

        assertThat(Doubles.max(result))
            .isCloseTo(1.000, Offset.offset(0.001));

        assertThat(Doubles.min(result))
            .isCloseTo(1.113E-11, Offset.offset(0.001E-11));

        assertThat(StatUtils.sum(result))
            .isCloseTo(66.346, Offset.offset(0.001));
    }

}
