# [실습] EC2에서 도커(Docker) 및 k3s 설치하기

### ✅ 목표
- AWS EC2 인스턴스 환경에서 컨테이너 실행을 위한 **Docker**를 설치한다.
- 저사양 환경에 최적화된 경량 쿠버네티스인 **k3s**를 설치하고 정상 동작을 확인한다.

---

### 1. 설치 권장 사양

k3s는 매우 가볍지만, 백엔드 애플리케이션(Spring Boot)과 DB(MySQL)를 함께 실행하기 위해 아래 이상의 사양을 권장합니다.

| 항목 | 권장 사양 | 비고 |
| :--- | :--- | :--- |
| **인스턴스 타입** | `t4g.small` (또는 `t3.small`) | CPU 2 Core, RAM 2GB 이상 권장 |
| **OS** | `Ubuntu 22.04 LTS` | 가장 범용적이고 안정적인 환경 |
| **디스크** | `20GB` 이상 | 이미지 및 데이터 저장 공간 확보 |

---

### 2. Docker 설치하기

쿠버네티스 노드에서 컨테이너 이미지를 빌드하거나 관리하기 위해 Docker를 설치합니다.

```bash
# 1. 패키지 업데이트 및 필요 도구 설치
$ sudo apt-get update && \
  sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

# 2. Docker 공식 GPG 키 추가 및 저장소 설정
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

# 3. Docker 엔진 설치
$ sudo apt-get update && sudo apt-get install -y docker-ce

# 4. 현재 사용자(ubuntu)에게 Docker 권한 부여 (로그아웃 후 재접속 필요)
$ sudo usermod -aG docker ubuntu
$ newgrp docker

# 5. Docker Compose 설치 (최신 버전)
$ sudo curl -L "https://github.com/docker/compose/releases/download/v2.27.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
$ sudo chmod +x /usr/local/bin/docker-compose
$ sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```

#### ✅ 설치 확인
```bash
$ docker -v          # Docker 버전 확인
$ docker compose version # Docker Compose 버전 확인
```

---

### 3. k3s 설치하기

k3s는 설치 스크립트 한 줄로 모든 구성 요소가 자동으로 설정됩니다.

```bash
# 1. k3s 설치 스크립트 실행
$ curl -sfL https://get.k3s.io | sh -

# 2. 설정 파일 권한 수정 (kubectl을 sudo 없이 사용하기 위함)
$ sudo chmod 644 /etc/rancher/k3s/k3s.yaml

# 3. 설치 확인
$ kubectl get nodes
```

**출력 결과 예시:**
```text
NAME             STATUS   ROLES                  AGE   VERSION
ip-172-31-0-10   Ready    control-plane,master   1m    v1.29.3+k3s1
```
`STATUS`가 `Ready`라면 이제 EC2에서 쿠버네티스를 사용할 준비가 끝났습니다!

---

### 4. 왜 k3s를 사용할까요? (복습)

1.  **초경량**: 불필요한 기능을 제거하여 512MB RAM 환경에서도 구동 가능합니다.
2.  **단일 바이너리**: 복잡한 설정 없이 실행 파일 하나로 모든 서비스가 통합되어 있습니다.
3.  **학습 최적화**: 저렴한 EC2 인스턴스 하나만으로도 쿠버네티스의 거의 모든 기능을 실습할 수 있어 비용 부담이 적습니다.

---

### ✅ 요약
- EC2 인스턴스는 최소 `t4g.small`급을 권장한다.
- Docker는 컨테이너 빌드 및 관리를 위해 필수적으로 설치한다.
- k3s 설치 후 `/etc/rancher/k3s/k3s.yaml` 권한을 수정해야 `kubectl` 명령어를 원활하게 사용할 수 있다.
