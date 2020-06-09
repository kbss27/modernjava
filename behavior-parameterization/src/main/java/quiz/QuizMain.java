package quiz;

import before.Color;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class QuizMain {
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(80, Color.GREEN), new Apple(155, Color.GREEN), new Apple(120, Color.RED));
        prettyPrintApple(inventory, apple -> apple.getColor().toString());
        prettyPrintApple(inventory, apple -> String.valueOf(apple.getWeight()));
        prettyPrintApple(inventory, apple -> {
            if (apple.getWeight() > 150) {
                return "heavy";
            }
            return "light";
        });
    }

    public static void prettyPrintApple(List<Apple> inventory, Function<Apple, String> p) {
        for (Apple apple : inventory) {
            String output = p.apply(apple);
            System.out.println(output);
        }
    }
}
