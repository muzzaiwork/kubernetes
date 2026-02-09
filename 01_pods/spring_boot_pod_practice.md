# Spring Boot 파드(Pod) 띄우기 실습

앞서 실습한 Nginx 파드에 이어, 실제 웹 애플리케이션인 **Spring Boot**를 파드로 띄워보고 Nginx와의 차이점을 이해해 봅니다.

## 1. Spring Boot 매니페스트 분석 (`spring-pod.yaml`)

Spring Boot 애플리케이션을 위한 매니페스트 파일입니다. 로컬에서 빌드한 이미지를 사용하는 예시입니다.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod
spec:
  containers:
    - name: spring-container
      image: spring-server            # 로컬에서 빌드한 이미지 이름
      imagePullPolicy: IfNotPresent   # 로컬 이미지 우선 사용 (중요)
      ports:
        - containerPort: 8080         # 스프링 부트의 기본 포트
```

### 🔍 주요 포인트
- **containerPort: 8080**: Spring Boot의 기본 포트인 8080을 사용합니다. Nginx(80)와 포트 번호가 다름에 유의하세요.
- **imagePullPolicy: IfNotPresent**: 로컬 Docker 엔진에 이미지가 있으면 원격에서 찾지 않고 로컬 이미지를 사용합니다.

---

## 2. [심화] 직접 만든 Spring Boot 이미지 사용하기 (로컬 빌드 → 로컬 배포)

기존에는 이미 만들어진 이미지를 사용했지만, 직접 작성한 코드를 Docker 이미지로 만들어서 띄울 수도 있습니다.
아래는 최소 구성으로 빠르게 실습하는 코스입니다.

### ① 프로젝트 위치
- 예제 소스: `01_pods/springbootapp/src/main/java/com/example/demo/DemoApplication.java`
- Gradle 설정: `01_pods/springbootapp/build.gradle`, `01_pods/springbootapp/settings.gradle`
- Dockerfile: `01_pods/springbootapp/Dockerfile`

### ② Dockerfile 작성하기
문서에서 사용하는 간단한 Dockerfile은 다음과 같습니다.

```docker
FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### ③ Spring Boot 프로젝트 빌드하기
- Gradle Wrapper가 없다면 `gradle wrapper`로 생성하거나, 로컬에 설치된 Gradle로 `gradle build`를 실행하세요.

```bash
$ cd 01_pods/springbootapp
$ ./gradlew clean build   # 또는: gradle clean build
```

### ④ Dockerfile을 바탕으로 이미지 빌드하기
```bash
$ docker build -t spring-server .
```

### ⑤ 이미지가 잘 생성됐는지 확인하기
```bash
$ docker image ls
```

### ⑥ 매니페스트 파일 작성하기 (로컬 이미지 사용)
로컬에서 빌드한 이미지를 그대로 사용하려면 다음과 같이 매니페스트를 작성합니다.

`01_pods/spring-pod.yaml`
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod
spec:
  containers:
    - name: spring-container
      image: spring-server            # 로컬에서 빌드한 이미지 이름
      imagePullPolicy: IfNotPresent   # 로컬 이미지 우선 사용 (중요)
      ports:
        - containerPort: 8080
```

### ⑦ 매니페스트 파일을 기반으로 파드(Pod) 생성하기
```bash
$ kubectl apply -f 01_pods/spring-pod.yaml
```

### ⑧ 파드(Pod)가 잘 생성됐는지 확인
```bash
$ kubectl get pods
```

---

## 3. 실습 진행 (로컬 빌드 버전)

직접 빌드한 이미지를 사용하여 파드를 생성하고 확인해 봅니다.

### ① 파드 생성
```bash
kubectl apply -f 01_pods/spring-pod.yaml
```

**실행 결과 (예시):**
```text
pod/spring-pod created
```

### ② 상태 확인 및 트래블슈팅 (ImagePullBackOff)

파드를 생성한 후 상태를 확인해 봅니다. 처음 생성 시 `STATUS`가 `ImagePullBackOff` 또는 `ErrImagePull`로 뜰 수 있습니다.

```bash
kubectl get pods
```

**실행 결과 (에러 발생 시):**
```text
NAME         READY   STATUS             RESTARTS   AGE
spring-pod   0/1     ImagePullBackOff   0          10s
```

#### ❓ 왜 ImagePullBackOff가 발생하나요?
이는 쿠버네티스의 **이미지 풀 정책(Image Pull Policy)** 때문입니다. 태그 없이 `image: spring-server`라고만 적으면 쿠버네티스는 기본적으로 원격 저장소(Docker Hub 등)에서 이미지를 찾으려 시도합니다. 하지만 `spring-server`는 우리가 로컬에서만 만든 이미지이므로 원격에는 존재하지 않아 에러가 발생합니다.

- **상세 원인 및 정책 종류**: [이미지 풀 정책(ImagePullPolicy) 이해하기](../01_concepts/image_pull_policy.md)

#### ✅ 해결 방법: 매니페스트 수정
로컬 이미지를 사용하려면 `imagePullPolicy: IfNotPresent`를 명시해야 합니다. (이미 `spring-pod.yaml`에 반영되어 있습니다.)

```yaml
spec:
  containers:
    - name: spring-container
      image: spring-server
      imagePullPolicy: IfNotPresent  # 로컬에 있으면 가져오지 않음
```

#### 🔄 재배포하기
정책을 확인하거나 수정한 후, 기존 파드를 삭제하고 다시 생성합니다.

```bash
kubectl delete pod spring-pod
kubectl apply -f 01_pods/spring-pod.yaml
```

정상적으로 처리되면 `STATUS`가 `Running`으로 변경됩니다.

### ③ 임시 접속 확인 (Port Forwarding)
호스트의 8081 포트를 파드의 8080 포트로 연결해 봅니다.
```bash
kubectl port-forward pod/spring-pod 8081:8080
```

**실행 결과 (예시):**
```text
Forwarding from 127.0.0.1:8081 -> 8080
```
Forwarding from [::1]:8081 -> 8080
```
이제 브라우저에서 `http://localhost:8081`에 접속하면 "Hello World!" 메시지를 확인할 수 있습니다.

---

## 4. Spring Boot 서버 응답 확인하기

서버가 정상적으로 기동되었다면 아래 두 가지 방법으로 응답을 확인할 수 있습니다.

### 방법 1: 파드 내부에서 요청 보내기
`kubectl exec`를 통해 파드 내부 쉘에 접속하여 직접 `curl`을 날려봅니다.
```bash
$ kubectl exec -it spring-pod -- bash 
$ curl localhost:8080
```

### 방법 2: 포트 포워딩 활용하기
로컬 PC의 특정 포트와 파드의 포트를 연결하여 브라우저에서 확인합니다.
```bash
$ kubectl port-forward pod/spring-pod 8081:8080
```
브라우저에서 `http://localhost:8081` 접속 시 응답을 확인할 수 있습니다.

---

## 5. Nginx 파드 vs Spring Boot 파드 비교

| 항목 | Nginx 파드 | Spring Boot 파드 |
| :--- | :--- | :--- |
| **기본 포트** | 80 | 8080 |
| **기동 속도** | 매우 빠름 | 보통 (JVM 실행 시간 필요) |
| **용도** | 정적 파일 서빙, 리버스 프록시 | 비즈니스 로직 처리 (API 서버 등) |

---

## 6. 파드 내부에서 통신해보기 (고급)

쿠버네티스 클러스터 안에서는 파드끼리 서로의 IP를 통해 통신할 수 있습니다. 

1. 먼저 `spring-pod`의 IP를 확인합니다.
   ```bash
   kubectl get pod spring-pod -o wide
   ```
   *IP 예시: `10.1.0.7`*

2. `nginx-pod` 내부로 들어가서 Spring Boot 파드로 요청을 보내봅니다.
   ```bash
   kubectl exec -it nginx-pod -- curl [Spring-Boot-IP]:8080
   ```

이처럼 쿠버네티스에서는 파드들이 각자의 고유한 IP를 가지고 클러스터 내부에서 자유롭게 통신할 수 있는 네트워크 환경을 제공합니다.
