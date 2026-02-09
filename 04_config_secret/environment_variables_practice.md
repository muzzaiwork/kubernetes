# [예제] 백엔드(Spring Boot) 서버에 환경변수 등록해 사용하기

### ✅ 백엔드(Spring Boot) 서버에 환경변수 등록해 사용하기

쿠버네티스에서 애플리케이션을 실행할 때, 데이터베이스 주소나 API 키와 같은 설정 정보들을 환경변수를 통해 전달해야 할 때가 많다. 이번 실습에서는 Spring Boot 서버에 환경변수를 등록하고 사용하는 방법을 알아본다.

---

### 1. Spring Boot 코드 수정

기존의 `DemoApplication.java` 코드를 수정하여 환경변수를 읽어오도록 설정한다. `@Value` 어노테이션을 사용하면 환경변수 값을 필드에 주입받을 수 있다.

**DemoApplication.java**
```java
@org.springframework.beans.factory.annotation.Value("${MY_ACCOUNT:default}")
private String myAccount;

@org.springframework.beans.factory.annotation.Value("${MY_PASSWORD:default}")
private String myPassword;

@GetMapping("/")
public String home() {
    return "myAccount: " + myAccount + ", myPassword: " + myPassword;
}
```
- `${MY_ACCOUNT:default}`: `MY_ACCOUNT`라는 이름의 환경변수 값을 가져온다. 만약 환경변수가 설정되어 있지 않으면 `default`라는 값을 기본값으로 사용한다.

---

### 2. 이미지 빌드 및 태그 지정

수정한 코드를 빌드하고 새로운 태그(`env`)를 붙여 이미지를 생성한다.

```bash
# 프로젝트 빌드
$ ./gradlew clean build

# 이미지 빌드 (태그: env)
$ docker build -t spring-server:env .
```

---

### 3. 매니페스트 파일 작성 (`spring-env-deployment.yaml`)

디플로이먼트의 `env` 항목을 통해 컨테이너 내부에서 사용할 환경변수를 정의한다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-env-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-env-app
  template:
    metadata:
      labels:
        app: spring-env-app
    spec:
      containers:
        - name: spring-container
          image: spring-server:env
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env: # 환경변수 등록
            - name: MY_ACCOUNT
              value: jaeseong
            - name: MY_PASSWORD
              value: pwd1234
```

---

### 4. 서비스 생성 및 확인

외부에서 접속하여 환경변수가 잘 적용되었는지 확인하기 위해 서비스를 생성한다.

```bash
# 리소스 생성
$ kubectl apply -f 04_config_secret/spring-env-deployment.yaml
$ kubectl apply -f 04_config_secret/spring-env-service.yaml

# 접속 확인 (NodePort: 30001)
$ curl localhost:30001
```

**실행 결과:**
```text
myAccount: jaeseong, myPassword: pwd1234
```
매니페스트 파일에 적어준 환경변수 값이 정상적으로 출력되는 것을 확인할 수 있다.

---

### 5. 파드 내부에서 환경변수 조회하기

실제로 컨테이너 내부에 환경변수가 어떻게 들어가 있는지 직접 확인할 수도 있다.

```bash
# 실행 중인 파드 이름 확인
$ kubectl get pods

# 파드 내부로 접속
$ kubectl exec -it [파드명] -- bash

# 환경변수 전체 조회
$ env | grep MY_
```

**실행 결과:**
```text
MY_ACCOUNT=jaeseong
MY_PASSWORD=pwd1234
```

---

### ✅ 요약

1.  **애플리케이션 코드**: 환경변수를 읽어오도록 작성한다 (예: Spring의 `@Value`).
2.  **매니페스트(`env`)**: `spec.containers[].env` 항목 아래에 `name`과 `value` 쌍으로 환경변수를 정의한다.
3.  **적용**: `kubectl apply`를 통해 클러스터에 배포하면 컨테이너 기동 시 환경변수가 주입된다.

하지만 이렇게 매니페스트 파일에 직접 중요한 정보를 적는 것은 보안상 좋지 않다. 다음 단계에서는 이러한 설정값들을 별도로 관리하는 **ConfigMap**과 **Secret**에 대해 알아본다.
