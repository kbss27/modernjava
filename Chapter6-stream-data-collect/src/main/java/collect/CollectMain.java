package collect;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectMain {
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

    public static List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550 , Dish.Type.OTHER),
            new Dish("prawns", false, 300 , Dish.Type.FISH),
            new Dish("salmon", false, 450 , Dish.Type.FISH)
    );

    public enum CaloricLevel { DIET, NORMAL, FAT }

    public static void main(String[] args) {
        int totalCalories = menu.stream().collect(Collectors.summingInt(dish -> dish.getCalories()));
        IntSummaryStatistics menuStatistics = menu.stream().collect(Collectors.summarizingInt(dish -> dish.getCalories()));
        System.out.println(totalCalories);

        String shortMenu = menu.stream().map(dish -> dish.getName()).collect(Collectors.joining());
        totalCalories = menu.stream().collect(Collectors.reducing(0, dish -> dish.getCalories(), (i, j) -> i + j));

        Map<Dish.Type, List<Dish>> dishedByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType()));

        Map<CaloricLevel, List<Dish>> dishedByCaloricLevel = menu.stream().collect(Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }));

        dishedByType = menu.stream().filter(dish -> dish.getCalories() > 500).collect(Collectors.groupingBy(dish -> dish.getType()));
        dishedByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.filtering(dish -> dish.getCalories() > 500, Collectors.toList())));

        Map<Dish.Type, List<String>> dishNamesByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.mapping(dish -> dish.getName(), Collectors.toList())));

        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        })));

        Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.maxBy(Comparator.comparingInt(dish -> dish.getCalories()))));
        Map<Dish.Type, Dish> mostCaloricByTypeWithoutOptional = menu.stream().collect(
                Collectors.groupingBy(dish -> dish.getType(), Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(dish -> dish.getCalories())),
                        dish -> dish.get())));

        Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(Collectors.partitioningBy(dish -> dish.isVegetarian()));
        Function.identity();
    }
}
