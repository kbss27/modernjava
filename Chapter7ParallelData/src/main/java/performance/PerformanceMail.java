package performance;

import java.util.stream.Stream;

public class PerformanceMail {
    public static void main(String[] args) {
        System.out.println("ee");
    }

    public long sequentialSum(long n) {
        return Stream.iterate(1L, i-> i+1)
                .limit(n).reduce(0L, Long::sum);
    }
}
