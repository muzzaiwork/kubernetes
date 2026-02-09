# Kubernetes 학습 가이드

쿠버네티스(Kubernetes, K8s) 학습을 시작하신 것을 환영합니다! 이 프로젝트 폴더는 학습 과정을 기록하고 실습 예제를 관리하는 용도로 사용될 것입니다.

## 📖 쿠버네티스란?

### ✅ 쿠버네티스(Kubernetes, K8s)란?
쿠버네티스는 **다수의 컨테이너를 효율적으로 배포, 확장 및 관리**하기 위한 오픈 소스 시스템입니다.

Docker Compose와 유사하게 다수의 컨테이너를 관리하는 목적을 가지고 있지만, 훨씬 더 거대한 규모의 클러스터를 관리할 수 있는 **Docker Compose의 확장판**이라고 이해하면 쉽습니다.

### ✅ 쿠버네티스의 장점
- **컨테이너 관리 자동화**: 배포, 확장, 업데이트 프로세스를 자동화합니다.
- **부하 분산 (로드 밸런싱)**: 트래픽을 여러 컨테이너에 효율적으로 분산합니다.
- **쉬운 스케일링**: 트래픽 증감에 따라 컨테이너 수를 쉽게 조절할 수 있습니다.
- **셀프 힐링**: 장애가 발생한 컨테이너를 자동으로 재시작하거나 교체합니다.

## 🏁 학습 로드맵

### 1단계: 기본 개념 파악
- [x] 컨테이너와 쿠버네티스의 관계 (문서화 완료: [01_concepts/introduction.md](01_concepts/introduction.md))
- [x] 매니페스트 파일의 이해 (문서화 완료: [01_concepts/manifest.md](01_concepts/manifest.md))
- [x] 이미지 풀 정책(ImagePullPolicy) 이해 (문서화 완료: [01_concepts/image_pull_policy.md](01_concepts/image_pull_policy.md))
- [ ] 쿠버네티스 아키텍처 (Control Plane, Worker Node)
- [ ] 기본 오브젝트 (Pod, Service, Deployment)

### 2단계: 실습 환경 구축
- [x] Docker 설치 (확인됨: v27.3.1)
- [x] 로컬 쿠버네티스 환경 구성 (Docker Desktop)
- [x] `kubectl` CLI 도구 설치 및 익히기 (확인됨: v1.30.2)

### 3단계: 기본 오브젝트 실습
- [x] Pod 개념 이해 (문서화 완료: [01_pods/pod_concept.md](01_pods/pod_concept.md))
- [x] Nginx Pod 생성 및 관리 (예제: [01_pods/nginx-pod.yaml](01_pods/nginx-pod.yaml), 가이드: [01_pods/nginx_pod_practice.md](01_pods/nginx_pod_practice.md))
- [x] Spring Boot Pod 생성 실습 (예제: [01_pods/spring-pod.yaml](01_pods/spring-pod.yaml), 가이드: [01_pods/spring_boot_pod_practice.md](01_pods/spring_boot_pod_practice.md))
- [x] Spring Boot Pod 3개 띄워보기 (예제: [01_pods/spring-pod-triple.yaml](01_pods/spring-pod-triple.yaml), 가이드: [01_pods/spring_boot_3_pods_practice.md](01_pods/spring_boot_3_pods_practice.md))
- [x] Nest.js Pod 생성 실습 (예제: [01_pods/nest-pod.yaml](01_pods/nest-pod.yaml), 가이드: [01_pods/nest_js_pod_practice.md](01_pods/nest_js_pod_practice.md))
- [x] Next.js Pod 생성 실습 (예제: [01_pods/next-app/next-pod.yaml](01_pods/next-app/next-pod.yaml), 가이드: [01_pods/next_js_pod_practice.md](01_pods/next_js_pod_practice.md))
- [x] Pod 디버깅 하는 방법 (가이드: [01_pods/pod_debugging.md](01_pods/pod_debugging.md))
- [x] 지금까지 배운 주요 명령어 정리 (가이드: [01_pods/kubectl_commands_summary.md](01_pods/kubectl_commands_summary.md))
- [x] Deployment 개념 이해 (문서화 완료: [02_deployments/deployment_concept.md](02_deployments/deployment_concept.md))
- [x] Deployment를 이용한 스케일링 (예제: [02_deployments/spring-deployment.yaml](02_deployments/spring-deployment.yaml), 가이드: [02_deployments/spring_boot_deployment_practice.md](02_deployments/spring_boot_deployment_practice.md))
- [ ] Service를 이용한 네트워크 노출 (`03_services/`)

### 4단계: 고급 설정 및 관리
- [ ] ConfigMap & Secret (설정 관리)
- [ ] Ingress (외부 트래픽 제어)
- [ ] Storage (Volume, PV, PVC)

### 5단계: 운영 및 모니터링
- [ ] 리소스 제한 (Requests & Limits)
- [ ] 상태 확인 (Liveness & Readiness Probes)
- [ ] Helm (패키지 매니저) 활용

---

## 🛠 필요한 도구 설치 안내
- **Docker Desktop**: 컨테이너 런타임 및 로컬 쿠버네티스 환경
- **kubectl**: 쿠버네티스 클러스터 제어 도구

## 📚 추천 리소스
- [Kubernetes 공식 문서 (한글)](https://kubernetes.io/ko/docs/home/)
- [Kubernetes 기초 (Interactive Tutorials)](https://kubernetes.io/ko/docs/tutorials/kubernetes-basics/)
