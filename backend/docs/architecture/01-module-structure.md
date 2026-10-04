# 모듈 구조

## 전체 구성

```
backend/
├── core/                    라이브러리 모듈. 도메인 로직 전부.
├── app/                     배포 단위 컨테이너 (빌드 스크립트 없음)
│   ├── api/                 HTTP API 서버
│   ├── batch/               배치 잡
│   └── worker/              메시지 컨슈머
├── build-logic/             Gradle 컨벤션 플러그인
├── infrastructure/          배포 관련 리소스
└── docs/
```

## 모듈을 나누는 기준

### core

라이브러리 모듈이다. 실행 가능한 jar를 만들지 않는다.

담는 것:

- 도메인 모델과 비즈니스 규칙
- 애플리케이션 서비스(유스케이스 구현)
- 인바운드 포트, 아웃바운드 포트
- 아웃바운드 어댑터(영속성, 외부 API 클라이언트)

담지 않는 것:

- 컨트롤러, 배치 잡 설정, 메시지 리스너
- Spring Security 설정, 필터 체인
- 웹 관련 의존성

`app` 하위 모듈 전부가 `core`를 의존한다. 반대 방향은 없다.

### app 하위 모듈

기준은 인바운드 방식이 아니라 **따로 떠야 하는 프로세스**다.
같은 모듈 안에 웹 컨트롤러와 스케줄러가 함께 있어도 된다.

현재 세 개로 나눈 이유는 생명주기가 다르기 때문이다.

| 모듈 | 생명주기 | 특성 |
|---|---|---|
| api | 상주 데몬 | 무중단 배포, 헬스체크, 오토스케일링 |
| batch | 원샷 | 실행 후 종료 코드 반환, CronJob 트리거, 멱등성 |
| worker | 상주 데몬 | 메시지 소비, 재처리 정책 |

배치가 API와 같은 프로세스에 있으면 배치 작업의 메모리 사용이 API 응답에 영향을 준다.
따로 스케일링할 수도 없다.

### app 디렉터리 자체

`app/`은 하위 모듈을 묶는 컨테이너다. 코드가 없다.

`app/build.gradle.kts` 파일을 만들지 않는다.
파일이 있으면 플러그인이 적용되어 빈 jar를 만들려다 빌드가 실패한다.

## Gradle 설정

```kotlin
// settings.gradle.kts
rootProject.name = "backend"

include("core")
include("app:api", "app:batch", "app:worker")
```

의존성 경로는 프로젝트 경로와 무관하게 절대 경로를 쓴다.

```kotlin
// app/api/build.gradle.kts
dependencies {
    implementation(project(":core"))
}
```

## 컨벤션 플러그인

`build-logic`에 정의한다. 루트 `build.gradle.kts`에 `subprojects` 블록을 쓰지 않는다.
모듈 성격이 세 가지(컨테이너, 라이브러리, 애플리케이션)로 갈리는데
`subprojects`는 이를 구분할 수 없다.

| 플러그인 | 적용 대상 | 역할 |
|---|---|---|
| `contentria-kotlin-common-conventions` | 전체 | 툴체인, 테스트, 컴파일 옵션 |
| `contentria-spring-library-conventions` | core | Boot BOM, bootJar 비활성화 |
| `contentria-jpa-conventions` | core | noarg, allOpen |
| `contentria-spring-app-conventions` | app 하위 | Boot 플러그인, core 의존 |

## 검증

```bash
./gradlew projects       # 모듈 트리 확인
./gradlew :core:jar      # plain jar 생성 확인
./gradlew :app:api:bootJar
```

`core`에서 `bootJar`가 켜져 있으면 메인 클래스를 찾지 못해 실패한다.

## 향후 분리

`core`가 커져서 `batch`가 불필요한 의존성까지 끌고 오는 것이 문제가 되면
`core-domain`과 `core-persistence`로 나눈다.
그 시점에 `libs/` 디렉터리를 두어 `app/`과 대칭을 맞춘다.

지금 미리 나누지 않는다. 기동 시간이나 jar 크기가 실제로 측정되었을 때 판단한다.