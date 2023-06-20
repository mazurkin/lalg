package org.github.mazurkin.benchmark.search;

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
public class DistanceBenchmark {

    @State(Scope.Benchmark)
    public static class Samples {

        private double[][] vectors;

        private double[][] candidates;

        @Param({"128", "2048", "16384"})
        @SuppressWarnings("unused")
        private int vectorCount;

        @Param({"330", "1024"})
        @SuppressWarnings("unused")
        private int vectorSize;

        @Setup(Level.Trial)
        public void setup() {
            this.vectors = NeuralUtils.randomArray(vectorCount, vectorSize, 0xCAFE_01);

            this.candidates = NeuralUtils.randomArray(64 * 1024, vectorSize, 0xCAFE_02);

            System.gc();
        }
    }

    @State(Scope.Thread)
    public static class Index {

        private int index;

        public Index() {
            this.index = 0;
        }

        public double[] next(double[][] array) {
            if (index >= array.length) {
                index = 0;
            }

            return array[index++];
        }
    }

    @Benchmark
    @SuppressWarnings("unused")
    public int test(Samples samples, Index index) {
        // pick the next vector from the array
        final double[] candidate = index.next(samples.candidates);

        // search for the minimal distance
        double minVal = Double.MAX_VALUE;
        int minIdx = -1;

        // simple linear search
        for (int i = 0, vectorCount = samples.vectorCount; i < vectorCount; i++) {
            double[] vector = samples.vectors[i];

            double distance = 0.0;
            for (int j = 0, vectorSize = samples.vectorSize; j < vectorSize; j++) {
                double d = vector[j] - candidate[j];
                distance += d * d;
            }

            if (distance < minVal) {
                minVal = distance;
                minIdx = i;
            }
        }

        return minIdx;
    }


}
