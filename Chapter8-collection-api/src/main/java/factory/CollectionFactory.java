package factory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionFactory {
    public static void main(String[] args) {
        mapMerge();
    }

    public void makeCollection() {
        List<String> friends = new ArrayList<>();
        friends.add("Raphael");
        friends.add("Olivia");
        friends.add("Thibaut");

        List<String> friends2 = Arrays.asList("Raphael", "Olivia", "Thibaut");

        Set<String> friendSet = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
        Set<String> friendStreamSet = Stream.of("Raphael", "Olivia", "Thibaut").collect(Collectors.toSet());
    }

    public void makeCollectionByOf() {
        List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
    }

    public void makeCollectionMap() {
        Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
        Map<String, Integer> ageOfFriends2 = Map.ofEntries(Map.entry("Raphael", 30), Map.entry("Olivia", 25), Map.entry("Thibaut", 26));
        Map<String, String> favouriteMovies = new HashMap<>();
        favouriteMovies.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(System.out::println);
        favouriteMovies.getOrDefault("Thibaut", "Matrix");
    }

    public static void mapMerge() {
        Map<String, String> family = Map.ofEntries(Map.entry("Teo", "Star Wars"), Map.entry("Cristina", "James Bond"));
        Map<String, String> friends = Map.ofEntries(Map.entry("Raphael", "Star Wars"), Map.entry("Cristina", "Matrix"));

        Map<String, String> everyOne = new HashMap<>(family);
        friends.forEach((key, value) -> everyOne.merge(key, value, (oldValue, newValue) -> oldValue + " & " + newValue));
        System.out.println(everyOne);

        Map<String, Long> movieToCount = new HashMap<>();
        for (Map.Entry entry : everyOne.entrySet()) {
            movieToCount.merge((String)entry.getKey(), 1L, (oldValue, newValue) -> oldValue + newValue);
        }
    }

    public static void mapMerge2() {
        ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.keySet();
        concurrentHashMap.newKeySet();

    }
}
