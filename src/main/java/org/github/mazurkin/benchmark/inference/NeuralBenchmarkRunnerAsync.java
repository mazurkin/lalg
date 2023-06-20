package org.github.mazurkin.benchmark.inference;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

public class NeuralBenchmarkRunnerAsync {

    public static void main(String[] args) throws RunnerException {
        String asyncProfilerConfiguration = String.format(
            "dir=%s;alluser=true;event=cpu;output=text,jfr",
            FileUtils.getTempDirectoryPath()
        );

        Options opt = new OptionsBuilder()
            .include(NeuralBenchmark.class.getSimpleName())
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .forks(1)
            .threads(1)
            .warmupIterations(1)
            .warmupTime(TimeValue.seconds(10))
            .measurementIterations(1)
            .measurementTime(TimeValue.seconds(60))
            .threads(1)
            .addProfiler(AsyncProfiler.class, asyncProfilerConfiguration)
            .verbosity(VerboseMode.EXTRA)
            .build();

        Runner runner = new Runner(opt);
        runner.run();
    }
}
