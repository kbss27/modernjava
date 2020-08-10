package benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
public class ParallelStreamBenchmark {
    private static final long N = 1000L;

    @Benchmark
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i+1).limit(N).reduce(0L, (a, b) -> a+b);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }
}
