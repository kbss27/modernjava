package intro.quiz;

import intro.Main;

import java.util.stream.Collectors;

public class IntroQuiz {
    public static void main(String[] args) {
        Main.menu.stream().filter(dish -> dish.getCalories() > 300).collect(Collectors.toList());
    }
}
