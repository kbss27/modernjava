package intro.quiz;

import intro.Dish;
import intro.Main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static intro.Main.menu;

public class StreamUsageQuiz {
    public static void main(String[] args) {
        // 처음 등장하는 두 고기요리 필터링
        List<Dish> dishes = menu.stream().filter(dish -> dish.getType() == Dish.Type.MEAT).limit(2).collect(Collectors.toList());
        dishes.forEach(dish -> System.out.println(dish.toString()));

        // 각 숫자의 제곱근으로 이루어진 리스트를 반환 [1,2,3,4,5]
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);
        numbers.stream().map(i -> i*i).collect(Collectors.toList()).forEach(i -> System.out.println(i));

        // 두 개의 숫자 리스트가 있을때 모든 숫자 쌍의 리스트 반환 [1,2,3], [3,4]
        List<Integer> numbers1 = Arrays.asList(1,2,3);
        List<Integer> numbers2 = Arrays.asList(3,4);

        numbers1.stream()
                .flatMap(i -> numbers2.stream().map(j -> new int[] {i, j}))
                .forEach(s -> System.out.println(s[0] + "," + s[1]));

        System.out.println();
        // 이전예제에서 합이 3으로 나누어 떨어지는 쌍만 반환 (2,4), (3,3)
        numbers1.stream()
                .flatMap(i -> numbers2.stream().filter(j -> (i + j) % 3 == 0).map(j -> new int[] {i, j}))
                .forEach(s -> System.out.println(s[0] + "," + s[1]));

        System.out.println();
        //map과 reduce 메서드를 이용해서 스트림의 요리 개수를 계산하시오.
//        int count = menu.stream().reduce(0, Integer::sum);
        int count = menu.stream().map(dish -> 1).reduce(0, Integer::sum);
        System.out.println(count);
    }
}
