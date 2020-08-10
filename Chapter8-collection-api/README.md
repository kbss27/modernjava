# 8장 컬렉션 API 개선

## 8.1 컬렉션 팩토리

Java 9 이전
~~~java
List<String> friends = new ArrayList<>();
        friends.add("Raphael");
        friends.add("Olivia");
        friends.add("Thibaut");
List<String> friendList = Arrays.asList("Raphael", "Olivia", "Thibaut");
Set<String> friendSet = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
Set<String> friendStreamSet = Stream.of("Raphael", "Olivia", "Thibaut").collect(Collectors.toSet());
~~~
* 새 요소 갱신만 가능, 추가 삭제 불가
* 내부적으로 불필요한 객체할당 필요

Java 9 이후
~~~java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
~~~
* 불변 리스트 반환 (add, set 불가)

##### 오버로딩 vs 가변 인수

오버로딩
~~~java
static <E> List<E> of(E e1, E e2, E e3, E e4);
static <E> List<E> of(E e1, E e2, E e3, E e4, E e5);
~~~ 

가변인수
~~~java
static <E> List<E> of(E... elements);
~~~ 

왜 가변인수와 오버로딩 둘 다 있을까?  
내부적으로 가변 인수 버전은 추가 배열을 할당해서 리스트로 감싼다.  
따라서 배열을 할당, 초기화하며, 나중에 GC 비용까지 지불해야 한다.  
of의 오버로딩은 고정된 숫자 요소 (최대 10개)를 API정의하므로 이런 비용 제거 가능하다.  
10개가 넘어가면, 가변 인수를 사용하는 of 메소드가 사용된다. Set.of, Map.of도 마찬가지다.

##### 맵 팩토리

~~~java
Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
Map<String, Integer> ageOfFriendsByEntry = Map.ofEntries(Map.entry("Raphael", 30), Map.entry("Olivia", 25), Map.entry("Thibaut", 26));
~~~  

##### 리스트와 집합 처리
List, Set 인터페이스에 추가된 메서드
* removeIf : 프레디케이트를 만족하는 요소 제거
* replaceAll : 리스트 인터페이스에서 사용 가능, UnaryOperator 함수를 이용해 요소 변경
* sort : 리스트 인터페이스에서 사용 가능

##### 맵 처리
Key, Value를 인수로 받는 BiConsumer를 인수로 받는 forEach 지원
~~~java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is" + age + "years old"));
~~~

정렬
* Entry.comparingByValue
* Entry.comparingByKey

~~~java
favouriteMovies.entrySet().stream().sorted(Entry.comparingByKey()).forEachOrdered(System.out::println);
~~~

getOrDefault  
* key가 없거나 key가 있어도 value가 null일 경우 (어쨋든 반환값이 null)
~~~java
favouriteMovies.getOrDefault("Thibaut", "Matrix");
~~~

계산 패턴
* computeIfAbsent : 제공된 키에 해당하는 값이 없으면 (값이 없거나 null), 키를 이용해 새 값을 계산하고 맵에 추가  
* computeIfPresent : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가 (값을 만드는 함수가 null을 반환하면, 현재 key의 매핑을 맵에서 제거)
* compute : 제공된 키로 새 값을 계산하고 맵에 저장

삭제 패턴
~~~java
favouriteMovies.remove(key, value);
~~~

교체 패턴
* replaceAll : BiFunction을 적용한 결과로 각 항목의 값을 교체 (List의 replaceAll과 비슷한 동작 수행)
* Replace : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을때만 값을 교체하는 오버로드도 있음
~~~java
favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
~~~

합침
~~~java
Map<String, String> family = Map.ofEntries(Map.entry("Teo", "Star Wars"), Map.entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(Map.entry("Raphael", "Star Wars"), Map.entry("Cristina", "Matrix"));

Map<String, String> everyOne = new HashMap<>(family);
friends.forEach((key, value) -> everyOne.merge(key, value, (oldValue, newValue) -> oldValue + " & " + newValue));
~~~

##### 개선된 ConcurrentHashMap

세 가지 새로운 연산 지원
* forEach : 각 (키, 값) 쌍에 주어진 액션 실행
* reduce : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
* search : null이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용

네 가지 연산 형태 지원
* 키, 값으로 연산 (forEach, reduce, search)
* 키로 연산 (forEachKey, reduceKeys, searchKeys)
* 값으로 연산 (forEachValue, reduceValues, searchValues)
* Map.Entry 객체로 연산 (forEachEntry, reduceEntries, searchEntries)

ConcurrentHashMap의 상태를 잠그지 않고 연산 수행하기 때문에, mutable한 객체, 값, 순서 등에 의존하지 않아야함  

병렬성 기준값(threshold) 지정해야 함
* 맵의 크기가 주어진 기준값보다 작으면 순차적으로 실행
* 기존값을 1로 지정하면 공통 스레드 풀을 이용해 병렬성 극대화
* Long.MAX_VALUE를 기준값으로 설정하면 한개의 스레드로 연산 수행

int, long, double 등의 primitive타입에는 전용 each reduce 연산이 제공됨 (reduceValuesToInt, reduceKeysToLong... etc)  

계수
* 맵의 매핑 개수를 반환하는 mappingCount 메서드 제공
    * 기존의 size대신 이점은 mappingCount는 int를 제공하기 때문에 매핑의 개수가 int 범위 넘어서는 이후 상황 대처 가능
    
집합뷰
* ConcurrentHashmap을 집합 뷰로 반환 하는 keySet 메서드 제공
    * ConcurrentHashMap과 keySet으로 만들어진 set은 서로 영향을 받음
    * newKeySet을 사용하면 ConcurrentHashMap으로 유지되는 집합 만들 수 있음
~~~java
// Pre-Java-8 way to create a concurrent set
Set<String> oldStyle = Collections.newSetFromMap(new ConcurrentHashMap<>());
// New method in Java 8
Set<String> newStyle = ConcurrentHashMap.newKeySet();
~~~