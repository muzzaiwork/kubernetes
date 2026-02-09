# Spring Boot 파드(Pod) 띄우기 실습

앞서 실습한 Nginx 파드에 이어, 실제 웹 애플리케이션인 **Spring Boot**를 파드로 띄워보고 Nginx와의 차이점을 이해해 봅니다.

## 1. Spring Boot 매니페스트 분석 (`spring-boot-pod.yaml`)

Spring Boot 애플리케이션을 위한 매니페스트 파일입니다.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: spring-boot-pod
  labels:
    app: spring-boot
spec:
  containers:
    - name: spring-boot-container
      image: ghcr.io/m88i/spring-boot-hello-world # 샘플 스프링 부트 이미지
      ports:
        - containerPort: 8080 # 스프링 부트의 기본 포트
```

### 🔍 주요 포인트
- **containerPort: 8080**: Spring Boot의 기본 포트인 8080을 사용합니다. Nginx(80)와 포트 번호가 다름에 유의하세요.
- **labels**: 파드에 `app: spring-boot`라는 라벨을 붙였습니다. 라벨은 나중에 **Service**나 **Deployment**에서 특정 파드를 선택할 때 중요한 역할을 합니다.

---

## 2. 실습 진행

### ① 파드 생성
```bash
kubectl apply -f 01_pods/spring-boot-pod.yaml
```

**실행 결과:**
```text
pod/spring-boot-pod created
```

### ② 상태 확인
Spring Boot는 Java 애플리케이션이므로 Nginx보다 기동 시간이 조금 더 걸릴 수 있습니다. `STATUS`가 `Running`이 될 때까지 기다립니다.
```bash
kubectl get pods
```

**실행 결과:**
```text
NAME              READY   STATUS    RESTARTS   AGE
nginx-pod         1/1     Running   0          25m
spring-boot-pod   1/1     Running   0          20s
```

### ③ 임시 접속 확인 (Port Forwarding)
호스트의 8081 포트를 파드의 8080 포트로 연결해 봅니다.
```bash
kubectl port-forward pod/spring-boot-pod 8081:8080
```

**실행 결과:**
```text
Forwarding from 127.0.0.1:8081 -> 8080
Forwarding from [::1]:8081 -> 8080
```
이제 브라우저에서 `http://localhost:8081`에 접속하면 "Hello World!" 메시지를 확인할 수 있습니다.

---

## 3. Nginx 파드 vs Spring Boot 파드 비교

| 항목 | Nginx 파드 | Spring Boot 파드 |
| :--- | :--- | :--- |
| **기본 포트** | 80 | 8080 |
| **기동 속도** | 매우 빠름 | 보통 (JVM 실행 시간 필요) |
| **용도** | 정적 파일 서빙, 리버스 프록시 | 비즈니스 로직 처리 (API 서버 등) |

---

## 4. 파드 내부에서 통신해보기 (고급)

쿠버네티스 클러스터 안에서는 파드끼리 서로의 IP를 통해 통신할 수 있습니다. 

1. 먼저 `spring-boot-pod`의 IP를 확인합니다.
   ```bash
   kubectl get pod spring-boot-pod -o wide
   ```
   *IP 예시: `10.1.0.7`*

2. `nginx-pod` 내부로 들어가서 Spring Boot 파드로 요청을 보내봅니다.
   ```bash
   kubectl exec -it nginx-pod -- curl [Spring-Boot-IP]:8080
   ```

이처럼 쿠버네티스에서는 파드들이 각자의 고유한 IP를 가지고 클러스터 내부에서 자유롭게 통신할 수 있는 네트워크 환경을 제공합니다.
