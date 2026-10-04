# [Prompt Template] Bug Fix & Troubleshooting

## 1. Context & Issue Summary
- Symptom / Exception: [예: 동시 요청 시 재고 정합성 깨짐 / OptimisticLockException 발생]
- Affected Service / Endpoint: [예: POST /api/v1/orders/cancel]
- Stacktrace & Error Log:
```text
[로그 또는 스택트레이스 붙여넣기]
```

## 2. Target Files
- [예: src/main/java/.../OrderService.java]
- [예: src/main/java/.../domain/Stock.java]

## 3. Engineering Requirements
- 에러의 근본 원인과 재현 조건을 3줄 이내로 명확히 분석할 것.
- 프로덕션 코드를 수정하기 전, 해당 이슈를 재현하고 실패하는 단위 테스트(@Test)를 먼저 작성할 것.
- 테스트 실패(Red)를 확인한 후, 최소한의 로직 수정으로 테스트를 통과(Green)시킬 것.

## 4. Constraints & Guardrails
- 기존 공통 모듈이나 다른 도메인 인터페이스의 시그니처를 임의로 변경하지 말 것.
- 단순 null 체크 남발을 지양하고, 도메인 무결성 검증 규칙 및 명시적 도메인 예외를 적용할 것.
- 수정 범위 외의 파일 포맷팅 변경을 지양할 것 (Diff 최소화).

## 5. Verification
- 실행 명령어: ./gradlew test --tests "[관련_테스트클래스명]"
- 위 명령어 실행 결과와 통과 로그를 최종 응답에 포함할 것.