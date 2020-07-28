package intro.quiz;

import java.util.*;
import java.util.stream.Collectors;

public class StreamUsageTraderQuiz {

    private static Trader raoul = new Trader("Raoul", "Cambridge");
    private static Trader mario = new Trader("Mario", "Milan");
    private static Trader alan = new Trader("Alan", "Cambridge");
    private static Trader brian = new Trader("Brian", "Cambridge");

    private static List<Transaction> transactions = Arrays.asList(
            new Transaction(brian, 2011, 300),
            new Transaction(raoul, 2012, 1000),
            new Transaction(raoul, 2011, 400),
            new Transaction(mario, 2012, 710),
            new Transaction(mario, 2012, 700),
            new Transaction(alan, 2012, 950)
    );

    public static void main(String[] args) {
        // 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리
//        transactions.stream()
//                .filter(transaction -> transaction.getYear() == 2011)
//                .sorted((o1, o2) -> {
//                    if (o1.getValue() > o2.getValue()) return 1;
//                    else return -1;
//                })
//                .forEach(transaction -> System.out.println(transaction.toString()));

        transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .sorted(Comparator.comparing(Transaction::getValue))
                .forEach(transaction -> System.out.println(transaction.toString()));
        System.out.println();

        // 거래자가 근무하는 모든 도시를 중복 없이 나열
        transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .forEach(city -> System.out.println(city));

        System.out.println();
        //케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
        transactions.stream()
                .map(transaction -> transaction.getTrader())
                .filter(trader -> trader.getCity().equalsIgnoreCase("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(trader -> trader.getName()))
                .forEach(trader -> System.out.println(trader.toString()));

        System.out.println();
        //모든 거래자의 이름을 알파벳순으로 정렬해서 반환
        String result = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));

        result = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .reduce("", (a, b) -> a + b);

        System.out.println(result);
        System.out.println();

        //밀라노에 거래자가 있는가
        boolean milano = transactions.stream().anyMatch(transaction -> transaction.getTrader().getCity().equalsIgnoreCase("Milan"));
        System.out.println(milano);
        System.out.println();

        transactions.stream().filter(transaction -> transaction.getTrader().getCity().equalsIgnoreCase("Cambridge"))
                .map(transaction -> transaction.getValue()).forEach(value -> System.out.println(value));

        System.out.println();
        //전체 트랜잭션 중 최댓값은 얼마인가?
        OptionalInt maxInt = transactions.stream()
                .mapToInt(transaction -> transaction.getValue())
                .max();

        Optional<Integer> maxInt2 = transactions.stream().map(transaction -> transaction.getValue()).reduce(Integer::max);

        Optional<Transaction> maxTransaction = transactions.stream()
                .max(Comparator.comparing(transaction -> transaction.getValue()));

        System.out.println(maxInt.getAsInt());
        System.out.println(maxInt2.get());
        System.out.println(maxTransaction.get().getValue());
        System.out.println();

        //전체 트랜잭션 중 최솟값은 얼마인가?
        Optional<Integer> min = transactions.stream().map(Transaction::getValue).reduce(Integer::min);
        min = transactions.stream().map(Transaction::getValue).reduce((a, b) -> a < b ? a : b);
        System.out.println(min.get());


    }
}
