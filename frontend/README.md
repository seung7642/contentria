# frontend

## Docker 이미지 빌드 및 배포

다음은 로컬에서 Docker 이미지를 빌드하고 원격 리눅스 서버로 배포하는 단계입니다:

1. 프론트엔드 프로젝트 루트 디렉토리로 이동: `cd <frontend-project-root>`
2. Docker 이미지 빌드: `docker build -t contentria/blog-frontend:1.0 .`
3. Docker 이미지 저장: `docker save -o blog-frontend.tar contentria/blog-frontend:1.0`
4. 원격 서버로 이미지 파일 전송: `scp blog-frontend.tar <username>@<remote-host>:~`

다음은 원격 리눅스 서버에서 Docker 이미지를 로드하는 단계입니다:

1. 원격 서버에 SSH로 접속: `ssh <username>@<remote-host>`
2. 원격 서버에서 Docker 이미지 로드: `sudo ctr -n k8s.io images import blog-frontend.tar`
3. 이미지 로드 확인: `sudo crictl images`
