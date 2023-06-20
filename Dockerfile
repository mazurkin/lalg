FROM eclipse-temurin:17.0.4.1_1-jdk-focal

# packages
RUN apt-get update \
 && apt-get install -y libgfortran5 \
 && rm -rf /var/lib/apt/lists/*

# async-profiler
# https://github.com/jvm-profiling-tools/async-profiler
RUN mkdir -p '/opt/asyncprofiler' \
 && mkdir -p '/usr/java/packages/lib' \
 && wget -qc -O - 'https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.9/async-profiler-2.9-linux-x64.tar.gz' | tar xvz -C "/opt/asyncprofiler" \
 && ln -sfT '/opt/asyncprofiler/async-profiler-2.9-linux-x64' '/opt/asyncprofiler/default' \
 && ln -sfT '/opt/asyncprofiler/default/build/libasyncProfiler.so' '/usr/java/packages/lib/libasyncProfiler.so'

# benchmark
COPY target/benchmarks.jar /opt/lalg/

# volumes
VOLUME ["/tmp"]

# application
WORKDIR /opt/lalg/
ENTRYPOINT ["/opt/java/openjdk/bin/java"]
CMD ["-cp", "benchmarks.jar", "org.winrateperf.benchmark.inference.NeuralBenchmarkRunnerFull"]
