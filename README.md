# Fast linear algebra library with zero allocation

    make docker-build docker-run

# benchmark

    Benchmark                                                    (factor)    Mode     Cnt      Score   Error   Units

    NeuralBenchmark.testBlasInference                                   8  sample    1820    131.729 ± 1.928   ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.00           8  sample             94.896           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.50           8  sample            124.060           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.90           8  sample            168.559           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.95           8  sample            188.468           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.99           8  sample            223.968           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.999          8  sample            278.717           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p0.9999         8  sample            282.591           ms/op
    NeuralBenchmark.testBlasInference:testBlasInference·p1.00           8  sample            282.591           ms/op

    NeuralBenchmark.testLalgInference                                   8  sample   11153     21.498 ± 0.159   ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.00           8  sample             15.614           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.50           8  sample             19.726           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.90           8  sample             27.184           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.95           8  sample             33.771           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.99           8  sample             42.664           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.999          8  sample             64.729           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p0.9999         8  sample             84.783           ms/op
    NeuralBenchmark.testLalgInference:testLalgInference·p1.00           8  sample             84.935           ms/op
