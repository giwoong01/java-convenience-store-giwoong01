# java-convenience-store-precourse

## 프로젝트 소개

구매자의 할인 혜택과 재고 상황을 고려하여 최종 결제 금액을 계산하고 안내하는 결제 시스템을 구현합니다.

## 4주 차 목표

- 편의점 구현 및 테스트 코드 작성
- 3주차 공통 피드백 적용
- 최종 코딩 테스트를 위한 5시간 연습
- 의미 있는 메소드 이름 적용
- 상수를 의미 있는 이름으로 적용
- 객체에 알맞은 역할 부여
- 회고

---

## 구현할 기능 목록

- [ ] 사용자
    - [x] 구매할 상품명과 수량 입력한다.
    - [ ] 프로모션 적용 가능한 상품에 대해 고객이 수량만큼 가져오지 않았을 경우, 혜택에 대한 유무(Y/N)를 입력한다.
    - [ ] 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 함에 대한 유무(Y/N)를 입력한다.
    - [ ] 멤버십 할인 적용에 대한 유무(Y/N)를 입력한다.
    - [ ] 추가로 구매할 상품이 있는지에 대한 유무(Y/N)를 입력한다.
- [x] 상품 재고 관리
- [ ] 결제 시스템
    - [ ] 프로모션 할인을 관리한다.
    - [ ] 멤버십 할인을 관리한다.
- [ ] 영수증 발행기
    - [ ] 영수증을 발행한다.

## 출력

- [x] 편의점 소개 메시지와 보유하고 있는 상품 출력
- [x] 구매할 상품명과 수량 입력 메시지
- [ ] 프로모션 적용 가능한 상품에 대해 고객에 수량만큼 가져오지 않았을 경우, 혜택에 대한 유무(Y/N) 안내 메시지
- [ ] 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 함에 대한 유무(Y/N) 안내 메시지
- [ ] 멤버십 할인 적용 유무(Y/N) 안내 메시지
- [ ] 영수증 출력

## 예외 상황

- [x] 사용자가 구매할 상품과 수량 형식을 올바르지 않게 입력하는 경우
- [x] 사용자가 존재하지 않는 상품을 입력하는 경우
- [x] 사용자가 입력한 구매 수량이 재고 수량을 초과한 경우
- [ ] 사용자가 여부를 묻는 메시지에 Y 또는 N을 제외한 다른 값을 입력한 경우
