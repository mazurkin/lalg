package org.github.mazurkin.benchmark.inference;

import org.github.mazurkin.domain.NeuralBlasInference;
import org.github.mazurkin.domain.NeuralInference;
import org.github.mazurkin.domain.NeuralJavaInference;
import org.github.mazurkin.domain.NeuralLalgInference;
import org.github.mazurkin.domain.NeuralUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(jvmArgsAppend = {
    // GC
    "-XX:+UseG1GC",
    "-XX:+UseGCOverheadLimit",
    "-XX:MaxGCPauseMillis=10",
    // memory
    "-Xms4096m",
    "-Xmx4096m",
    "-XX:+AlwaysPreTouch",
    "-XX:+UseCompressedOops",
})
public class NeuralBenchmark {

    private static final int INPUT_LIST_SIZE = 64 * 1024;

    @State(Scope.Benchmark)
    public static class Inferences {

        private NeuralInference javaInference;

        private NeuralInference blasInference;

        private NeuralInference lalgInference;

        private int[][] inputs;

        @Param({"1", "2", "4", "8"})
        @SuppressWarnings("unused")
        private int factor;

        @Setup(Level.Trial)
        public void setup() {
            // 4 is a "default" factor when the matrix sized are equal to the real sizes
            int embeddingSize = 128 * this.factor / 4;
            int innerSize = 2048 * this.factor / 4;

            List<double[][]> embeddingList = NeuralUtils.buildEmbeddingList(embeddingSize);

            double[][] transposedLayer1 = NeuralUtils.buildTransposedLayer1(embeddingSize, innerSize);

            double[][] transposedLayer2 = NeuralUtils.buildTransposedLayer2(innerSize);

            double[] bias1 = NeuralUtils.buildBias1(innerSize);

            double[] bias2 = NeuralUtils.buildBias2();

            this.javaInference = new NeuralJavaInference(embeddingList, transposedLayer1, transposedLayer2, bias1, bias2);
            this.blasInference = new NeuralBlasInference(embeddingList, transposedLayer1, transposedLayer2, bias1, bias2);
            this.lalgInference = new NeuralLalgInference(embeddingList, transposedLayer1, transposedLayer2, bias1, bias2);

            this.inputs = NeuralUtils.buildInputs(embeddingList, INPUT_LIST_SIZE);

            System.gc();
        }
    }

    @State(Scope.Thread)
    public static class Inputs {

        private int index;

        public Inputs() {
            this.index = 0;
        }

        public int[] next(int[][] array) {
            if (index >= array.length) {
                index = 0;
            }

            return array[index++];
        }
    }

    @Benchmark
    @SuppressWarnings("unused")
    public double[] testJavaInference(Inferences inferences, Inputs inputs) {
        int[] input = inputs.next(inferences.inputs);
        return inferences.javaInference.compute(input);
    }

    @Benchmark
    @SuppressWarnings("unused")
    public double[] testBlasInference(Inferences inferences, Inputs inputs) {
        int[] input = inputs.next(inferences.inputs);
        return inferences.blasInference.compute(input);
    }

    @Benchmark
    @SuppressWarnings("unused")
    public double[] testLalgInference(Inferences inferences, Inputs inputs) {
        int[] input = inputs.next(inferences.inputs);
        return inferences.lalgInference.compute(input);
    }
}
