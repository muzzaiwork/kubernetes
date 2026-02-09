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

### ② 상태 확인
Spring Boot는 Java 애플리케이션이므로 Nginx보다 기동 시간이 조금 더 걸릴 수 있습니다. `STATUS`가 `Running`이 될 때까지 기다립니다.
```bash
kubectl get pods
```

**실행 결과 (예시):**
```text
NAME         READY   STATUS    RESTARTS   AGE
nginx-pod    1/1     Running   0          25m
spring-pod   1/1     Running   0          20s
```

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

## 4. 왜 ImagePullBackOff가 발생하나요? (이미지 풀 정책)

이전에 Spring Boot 프로젝트를 이미지로 빌드해서 파드로 띄웠을 때, `ImagePullBackOff`라는 에러가 발생할 수 있습니다. 이는 쿠버네티스의 **이미지 풀 정책(Image Pull Policy)** 때문입니다.

- 상세 설명: [이미지 풀 정책 이해하기](../01_concepts/image_pull_policy.md)

### ① 이미지 풀 정책 (Image Pull Policy)이란?
쿠버네티스가 매니페스트 파일을 읽어 파드를 생성할 때, 이미지를 어떻게 가져올(Pull) 것인지에 대한 규칙입니다.

1. **`Always`**: 로컬에 이미지가 있더라도 무조건 원격 레지스트리(Docker Hub 등)에서 새로 가져옵니다.
2. **`IfNotPresent`**: 로컬에 이미지가 있는지 먼저 확인하고, 없을 때만 원격에서 가져옵니다. (권장)
3. **`Never`**: 무조건 로컬에 있는 이미지명만 사용하며 원격에서 가져오지 않습니다.

### ② 기본 동작 방식 (Default Behavior)
`imagePullPolicy`를 생략했을 때 쿠버네티스는 다음과 같이 판단합니다.

- 이미지 태그가 `:latest`이거나 아예 없는 경우: **`Always`**로 설정됨
- 이미지 태그가 특정 버전(예: `:v1`)인 경우: **`IfNotPresent`**로 설정됨

따라서 우리가 태그 없이 `image: spring-server`라고만 적으면 쿠버네티스는 `spring-server:latest`를 원격 저장소에서 찾으려 시도하고, 찾지 못해 `ImagePullBackOff` 에러를 띄우게 됩니다.

### ③ 해결 방법
로컬에서 빌드한 이미지를 사용하려면 다음과 같이 정책을 명시해 주어야 합니다.

```yaml
spec:
  containers:
    - name: spring-container
      image: spring-server
      imagePullPolicy: IfNotPresent
```

정책을 수정한 후 기존 파드를 삭제하고 다시 생성하면 정상적으로 실행됩니다.

```bash
$ kubectl delete pod spring-pod
$ kubectl apply -f 01_pods/spring-pod.yaml
$ kubectl get pods
```

---

## 5. Spring Boot 서버 응답 확인하기

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

## 6. Nginx 파드 vs Spring Boot 파드 비교

| 항목 | Nginx 파드 | Spring Boot 파드 |
| :--- | :--- | :--- |
| **기본 포트** | 80 | 8080 |
| **기동 속도** | 매우 빠름 | 보통 (JVM 실행 시간 필요) |
| **용도** | 정적 파일 서빙, 리버스 프록시 | 비즈니스 로직 처리 (API 서버 등) |

---

## 5. 파드 내부에서 통신해보기 (고급)

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
