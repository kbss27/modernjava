# 6장 Stream으로 데이터 수집

## 6.1 Collector란 무엇인가?
* collect 메소드로 Collector 인터페이스 구현을 전달

* Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정   
ex) collect(Collectors.groupingBy()), collect(Collectors.toList())

* 스트림에 collect호출하면 스트림의 요소에 컬렉터로 파라미터화된 리듀싱 연산이 수행됨
* Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림에 어떤 리듀싱 연산을 수행할지 결정

* Collectors에서 제공하는 메서드의 기능은 크게 세 가지로 구분
    * 스트림 요소를 하나의 값으로 리듀스하고 요약 (ex. toList())
    * 요소 그룹화 (ex. groupingBy())
    * 요소 분할 (ex. 프레디케이트를 그룹화 함수로 사용)

## 6.2 리듀싱과 요약

요약 연산 :최댓값이나 최소값을 구하거나 숫자 필드의 합계나 평균 등을 반환하는 연산
~~~java
//calory type int
int totalCalories = menu.stream().collect(Collectors.summingInt(dish -> dish.getCalories()));
//calory type long
long totalCalories = menu.stream().collect(Collectors.summingLong(dish -> dish.getCalories()));
//calory type double
long totalCalories = menu.stream().collect(Collectors.summingDouble(dish -> dish.getCalories()));
~~~

두 개 이상의 요약 연산을 한번에 수행해야 할때
summing연산과 마찬가지로 Long, Double도 지원
~~~java
IntSummaryStatistics menuStatistics = menu.stream().collect(Collectors.summarizingInt(dish -> dish.getCalories()));
~~~

문자열 연결
~~~java
String shortMenu = menu.stream().map(dish -> dish.getName()).collect(Collectors.joining());
String shortMenu = menu.stream().map(dish -> dish.getName()).collect(Collectors.joining(", "));
~~~

Collectors.reducing 팩토리 메서드가 제공하는 범용 리듀싱 컬렉터로도 지금까지 살펴본 모든 리듀싱을 재현할 수 있음
~~~java
int totalCalories = menu.stream().collect(Collectors.reducing(0, dish -> dish.getCalories(), (i, j) -> i + j));
~~~

## 6.3 그룹화

분류 함수 (classfication function) Dish.Type 기준으로 분류 (dish -> dish.getType()) 가 분류 함수
~~~java
Map<Dish.Type, List<Dish>> dishedByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType()));
~~~

400 칼로리 이하는 diet, 400~ 700 칼로리는 normal, 700 이상 칼로리는 fat으로 분류
~~~java
Map<CaloricLevel, List<Dish>> dishedByCaloricLevel = menu.stream().collect(Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }));
~~~

* 그룹화된 요소 조작

아래와 같이 필터링하고 그룹화하면 predicate조건에 맞지않는 타입은 키값조차 가질 수 없음  
아래 예제에서는 FISH type의 모든 menu가 500 calory를 넘지 않기 때문에 Map에서 제외됨
~~~java
Map<Dish.Type, List<Dish>> dishedByType = menu.stream().filter(dish -> dish.getCalories() > 500).collect(Collectors.groupingBy(dish -> dish.getType()));
~~~

아래와 같이 분류함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy 팩토리 메서드를 오버로드해 이 문제를 해결
~~~java
Map<Dish.Type, List<Dish>> dishedByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.filtering(dish -> dish.getCalories() > 500, Collectors.toList())));
Map<Dish.Type, List<String>> dishNamesByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.mapping(dish -> dish.getName(), Collectors.toList())));
~~~

* 다수준 그룹화
~~~java
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        })));
~~~

> groupingBy의 연산을 bucket 개념으로 생각하면 쉽다. 첫 번째 groupingBy는 각 키의 bucket을 만든다. 그리고 준비된 각 bucket을 substream collector로 채워가기를 반복하면서 n수준 그룹화를 달성한다.

~~~java
Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.maxBy(Comparator.comparingInt(dish -> dish.getCalories()))));

Map<Dish.Type, Dish> mostCaloricByTypeWithoutOptional = menu.stream().collect(
                Collectors.groupingBy(dish -> dish.getType(), Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(dish -> dish.getCalories())),
                        dish -> dish.get())));
~~~

collectingAndThen : 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다.


* 분할 : 분할 함수라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능
    * 분할 함수는 불리언을 반환하므로 맵의 키형식은 Boolean
    * 결과적으로 두 개의 그룹으로 분리됨 (true, false)
    * true, false 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점
~~~java    
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(Collectors.partitioningBy(dish -> dish.isVegetarian()));
~~~

## 6.5 Collector 인터페이스

~~~java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    Function<A, R> finisher();
    BinaryOperator<A> combiner();
    Set<Characteristics> characteristics();
}
~~~

* T는 수집될 항목의 제네릭 형식
* A는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체 형식
* R은 수집 연산 결과 객체의 형식 (대부분 컬렉션)

Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T> Class 정의
~~~java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
~~~

#### supplier
* 새로운 결과 컨테이너 만들기
    * 빈 결과로 이루어진 Supplier를 반환해야 함
    * 수집과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수
    * ToListCollector처럼 누적자를 반환하는 컬렉션에서는 빈 누적자가 비어있는 스트림의 수집 과정의 결과가 될 수 있음

ToListCollector에서 supplier
~~~java
public Supplier<List<T>> supplier() {
    return () -> new ArrayList<T>();
}
~~~

#### accumulator 
* 결과 컨테이너에 요소 추가
    * 리듀싱 연산을 수행하는 함수
    * 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없음
    
ToListCollector에서 accumulator : 이미 탐색한 항목을 포함하는 리스트에 현재 항목을 추가
~~~java
public BiConsumer<List<T>, T> accumulator() {
    return (list, item) -> list.add(item);
}
~~~

#### finisher
* 최종 변환값을 결과 컨테이너로 적용
    * 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환

ToListCollector에서는 누적자 객체가 이미 최종 결과이기 떄문에 변환과정이 필요하지 않으므로 finisher 메서드는 항등 함수를 반환
~~~java
public Function<List<T>, List<T>> finisher() {
    return Function.identity();
}


* Returns a function that always returns its input argument.
*
* @param <T> the type of the input and output objects to the function
* @return a function that always returns its input argument
*/
static <T> Function<T, T> identity() {
    return t -> t;
}
~~~

#### Combiner
* 두 결과 컨테이너 병합
    * 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의
    
ToListCollector에서는 스트림의 두 번째 서브 파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가
~~~java
public BinaryOperator<List<T>> combiner() {
    return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
    }
}
~~~

병렬화 리듀싱 과정 순서
* 스트림을 분할해야하는 조건이 거짓이 되기전까지 분할 
    * 분산작업의 크기가 너무 작아지면 병렬 수행 속도는 순차 수행보다 느릴 수 있음 
    * 프로세싱 코어의 개수를 초과하는 병렬작업은 효율적이지 않음
* 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리
* 마지막에 컬렉터의 combiner 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합침
    * 분할된 모든 서브스트림의 결과를 합치면서 연산 완료
    
#### Characteristics
* 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환
    * 스트림을 병렬로 리듀스 할것인지, 한다면, 어떤 최적화를 선택해야 할지 힌트 제공
    
Characteristics Enum
* UNORDERED : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않음
* CONCURRENT : 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며, 이 컬렉터는 스트림의 병렬 리듀싱 수행 가능  
UNORDERED를 함께 설정하지 않았다면 정렬되어 있지 않은, 순서가 무의미한 상황에서만 병렬 리듀싱 수행 가능
* IDENTITY_FINISH : finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략 가능  
리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용 가능, 또한 누적자 A를 결과 R로 안전하게 형변환 가능

ToListCollector  
* 누적자 값 자체가 최종값이므로 IDENTITY_FINISH
* 리스트의 순서는 상관 없으므로 UNORDERED
* CONCURRENT. 요소의 순서가 무의미한 데이터 소스여야 병렬로 실행 가능

## 6.6 커스텀 컬렉터를 구현해서 성능 개선

Collector 클래스 시그니처 정의
~~~java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    Function<A, R> finisher();
    BinaryOperator<A> combiner();
    Set<Characteristics> characteristics();
}
~~~

위의 interface에 맞춰 primeNumbersCollectors 구현

~~~java
public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    /**
     * 누적자로 사용할 map을 만들면서, true, false key와 빈 리스트로 초기화
     * @return
     */
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>() {
            {
                put(true, new ArrayList<>());
                put(false, new ArrayList<>());
            }
        };
    }

    /**
     * isPrime결과에 따라 소수리스트 비소수 리스트를 만듦
     * @return
     */
    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate)).add(candidate);
        };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    /**
     * 실제로 병렬로 사용할 순 없지만, 학습 목적으로 구현
     * @return
     */
    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
            return map1;
        };
    }

    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(java.util.stream.Collector.Characteristics.IDENTITY_FINISH));
    }

    private static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
                .takeWhile(i -> i <= candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }
}
~~~

##### Collectors 클래스의 정적 팩토리 메서드  

| *팩토리 메서드* | *반환 형식* | *사용 예제* | *설명* |
|:--------:|:--------:|:--------|:--------|
| toList				| List<T>					| List<Dish> dishes = menuStream.collect(toList());														| 스트림의 모든 항목을 리스트로 수집 |
| toSet					| Set<T>					| Set<Dish> dishes = menuStream.collect(toSet());														| 스트림의 모든 항목을 중복없는 집합으로 수집 |
| toCollection			| Collection<T>				| Collection<Dish> dishes = menuStream.collect(toCollection(), ArrayList::new);							| 스트림의 모든 항목을 공급자가 제공하는 컬렉션으로 수집 |
| counting				| Long						| long howManyDishes = menuStream.collect(counting());													| 스트림의 항목 수 계산 |
| summingInt			| Integer					| int totalCalories = menuStream.collect(summingInt(Dish::getCalories));								| 스트림의 항목에서 정수 프로퍼티 값을 더함 |
| averagingInt			| Double					| double avgCalories = menuStream.collect(averagingInt(Dish::getCalories));								| 스트림 항목의 정수 프로퍼티의 평균값 계산 |
| summarizingInt		| IntSummaryStatistics		| IntSummaryStatistics summary = menuStream.collect(summarizingInt(Dish::getCalories));					| 스트림 내의 항목의 최대, 최소, 합계, 평균 등의 정수 정보 통계를 수집 |
| joining				| String					| String shortMenu = menuStream.map(Dish::getName).collect(joining(“, “));								| 스트림의 각 항목에 toString 메서드를 호출한 결과 문자열을 연결 |
| maxBy					| Optional<T>				| Optional<Dish> fattest = menuStream.collect(maxBy(comparingInt(Dish::getCalories)));					| 주어진 비교자를 이용해서 스트림의 최대값 요소를 Optional로 감싼 값을 반환. 스트림에 요소가 없는경우 Optional.empty() 반환 |
| minBy					| Optional<T>				| Optional<Dish> lightest = menuStream.collect(minBy(comparingInt(Dish::getCalories)));					| 주어진 비교자를 이요해서 스트림의 최소값 요소를 Optional로 감싼 값을 반환. 스트림에 요소가 없는경우 Optional.empty() 반환 |
| reducing				| 리듀싱 연산에서 형식을 결정		| int totalCalories = menuStream.collect(reducing(0, Dish::getCalories, Integer::sum));					| 누적자를 초깃값으로 설정한 다음 BinaryOperator로 스트림의 각 요소를 반복적으로 누적자와 합쳐 스트림을 하나의 값으로 리듀싱 |
| collectingAndThen		| 변환함수가 형식을 반환			| int howManyDishes = menuStream.collect(collectingAndThen(toList(), List::size));						| 다른 컬렉터를 감싸고 그 결과에 변환 함수를 적용 |
| groupingBy			| Map<K, List<T>>			| Map<Dish.Type, List<Dish>> dishesByType = menuStream.collect(groupingBy(Dish::getType), toList()); 	| 하나의 프로퍼티값을 기준으로 스트림의 항목을 그룹화하며 기준 프로퍼티값을 결과 맵의 키로 사용 |
| partitioningBy		| Map<Boolean, List<T>>		| Map<Boolean, List<Dish>> vegetarianDishes = menuStream.collect(partitioningBy(Dish::isVegetarian));	| 프레디케이트를 스트림의 각 항목에 적용한 결과로 항목을 분할 |