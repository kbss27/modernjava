## DSL
* DSL은 범용 프로그래밍 언어가 아니다.
* DSL에서 동작과 용어는 특정 도메인에 국한

* DSL 장점?
    * 코드의 의도 명확히 전달
    * 가독성 좋음

#### DSL 카테고리
* 내부 DSL
    * 순수 자바코드 같은 기존 호스팅언어기반
    * 순수 자바로 DSL 구현하면 나머지 코드와 함께 컴파일 가능. 추가 비용 발생하지 않음.
* 외부 DSL
    * 새 언어 설계
    * ANTLR같은 자바 기반 파서 생성기를 이용하면 도움이 됨
    * 자바로 개발한 인프라구조 코드와 외부 DSL로 구현한 비즈니스 코드를 명확히 분리한다는 장점
    * 외부 DSL과 호스트 언어 사이에 인공 계층이 생기는 단점도 있으므로 양날의 검
* 다중 DSL
    * 스칼라나 그루비처럼 자바가 아니지만 JVM에서 실행되고 더 유연하고 표현력이 강한 언어는 다중 DSL
    * JVM 기반 프로그래밍 언어를 이용함으로써 DSL 합침 문제를 해결하는 방법
    * 자바와 호환성이 완벽하지 않을 때도 있음. 
        * ex) 스칼라와 자바 컬렉션은 서로 호환되지 않음. 상호 컬렉션을 전달하려면 기존 컬렉션을 대상 언어의 API에 맞게 변환해야함 

#### 최신 자바의 작은 DSL
**데이터의 정렬, 필터링, 변환, 그룹화에 유용한 작은 DSL들 제공**
* Comparator
* 컬렉션을 조작하는 DSL Stream
* 데이터를 수집하는 DSL Collectors

#### 자바로 DSL을 만드는 패턴과 기법
* 빌더패턴
* 람다 표현식을 이용한 함수 시퀀싱
~~~java
Order orer = order(o -> {
    o.forCustomer("BigBank");
    o.buy(t -> {
        t.quantity(80);
        t.price(125.00);
        t.stock(s -> {
            s.symbol("IBM");
            s.market("NYSE");
        });
    });
    o.sell(t -> {
        t.quantity(50);
        t.price(375.00);
        t.stock(s -> {
            s.symbol("GOOGLE");
            s.market("NASDAQ");
        });
    });
});
~~~

#### 실생활의 자바 8 DSL
* 메서드 체인
* 중첩 함수
* 람다를 이용한 함수 시퀀싱

위의 DSL 패턴을 조합해 새로운 DSL을 개발할 수 있음

