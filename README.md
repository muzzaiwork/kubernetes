# Kubernetes 학습 가이드

쿠버네티스(Kubernetes, K8s) 학습을 시작한 것을 환영한다! 이 프로젝트는 쿠버네티스의 핵심 개념부터 실무 활용까지 단계별로 학습하고 실습 예제를 관리하는 공간이다.

## 📖 쿠버네티스란?

### ✅ 쿠버네티스(Kubernetes, K8s)란?
쿠버네티스는 **다수의 컨테이너를 효율적으로 배포, 확장 및 관리**하기 위한 오픈 소스 시스템이다.

Docker Compose와 유사하게 다수의 컨테이너를 관리하는 목적을 가지고 있지만, 훨씬 더 거대한 규모의 클러스터를 관리할 수 있는 **Docker Compose의 확장판**이라고 이해하면 쉽다.

### ✅ 쿠버네티스의 장점
- **컨테이너 관리 자동화**: 배포, 확장, 업데이트 프로세스를 자동화한다.
- **부하 분산 (로드 밸런싱)**: 트래픽을 여러 컨테이너에 효율적으로 분산한다.
- **쉬운 스케일링**: 트래픽 증감에 따라 컨테이너 수를 쉽게 조절할 수 있다.
- **셀프 힐링**: 장애가 발생한 컨테이너를 자동으로 재시작하거나 교체한다.

## 🏁 학습 로드맵

### 1단계: 기본 개념 파악
- [x] [쿠버네티스 소개 및 관계 이해](01_concepts/introduction.md)
- [x] [매니페스트(Manifest) 파일의 이해](01_concepts/manifest.md)
- [x] [이미지 풀 정책(ImagePullPolicy) 이해](01_concepts/image_pull_policy.md)
- [ ] 쿠버네티스 아키텍처 (Control Plane, Worker Node)

### 2단계: 실습 환경 구축
- [x] [로컬 쿠버네티스 환경 구성 (Docker Desktop + kubectl)](01_concepts/installation.md)
- [x] `kubectl` CLI 도구 기본 사용법 숙지

### 3단계: 기본 오브젝트 실습
#### 🔹 Pod (파드)
- [x] [Pod 개념 이해](02_pods/pod_concept.md)
- [x] [Nginx Pod 생성 및 관리 실습](02_pods/nginx_pod_practice.md)
- [x] [Spring Boot Pod 생성 및 이미지 풀 정책 실습](02_pods/spring_boot_pod_practice.md)
- [x] [Spring Boot Pod 3개 띄우기 (수평 확장 맛보기)](02_pods/spring_boot_3_pods_practice.md)
- [x] [Nest.js Pod 생성 실습](02_pods/nest_js_pod_practice.md)
- [x] [Next.js Pod 생성 실습](02_pods/next_js_pod_practice.md)
- [x] [Pod 디버깅 가이드 (describe, logs, exec)](02_pods/pod_debugging.md)
- [x] [**주요 kubectl 명령어 요약 정리**](02_pods/kubectl_commands_summary.md)

#### 🔹 Deployment & Service (디플로이먼트 & 서비스)
- [x] [Deployment 개념 및 구조 이해](03_deployment_service/deployment_concept.md)
- [x] [Deployment를 이용한 Spring Boot 스케일링 실습](03_deployment_service/spring_boot_deployment_practice.md)
- [x] [Deployment를 활용한 서버 개수 조절 방법 (Scaling)](03_deployment_service/deployment_scaling.md)
- [x] [쿠버네티스의 자동 복구 기능 (Self-Healing)](03_deployment_service/self_healing.md)
- [x] [새로운 버전으로 업데이트 (Rolling Update)](03_deployment_service/rolling_update.md)
- [x] [Nest.js 서버 Deployment & Service 실습](03_deployment_service/nest_js_deployment_practice.md)
- [x] [Service 개념 이해](03_deployment_service/service_concept.md)
- [x] [Service를 이용한 네트워크 노출 및 로드 밸런싱 실습](03_deployment_service/spring_boot_service_practice.md)
- [x] [**지금까지 배운 명령어 요약 정리 (Pod & Deployment)**](03_deployment_service/commands_summary_v2.md)

### 4단계: 고급 설정 및 관리
- [ ] ConfigMap & Secret (설정 관리)
- [ ] Ingress (외부 트래픽 제어)
- [ ] Storage (Volume, PV, PVC)

### 5단계: 운영 및 모니터링
- [ ] 리소스 제한 (Requests & Limits)
- [ ] 상태 확인 (Liveness & Readiness Probes)
- [ ] Helm (패키지 매니저) 활용

---

## 🛠 필요한 도구
- **Docker Desktop**: 컨테이너 런타임 및 로컬 쿠버네티스 환경
- **kubectl**: 쿠버네티스 클러스터 제어 도구

## 📚 추천 리소스
- [Kubernetes 공식 문서 (한글)](https://kubernetes.io/ko/docs/home/)
- [Kubernetes 기초 (Interactive Tutorials)](https://kubernetes.io/ko/docs/tutorials/kubernetes-basics/)
