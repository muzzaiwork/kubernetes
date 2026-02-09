# 쿠버네티스(Kubernetes) 설치 가이드

로컬 환경에서 쿠버네티스를 학습하기 위해 클러스터를 구성하고 제어 도구인 `kubectl`을 설치하는 방법을 정리합니다.

## 1. 개요
쿠버네티스를 관리하기 위해서는 클러스터에 명령을 내리는 CLI 도구인 **kubectl**과 실제로 컨테이너가 돌아갈 **쿠버네티스 클러스터** 환경이 필요합니다.

## 2. kubectl 설치 (macOS)
공식 문서의 가이드에 따라 `kubectl`을 설치합니다.

### ① Homebrew를 이용한 설치 (권장)
가장 간단하고 관리가 편한 방법입니다.
```bash
brew install kubectl
```

### ② curl을 이용한 바이너리 직접 다운로드
Homebrew를 사용하지 않을 경우 최신 릴리즈를 직접 다운로드하여 설치할 수 있습니다.
```bash
# 최신 릴리즈 다운로드 (Intel Mac인 경우 arm64 대신 amd64 사용)
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/darwin/arm64/kubectl"

# 실행 권한 부여 및 경로 이동
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin/kubectl
sudo chown root: /usr/local/bin/kubectl
```

### ③ 설치 확인
```bash
kubectl version --client
```

## 3. 쿠버네티스 클러스터 환경 구성
로컬에서 클러스터를 실행하는 대표적인 도구들입니다.

### ① Docker Desktop 이용 (가장 간편함)
Docker Desktop 설정에서 클릭 한 번으로 쿠버네티스를 활성화할 수 있습니다.
- **장점**: 별도 도구 설치 없이 바로 사용 가능.
- **방법**: `Settings` -> `Kubernetes` -> `Enable Kubernetes` 체크.

### ② Minikube 이용
로컬에서 쿠버네티스 클러스터를 실행하는 대중적인 도구입니다.
- **장점**: 다양한 기능을 지원하며 가상 머신이나 Docker 내부에서 실행 가능.
- **설치**: `brew install minikube`

## 4. 우리 PC의 설치 과정 (Docker Desktop 선택)
현재 시스템 환경(macOS)에서는 **Docker Desktop**을 사용하여 클러스터를 구성합니다.

### Step 1: Docker Desktop 설정 변경
1. Docker Desktop 앱을 실행합니다.
2. 우측 상단의 **Settings** (톱니바퀴 아이콘) 클릭.
3. **Kubernetes** 메뉴에서 **Enable Kubernetes** 체크.
4. **Apply & Restart** 클릭.

### Step 2: 컨텍스트(Context) 확인
`kubectl`이 Docker Desktop을 바라보도록 설정합니다.
```bash
kubectl config get-contexts
kubectl config use-context docker-desktop
```

### Step 3: 최종 상태 확인
```bash
kubectl cluster-info
kubectl get nodes
```
