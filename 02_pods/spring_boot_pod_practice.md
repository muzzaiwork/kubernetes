# Spring Boot 파드(Pod) 띄우기 실습

앞서 실습한 Nginx 파드에 이어, 실제 웹 애플리케이션인 **Spring Boot**를 파드로 띄워보고 Nginx와의 차이점을 이해해 본다.

## 1. Spring Boot 매니페스트 분석 (`spring-pod.yaml`)

Spring Boot 애플리케이션을 위한 매니페스트 파일이다. 로컬에서 빌드한 이미지를 사용하는 예시이다.

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
- **containerPort: 8080**: Spring Boot의 기본 포트인 8080을 사용한다. Nginx(80)와 포트 번호가 다름에 유의한다.
- **imagePullPolicy: IfNotPresent**: 로컬 Docker 엔진에 이미지가 있으면 원격에서 찾지 않고 로컬 이미지를 사용한다.

---

## 2. 직접 만든 Spring Boot 이미지로 실습하기 (로컬 빌드 → 로컬 배포)

직접 작성한 코드를 Docker 이미지로 만들어서 쿠버네티스 파드로 띄워보는 전체 과정을 실습한다.

### ① 프로젝트 준비 및 빌드
실습을 위해 준비된 Spring Boot 프로젝트를 빌드하여 JAR 파일을 생성한다.

- 예제 소스: `02_pods/springbootapp/`
- Dockerfile: `02_pods/springbootapp/Dockerfile`

```bash
$ cd 02_pods/springbootapp
$ ./gradlew clean build   # JAR 파일 빌드
```

### ② Docker 이미지 빌드
Dockerfile을 바탕으로 `spring-server`라는 이름의 이미지를 로컬에 생성한다.

```bash
$ docker build -t spring-server .
$ docker image ls          # 이미지 생성 확인
```

### ③ 파드 생성 및 상태 확인 (ImagePullBackOff 발생)
로컬 이미지를 사용하는 매니페스트 파일을 적용한다.

`02_pods/spring-pod.yaml`
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod
spec:
  containers:
    - name: spring-container
      image: spring-server
      # imagePullPolicy를 생략하거나 Always로 설정한 경우 에러 발생 가능
      ports:
        - containerPort: 8080
```

```bash
$ kubectl apply -f 02_pods/spring-pod.yaml
$ kubectl get pods
```

**실행 결과 (에러 발생 시):**
```text
NAME         READY   STATUS             RESTARTS   AGE
spring-pod   0/1     ImagePullBackOff   0          10s
```

#### ❓ 왜 ImagePullBackOff가 발생하나요?
이미지 태그를 명시하지 않으면 쿠버네티스는 기본적으로 `latest` 태그를 사용하며, 이미지 풀 정책을 **`Always`**로 설정한다. 이 경우 쿠버네티스는 로컬에 이미지가 있더라도 무조건 원격 레지스트리(Docker Hub 등)에서 이미지를 찾으려 시도한다. `spring-server`는 로컬에서만 만든 이미지이므로 원격에서 찾지 못해 에러가 발생하는 것이다.

- 상세 설명: [이미지 풀 정책(ImagePullPolicy) 이해하기](../01_concepts/image_pull_policy.md)

### ④ 문제 해결: 매니페스트 수정 및 재배포
로컬 이미지를 우선적으로 사용하도록 `imagePullPolicy: IfNotPresent`를 추가한다.

```yaml
spec:
  containers:
    - name: spring-container
      image: spring-server
      imagePullPolicy: IfNotPresent  # 로컬에 있으면 가져오지 않음
```

**재배포 절차:**
```bash
$ kubectl delete pod spring-pod
$ kubectl apply -f 02_pods/spring-pod.yaml
$ kubectl get pods  # STATUS가 Running인지 확인
```

---

## 3. Spring Boot 서버 응답 확인하기

서버가 정상적으로 기동되었다면 아래 방법들로 응답을 확인할 수 있다.

### ① 임시 접속 확인 (Port Forwarding)
호스트의 8081 포트를 파드의 8080 포트로 연결해 본다.
```bash
$ kubectl port-forward pod/spring-pod 8081:8080
```
이제 브라우저에서 `http://localhost:8081`에 접속하면 "Hello World!" 메시지를 확인할 수 있다.

### ② 파드 내부에서 요청 보내기
`kubectl exec`를 통해 파드 내부 쉘에 접속하여 직접 `curl`을 날려본다.
```bash
$ kubectl exec -it spring-pod -- bash 
$ curl localhost:8080
```

---

## 4. Nginx 파드 vs Spring Boot 파드 비교

| 항목 | Nginx 파드 | Spring Boot 파드 |
| :--- | :--- | :--- |
| **기본 포트** | 80 | 8080 |
| **기동 속도** | 매우 빠름 | 보통 (JVM 실행 시간 필요) |
| **용도** | 정적 파일 서빙, 리버스 프록시 | 비즈니스 로직 처리 (API 서버 등) |

---

## 5. 파드 내부에서 통신해보기 (고급)

쿠버네티스 클러스터 안에서는 파드끼리 서로의 IP를 통해 통신할 수 있다. 

1. 먼저 `spring-pod`의 IP를 확인한다.
   ```bash
   kubectl get pod spring-pod -o wide
   ```
   *IP 예시: `10.1.0.7`*

2. `nginx-pod` 내부로 들어가서 Spring Boot 파드로 요청을 보내본다.
   ```bash
   kubectl exec -it nginx-pod -- curl [Spring-Boot-IP]:8080
   ```

이처럼 쿠버네티스에서는 파드들이 각자의 고유한 IP를 가지고 클러스터 내부에서 자유롭게 통신할 수 있는 네트워크 환경을 제공한다.
