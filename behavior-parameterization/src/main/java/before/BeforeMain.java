package before;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BeforeMain {
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(80, Color.GREEN), new Apple(155, Color.GREEN), new Apple(120, Color.RED));
        List<Apple> heavyApples = filterApples(inventory, new AppleHeavyWeightPredicate());
        List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());
        heavyApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return apple.getWeight() > 150;
            }
        });

        greenApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return Color.GREEN.equals(apple.getColor());
            }
        });
        heavyApples = filterApples(inventory, apple -> apple.getWeight() > 150);
        greenApples = filterApples(inventory, apple -> Color.GREEN.equals(apple.getColor()));

        heavyApples = filter(inventory, apple -> apple.getWeight() > 150);
    }

    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        return inventory.stream()
                .filter(apple -> p.test(apple))
                .collect(Collectors.toList());
    }

    public static <T> List<T> filter(List<T> inventory, Predicate<T> p) {
        return inventory.stream()
                .filter(t -> p.test(t))
                .collect(Collectors.toList());
    }
}
