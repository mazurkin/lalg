package org.github.mazurkin.domain;

import java.util.List;

class NeuralJavaInferenceDatasetTest extends AbstractNeuralInferenceDatasetTest {

    @Override
    protected NeuralInference createInference(List<double[][]> embeddingList, double[][] layer1, double[][] layer2, double[] bias1, double[] bias2) {
        return new NeuralJavaInference(embeddingList, layer1, layer2, bias1, bias2);
    }

}
