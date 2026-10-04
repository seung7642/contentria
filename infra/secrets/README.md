# Secrets

수동으로 적용하는 쿠버네티스 시크릿 모음이다. ArgoCD는 이 폴더를 관리하지 않는다.

`README.md`와 `example/`만 git에 올라가고, 나머지는 `.gitignore`로 제외된다.

## 구조

```
infra/secrets/
├── README.md
├── example/                     # 키 이름만 적은 예시 (git 추적)
│   ├── database.yaml
│   ├── keycloak.yaml
│   ├── openfga.yaml
│   ├── blog-backend-api.yaml
│   └── blog-backend-batch.yaml
└── dev/                         # 실제 값 (git 미추적), example/과 같은 파일 구성
```

## 시크릿 목록

| 파일 | namespace | Secret 이름 | 사용처 |
| --- | --- | --- | --- |
| `database.yaml` | `database` | `postgres-contentria` | 블로그 서비스 DB 계정 (CloudNativePG가 생성) |
| `database.yaml` | `database` | `postgres-keycloak` | keycloak DB 계정 (CloudNativePG가 생성) |
| `database.yaml` | `database` | `postgres-openfga` | openfga DB 계정 (CloudNativePG가 생성) |
| `keycloak.yaml` | `keycloak` | `keycloak-admin` | keycloak 최초 관리자 계정 |
| `keycloak.yaml` | `keycloak` | `keycloak-db` | keycloak의 DB 접속 비밀번호 |
| `openfga.yaml` | `openfga` | `openfga-datastore` | openfga의 DB 접속 URI |
| `blog-backend-api.yaml` | `blog` | `blog-backend-api` | 백엔드 API |
| `blog-backend-batch.yaml` | `blog` | `blog-backend-batch` | 배치 CronJob |

DB 비밀번호는 여러 시크릿에 같은 값이 들어간다. `database.yaml`의 비밀번호를 바꾸면 아래 값도 함께 바꾼다.

| 원본 (`database.yaml`) | 같은 값을 넣는 곳 |
| --- | --- |
| `postgres-contentria` | `blog-backend-api`, `blog-backend-batch`의 `DATABASE_PASSWORD` |
| `postgres-keycloak` | `keycloak-db`의 `password` |
| `postgres-openfga` | `openfga-datastore`의 `uri` |

postgres 백업용 저장소 접근 키는 백업을 구성할 때 추가한다.

## 사용 방법

1. `example/`의 파일을 `<env>/`로 복사하고 값을 채운다.

   ```bash
   cp infra/secrets/example/*.yaml infra/secrets/dev/
   ```

2. 클러스터에 적용한다. 각 파일에 `Namespace`가 함께 있어 root 앱 적용 전에도 적용된다.

   ```bash
   kubectl apply -f infra/secrets/dev/
   ```

3. Ansible이 root 앱을 적용하기 전에 인벤토리 변수 `required_secrets`의 시크릿이 있는지 확인한다.

## 규칙

- 키를 추가하거나 빼면 `example/`도 함께 고친다.
- 시크릿을 추가하면 위 목록과 Ansible `required_secrets`에 함께 추가한다.
- 원본은 로컬에만 있으므로 비밀번호 관리자 등 별도 위치에 백업한다.
