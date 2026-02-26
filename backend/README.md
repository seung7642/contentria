# backend

## Docker 이미지 빌드 및 배포
다음은 로컬에서 Docker 이미지를 빌드하고 원격 리눅스 서버로 배포하는 단계입니다:
1. 프로젝트 루트 디렉토리로 이동: `cd <project-root>`
2. Docker 이미지 빌드: `docker build -t contentria/blog-api:1.0 -f blog-api/Dockerfile .`
3. Docker 이미지 저장: `docker save -o blog-api.tar contentria/blog-api:1.0`
4. 원격 서버로 이미지 파일 전송: `scp blog-api.tar <username>@<remote-host>:~`

> note:
> - blog-batch 모듈도 위와 동일한 방식으로 진행합니다.

다음은 원격 리눅스 서버에서 Docker 이미지를 로드하는 단계입니다:
1. 원격 서버에 SSH로 접속: `ssh <username>@<remote-host>`
2. 원격 서버에서 Docker 이미지 로드: `sudo ctr -n k8s.io images import blog-api.tar`
3. 이미지 로드 확인: `sudo crictl images`
