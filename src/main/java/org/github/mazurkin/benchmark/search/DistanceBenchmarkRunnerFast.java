package org.github.mazurkin.benchmark.search;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.PausesProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

public class DistanceBenchmarkRunnerFast {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(DistanceBenchmark.class.getSimpleName())
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .forks(1)
            .threads(4)
            .warmupIterations(1)
            .warmupTime(TimeValue.minutes(1))
            .measurementIterations(1)
            .measurementTime(TimeValue.minutes(2))
            .addProfiler(GCProfiler.class)
            .addProfiler(PausesProfiler.class, "period=1000;threshold=1000")
            .verbosity(VerboseMode.EXTRA)
            .build();

        Runner runner = new Runner(opt);
        runner.run();
    }
}
