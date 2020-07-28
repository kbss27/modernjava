package intro.quiz;

import intro.Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntroQuiz {
    public static void main(String[] args) {
        Main.menu.stream().filter(dish -> dish.getCalories() > 300).collect(Collectors.toList());
        Main.menu.stream().map(dish -> dish.getName()).sorted()
                .forEach(value -> System.out.println(value));

        Main.menu.stream().mapToInt(dish -> dish.getCalories());
        List<String> test = new ArrayList<>();
        test.add("b");
        test.add("a");
        test.add("c");

        test.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        }).forEach(t -> System.out.println(t));

    }
}
