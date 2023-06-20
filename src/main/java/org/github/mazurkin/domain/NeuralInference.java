package org.github.mazurkin.domain;

/**
 * Abstraction for neural network inference
 */
public interface NeuralInference {

    /**
     * Calculate output
     *
     * @param input Input values
     *
     * @return Output values. If you need to save this array you <em>must</em> make a copy
     * as it could be a reference to the mutable internal per-thread buffer
     */
    double[] compute(int[] input);

}
