# 구독 + 게시글 발행 알림 기능 설계

아직 확정 전 논의 정리. 이슈/브랜치 생성 전 단계.

## 관련 기존 코드

대부분 빈 스텁이다.

- `blog-api/.../subscription/Subscription.kt`, `SubscriptionRepository.kt`, `SubscriptionService.kt`
- `blog-api/.../notification/Notification.kt`, `NotificationRepository.kt`, `NotificationService.kt`, `NotificationType.kt`, `RelatedEntityType.kt`
- `blog-api/.../post/domain/Post.kt`, `PostInternalService.kt`, `PostFacade.kt`
- `blog-common/.../infrastructure/email/EmailService.kt`

## 용어

- **팬아웃(fan-out)**: 이벤트 1건을 구독자 수만큼의 개별 발송 작업(이메일/인앱 알림)으로 나눠 처리하는 것. 1:N.
- **팬인(fan-in)**: 여러 소스의 작업/데이터를 하나로 모아 처리하는 것. N:1. 팬아웃의 반대.
- **아웃박스 패턴(outbox pattern)**: "상태를 바꾸는 트랜잭션"과 "그 결과를 외부로 보내는 작업"을 분리하지 않고 한 트랜잭션으로 묶기 위한 패턴. 외부 발송(이메일 전송 등)을 트랜잭션 안에서 직접 하는 대신, 같은 트랜잭션에서 "보낼 내용"을 pending 레코드로만 남긴다. 커밋되는 순간 그 레코드가 이미 DB에 영속화되므로, 이후 별도 프로세스(비동기 리스너, 배치 등)가 pending 레코드를 읽어 실제 발송을 수행해도 트랜잭션 커밋과 발송 사이에 안전하게 끊어 처리할 수 있다. 발송 도중 프로세스가 죽어도 레코드가 pending 상태로 남아 있어 재시도가 가능하다.
  - 교과서적인 정의는 보통 도메인과 무관한 전용 이벤트 로그 테이블 + 메시지 브로커로 릴레이하는 구조를 가리킨다. 이 프로젝트는 브로커가 없으므로(§3), `notifications` 테이블에 상태 컬럼을 얹어 같은 효과를 내는 경량 변형을 쓴다(§4, 5). 핵심 아이디어(커밋 시점에 미리 영속화해 크래시로부터 안전하게 만든다)는 동일하다.

## 배포 환경

쿠버네티스 클러스터. `blog-api-deployment.yaml` 기준 현재 replica는 1이지만, 서버 1대를 추가해 추후 2 replica로 확장할 예정이다. 지금 1 replica라고 설계를 단순화하지 않고, 처음부터 파드 간 조율이 필요 없는 구조로 설계한다.

## 요구사항

- 사용자가 (공개) 게시글을 발행하면, 해당 블로그의 구독자들에게 알림을 보낸다.
  - 이메일 발송
  - 구독자가 로그인 계정이라면 대시보드 알림(벨)에도 표시
- 로그인 상태에서 다른 사용자의 블로그를 구독하면 즉시 구독 처리된다.
- (이번 스코프 제외) 비로그인 상태에서 이메일만 입력해 구독하는 방식은 이번 설계에서 제외한다. 필요성이 확인되면 별도 기능/이슈로 진행한다.

## 결정한 설계 방향

### 1. 구독은 로그인 계정만 가능

비로그인 이메일 구독을 지원하려면 필요했을 것들(스키마상 `user_id` nullable 분기, 이메일 오남용 방지를 위한 더블 옵트인, 비인증 write 엔드포인트에 대한 rate limit/캡차)이 없어져 구현 난이도가 크게 줄어든다. 기존에 스텁으로 존재하는 `Subscription(user_id, blog_id)` + `unique(user_id, blog_id)` 구조를 그대로 쓸 수 있다.

**확인 필요: DB 테이블이 아직 없음.** `Subscription.kt`, `Notification.kt`는 `@Entity`까지 전부 주석 처리된 스텁이고, `infrastructure/postgresql/tables/`에 대응 DDL(`subscriptions`, `notifications`)이 없다. `application.yml`의 `ddl-auto: validate` 때문에 엔티티 주석만 풀면 앱이 기동 실패한다. 신규 테이블 2개(subscriptions, notifications) 마이그레이션 SQL 작성이 선행되어야 한다. (별도 outbox 테이블은 만들지 않는다. §4, 5 참고.)

**Subscription FK cascade 정책 (신규 결정 필요).** 기존 테이블들(`08_posts.sql` 등)의 관례는 `blog_id → CASCADE`, `author_id → SET NULL`(글은 보존), 참조 무결성이 중요한 카테고리류는 `RESTRICT`다. Subscription은 구독자나 블로그 중 하나만 없어져도 레코드 자체가 의미 없으므로, `user_id`, `blog_id` 둘 다 `ON DELETE CASCADE`로 간다. 다만 현재 블로그 삭제·유저 탈퇴 기능 자체가 서비스 레벨에 구현되어 있지 않다. `UserStatus.DELETED`는 선언만 되고 미사용이고, `BlogService`에도 삭제 메서드가 없다. 스키마는 지금 정해두고, 실제 cascade 동작 검증은 해당 기능이 생긴 뒤에 한다.

### 2. 알림은 게시글이 "최초로" 발행되는 시점에만 발생

이미 발행된 글을 재수정해도 알림이 다시 나가지 않아야 한다.

- 판별 기준: `Post.publishedAt`이 null → non-null 로 바뀌는 전이 시점.
- 새 컬럼은 추가하지 않는다. `publishedAt`을 그대로 "최초 발행 여부" 판별에 재사용한다.
- 기존 버그 발견: `Post.update()`(`Post.kt:59`)가 `publishedAt`을 전혀 세팅하지 않는다. DRAFT로 만든 뒤 수정을 통해 PUBLISHED로 바꾸면 `publishedAt`이 계속 null로 남는다. 이번 작업에서 함께 고친다.
- `Post.update()`가 "이번 호출로 막 최초 발행됐는지"를 boolean으로 반환하도록 변경하고, 그 값을 알림 트리거 여부 판단에 사용한다.

**`PostStatus`는 실제로 4종이다** (`DRAFT`, `PUBLISHED`, `PRIVATE`, `ARCHIVED`). DRAFT/PUBLISHED 이원 구조가 아니다. `UpdatePostRequest.status`는 클라이언트가 임의 값을 넘길 수 있어 PUBLISHED → PRIVATE/ARCHIVED 전환(사실상 발행취소)도 가능하다.

**재발행 시 알림 정책 (결정됨).** PUBLISHED(publishedAt=t1) → PRIVATE/ARCHIVED 전환 → 다시 PUBLISHED로 되돌리는 재발행 시나리오에서는 `publishedAt`이 t1로 유지되므로(`update()`가 건드리지 않음) null → non-null 전이가 일어나지 않는다. 즉 재발행 시 알림을 다시 보내지 않는 것으로 확정한다. 최초 발행 1회에만 알림이 나가는 현재 설계를 유지하고, 별도 상태 추적 컬럼도 추가하지 않는다.

(참고: 예약 발행(SCHEDULED) 기능은 현재 코드에 없다. 다만 백로그에 있는 기능이라 아래 원칙을 미리 정해둔다.)

**예약 발행 대비 원칙** (지금 구현하지 않지만, 나중에 판별 기준을 안 바꿔도 되도록 미리 정해둠)

- `publishedAt`은 실제 공개 시각에만 세팅한다. 예약 등록 시점에는 `status`, `publishedAt` 모두 건드리지 않고, 예약 의도는 별도 필드(예: `scheduledAt`)에만 저장한다. 실제 공개 시각에 스케줄러/배치가 상태를 PUBLISHED로 바꾸는 순간에만 `publishedAt`을 처음 세팅한다. 이러면 null → non-null 전이가 항상 실제 공개 시각과 일치해 지금 판별 기준을 바꿀 필요가 없다.
- 공개 전환 진입점을 `Post.update()`가 아니라 전용 메서드(예: `Post.publish()`)로 분리한다. 즉시 발행 요청이든 향후 예약 발행 워커든 이 메서드 하나만 거치게 해, 판별 로직이 여러 경로에 중복되는 것을 막는다.
- 예약 발행 트리거가 `blog-api`가 아닌 다른 프로세스(예: `blog-batch` 배치 잡)에서 돌아도 별도 인프라가 필요 없다. 위 원칙을 지키는 공개 전환 로직을 그 프로세스에서 재사용해 `notifications`에 `PENDING` 행만 자기 트랜잭션 안에서 만들면, 아래 4·5번에서 설계한 배치 스윕이 프로세스와 무관하게 처리한다. `ApplicationEventPublisher` + `AFTER_COMMIT` + `@Async`는 즉시 발행 경로의 최적화일 뿐 정합성의 필수 조건은 아니다.

### 3. 메시지 큐(Kafka 등) 없이 인프로세스 이벤트로 처리

현재 이 리포에는 Kafka/RabbitMQ/SQS/Redis 등 메시징 인프라가 없고, `blog-batch`도 실시간 컨슈머가 아니라 k8s CronJob이 트리거하는 배치 잡(청크 기반 Spring Batch)이다. 이 규모(단일 모놀리스, 특별한 컨슈머 확장 요구 없음)에서 메시지 큐 도입은 오버엔지니어링으로 판단해 채택하지 않는다.

대신:
- `ApplicationEventPublisher`로 "게시글 최초 발행됨" 이벤트를 발행한다.
- `@TransactionalEventListener(phase = AFTER_COMMIT)`으로 받아, 발행 트랜잭션이 커밋된 이후에만 알림 로직이 실행되게 한다(트랜잭션 롤백 시 알림 오발송 방지).
- 리스너는 `@Async`로 처리해 게시글 발행 요청의 응답 스레드를 블로킹하지 않는다. (`AnalyticsInternalService`에 기존 `@Async` 사용 선례 있음)

**멀티 replica 환경에서의 정합성.** 이벤트 발행(publish)과 리스너 실행이 요청을 받은 파드 안에서 로컬로 끝나는 구조라, 파드 간 조율(중복 발송 방지, 분산 락 등)이 필요 없다. `blog-batch`의 크론잡(k8s CronJob 트리거)이나 `RefreshTokenGraceCache`(파드 로컬 Caffeine 캐시, 레플리카 간 레이스 있음)와는 성격이 다르다. 현재는 1 replica지만 추후 2 replica 확장이 예정되어 있어, 처음부터 이 전제로 설계한다.

다만 `@Async` 작업은 DB/디스크에 남지 않고 메모리 스레드에서만 진행되므로, 커밋은 끝났지만 팬아웃이 끝나기 전에 롤링 디플로이 등으로 파드가 종료되면 그 알림은 기록 없이 유실된다. 멀티 replica 환경에선 롤링 디플로이가 일상적이라 이 유실 창이 실제로 자주 열린다. → **아웃박스 패턴 채택**(아래 4, 5번). (참고로 1 replica에서도 배포 시 파드가 재시작되므로 유실 창은 동일하게 존재한다. outbox 채택 근거는 replica 수와 무관하다.)

### 4, 5. 인앱 알림 + 이메일 발송: notifications 테이블을 outbox로 겸용

파드 종료 시 팬아웃 유실을 막기 위해, 발송 자체를 커밋 시점에 완결하지 않고 다음과 같이 나눈다.

**구독자별 단위(형태 B)로 결정.** 발행 이벤트당 1행만 남기는 방식(형태 A)은 행 수가 적지만, 부분 발송 실패 시 "누구까지 보냈는지"를 표현할 수 없어 재시도 시 중복 발송 위험이 있다. `EmailService`가 건별 동기 호출에 실패 시 예외를 던지는 구조라(아래 참고) 부분 실패가 흔한 실패 모드이므로, 구독자별로 독립된 상태를 갖는 형태 B를 택한다. 별도 outbox 테이블은 두지 않고, `notifications` 테이블에 이메일 발송 상태 컬럼(예: `email_status: PENDING/SENT/FAILED`)을 추가해 그 역할을 겸하게 한다.

- 게시글 발행 **커밋과 같은 트랜잭션**에서 `INSERT INTO notifications (...) SELECT user_id, ... FROM subscriptions WHERE blog_id = :blogId` 형태의 단일 bulk INSERT로, 구독자 수만큼의 행을 `email_status = PENDING` 상태로 한 번에 남긴다. 애플리케이션에서 구독자 수만큼 루프를 돌며 개별 INSERT를 하지 않으므로, 구독자가 많아도 발행 트랜잭션 자체는 가볍다. 커밋되는 순간 이미 DB에 영속화되므로, 이후 `@Async` 처리가 시작되기 전에 파드가 죽어도 유실되지 않는다.
- `@TransactionalEventListener` + `@Async` 리스너가 `WHERE email_status = PENDING`인 행을 읽어 `EmailService`를 호출하고, 성공하면 `SENT`로, 실패하면 `FAILED`로 표시한다. 인앱 알림 표시는 행이 insert된 시점에 이미 끝나 있으므로 별도 처리가 필요 없다.
- 처리 도중 파드가 죽어도 행은 `PENDING`으로 남으므로, 재시작된 파드나 이후 배치가 이어서 처리할 수 있다. 필요하면 `blog-batch`에 `PENDING` 행을 쓸어가는 크론잡을 추가해 재시도를 보장한다(기존 배치 패턴 재사용, 새 인프라 도입 없음). 참고할 만한 기존 구현: `VideoGcJobConfig.purge()`. 실패 시 row를 지우지 않고 남겨 다음 실행이 재시도하는 패턴이 이미 있다.
- 기존에 준비된 `NotificationType.NEW_POST`, `RelatedEntityType.SUBSCRIPTION` enum 값을 그대로 사용한다.

**주의: `EmailService`는 팬아웃용으로 설계되어 있지 않다.** `blog-common`의 `EmailService`는 완전 동기 SMTP 호출(Mailgun, SES 아님)이고, 실패 시 예외를 던지며 rate limit/재시도/배치 발송 로직이 없다. 현재 유일한 사용처는 인증코드 1건 발송뿐이다. 구독자 수만큼 `@Async` 리스너 안에서 순차 호출하면 스레드가 오래 점유되고 Mailgun 자체 rate limit에 걸릴 수 있다. 처리기(리스너 또는 배치 스윕)에서 청크 단위 처리 + 처리 간 지연 등 최소한의 스로틀링을 설계에 포함해야 한다(구체 수치는 보류 항목).

## 보류 / 추후 결정 필요

- 비로그인 이메일 구독 지원 여부와 방식
- 이메일 템플릿 내용/디자인
- 알림함 조회 API, 읽음 처리 등 세부 스펙
- 이메일 발송 실패(`FAILED`) 시 재시도 정책, 재시도 횟수 제한
- `PENDING` 행을 이어서 처리하는 트리거를 `@Async` 재시도로 할지, `blog-batch` 주기 스윕으로 할지
- `EmailService` 팬아웃 시 청크 크기/처리 간 지연 등 구체적인 스로틀링 수치
- `notifications.email_status` 폴링용 부분 인덱스 등 인덱스 설계
- `subscriptions`/`notifications` 신규 테이블 마이그레이션 SQL 작성 (스키마 설계 자체는 위 1번, 4·5번에서 결정, 실제 DDL 파일 작성은 구현 단계 작업)
