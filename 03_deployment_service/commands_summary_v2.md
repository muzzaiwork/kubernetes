# [요약] 지금까지 배운 주요 명령어 정리 (v2)

쿠버네티스 실습을 진행하며 사용한 주요 `kubectl` 명령어들을 오브젝트별로 정리한다.

---

### 1. 파드(Pod) 관련 명령어

| 기능 | 명령어 |
| :--- | :--- |
| **파드 조회** | `kubectl get pods` |
| **파드 상세 정보 확인** | `kubectl describe pod [파드명]` |
| **파드 로그 확인** | `kubectl logs [파드명]` |
| **실시간 로그 확인** | `kubectl logs -f [파드명]` |
| **파드 내부 접속** | `kubectl exec -it [파드명] -- bash` (또는 `sh`) |
| **포트 포워딩** | `kubectl port-forward pod/[파드명] [로컬포트]:[파드포트]` |
| **파드 삭제** | `kubectl delete pod [파드명]` |

---

### 2. 디플로이먼트(Deployment) 관련 명령어

| 기능 | 명령어 |
| :--- | :--- |
| **디플로이먼트 조회** | `kubectl get deployment` |
| **레플리카셋 조회** | `kubectl get rs` (또는 `replicaset`) |
| **업데이트 상태 확인** | `kubectl rollout status deployment/[명칭]` |
| **업데이트 이력 확인** | `kubectl rollout history deployment/[명칭]` |
| **이전 버전으로 롤백** | `kubectl rollout undo deployment/[명칭]` |
| **디플로이먼트 삭제** | `kubectl delete deployment [디플로이먼트명]` |

---

### 3. 서비스(Service) 관련 명령어

| 기능 | 명령어 |
| :--- | :--- |
| **서비스 조회** | `kubectl get service` (또는 `svc`) |
| **엔드포인트 확인** | `kubectl get endpoints` (서비스와 연결된 파드 IP 확인) |
| **서비스 삭제** | `kubectl delete service [서비스명]` |

---

### 4. 공통 및 관리 명령어

#### ✅ 리소스 생성 및 변경 적용
매니페스트 파일(YAML)에 정의된 내용을 클러스터에 반영할 때 사용한다.
```bash
# kubectl apply -f [파일명]
$ kubectl apply -f spring-deployment.yaml
```

#### ✅ 전체 리소스 조회
현재 네임스페이스에 있는 모든 주요 리소스(Pod, Service, Deployment, RS 등)를 한 번에 확인한다.
```bash
$ kubectl get all
```

#### ✅ 모든 리소스 삭제 (주의)
현재 클러스터(네임스페이스)의 모든 리소스를 한꺼번에 삭제한다. 실습 환경을 초기화할 때 유용하지만, 운영 환경에서는 절대 주의해야 한다.
```bash
$ kubectl delete all --all
```
