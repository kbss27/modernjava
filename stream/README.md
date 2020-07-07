# 4장 Stream 소개

## 4.1 Stream이란 무엇인가?
* 선언형(SQL문과 같이 질의로 표현)으로 컬렉션 데이터를 처리할 수 있다.

Stream API 특징
* 선언형 : 더 간결하고 가독성이 좋아진다.
* 조립할 수 있음 : 유연성이 좋아진다.
* 병렬화 : 성능이 좋아진다.

## 4.2 Stream 시작하기

> "스트림이란 데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소"  

* 연속된 요소 : collection의 주제는 데이터이고, stream의 주제는 계산이다.
    * Collection : 자료구조 이므로 시간과 공간의 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이룬다. (ex. ArrayList, LinkedList 중 어느것을 사용할 것인지)
    * Stream : filter, sorted, map처럼 표현 계산식이 주를 이룬다.
* 소스 : stream은 collection, array, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다.
    * 정렬된 collection으로 stream을 생성하면 정렬이 그대로 유지된다. 즉, stream의 요소와 collection의 요소는 같은 순서를 유지한다.
* 데이터 처리 연산
    * 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산이나 데이터베이스 연산과 비슷한 연산을 지원한다. (filter, map, reduce, find, match, sort 등)
    * 순차적으로 또는 병렬적으로 실행할 수 있다.
* 파이프라이닝 : 대부분의 스트림 연산은 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다. 그 덕분에 laziness, short-circuiting 같은 최적화도 얻을 수 있다.
* 내부 반복 : 컬렉션과 달리 스트림은 내부 반복을 지원한다.

## 4.3 스트림과 컬렉션
    
컬렉션과 스트림 모두 연속된 요소 형식의 값을 저장하는 자료구조와 인터페이스 제공
데이터를 언제 계산하느냐의 차이
* collection : collection의 모든 요소는 collection에 추가되기 전에 계산 되어야 한다. 컬레션에 요소를 추가하거나 삭제할때마다 컬렉션의 모든 요소를 메모리에 저장해야 하고, 추가하려는 요소는 미리 계산되어야 한다.
* stream : 요청할 때만 요소를 계산 (stream에 요소를 추가하거나 제거할 수 없다) 

딱 한번만 탐색 할 수 있다.
* collection과 마찬가지로 stream도 한번 탐색한 요소를 다시 탐색하려면 데이터 소스에서 새로운 스트림을 만들어야 한다.

외부 반복과 내부 반복
* collection은 사용자가 직접 요소를 반복 (ex. for-each), 이를 외부 반복이라 한다.
* Stream 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해주는 내부반복을 사용한다. 어떤 작업을 수행할지만 정의해놓으면 내부적으로 stream 라이브러리 내에서 처리한다.
* stream 라이브러리는 데이터 표현과 하드웨어를 활용한 병렬성 구현을 자동으로 선택하지만, collection은 병렬성을 스스로 관리해야 한다.
~~~java
List<String> names = new ArrayList<String>();
        for (intro.Dish dish : menu) {
            names.add(dish.getName());
        }
~~~

~~~java
List<String> names = menu.stream()
                .map(Dish::getName)
                .collect(Collectors.toList());
~~~

## 4.4 스트림 연산

* 중간 연산 : Stream을 반환하여 연산끼리 연결하며 파이프라인을 구성하게 해주는 연산
    * 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리
* 최종 연산 : Stream을 닫고 Stream 이외의 결과를 도출하는 연산

~~~java
List<String> names = menu.stream().filter(dish -> {
            System.out.println("filtering:" + dish.getName());
            return dish.getCalories() > 300;
        }).map(dish -> {
            System.out.println("mapping:" + dish.getName());
            return dish.getName();
        }).limit(3).collect(Collectors.toList());
~~~
limit연산과 쇼트서킷 (300 칼로리 넘는 요리는 여러개지만 오직 처음 3개만 선택)
루프 퓨전 (filter와 map은 서로 다른 연산이지만 한 과정으로 병합)
```
filtering:pork
mapping:pork
filtering:beef
mapping:beef
filtering:chicken
mapping:chicken
[pork, beef, chicken]
```  
## 4.4 스트림 이용하기

* 질의를 수행할 데이터 소스
* 스트림 파이프라인을 구성할 중간 연산 연결
* 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

호출을 연결해서 설정을 만든다는 방식의 빌더 패턴과 유사
* 빌더 패턴 : build하고자 하는 요소들의 호출을 연결 후, build 호출
* 스트림 : 연산을 연결 (중간연산) 한 후, 최종 연산


