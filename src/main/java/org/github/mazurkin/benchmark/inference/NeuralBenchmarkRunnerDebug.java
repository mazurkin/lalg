package org.github.mazurkin.benchmark.inference;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.PausesProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

public class NeuralBenchmarkRunnerDebug {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(NeuralBenchmark.class.getSimpleName())
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .forks(0)
            .threads(1)
            .warmupIterations(0)
            .warmupTime(TimeValue.seconds(5))
            .measurementIterations(1)
            .measurementTime(TimeValue.seconds(10))
            .addProfiler(GCProfiler.class)
            .addProfiler(PausesProfiler.class, "period=1000;threshold=1000")
            .verbosity(VerboseMode.EXTRA)
            .build();

        Runner runner = new Runner(opt);
        runner.run();
    }
}
