package org.github.mazurkin.domain;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.List;

@EnabledOnOs(value = OS.LINUX, disabledReason = "the test requires the native binary BLAS and Fortran libraries")
class NeuralBlasInferenceDatasetTest extends AbstractNeuralInferenceDatasetTest {

    @Override
    protected NeuralInference createInference(List<double[][]> embeddingList, double[][] layer1, double[][] layer2, double[] bias1, double[] bias2) {
        return new NeuralBlasInference(embeddingList, layer1, layer2, bias1, bias2);
    }

}
