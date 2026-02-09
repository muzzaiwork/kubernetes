# [예제] 백엔드(Spring Boot) 서버 3개 띄워보기

### ✅ 백엔드(Spring Boot) 서버 3개 띄워보기

실제 서비스를 운영하다보면 트래픽이 증가해서 서버가 버벅거리는 경우가 생긴다. 이 때는 서버의 성능을 높이거나 개수를 늘려 해결하는데, 이를 **스케일링(Scaling)**이라고 한다.

#### 💡 수평 확장(Scale-out) vs 수직 확장(Scale-up)

1. **수평 확장 (Horizontal Scaling / Scale-out)**
    - **정의**: 서버의 대수를 늘리는 방식이다. (예: 서버 1대 -> 3대)
    - **장점**: 
        - 클라우드 환경에서 매우 유연하게 대응할 수 있다.
        - 여러 대의 서버가 부하를 나누어 가지므로 한 대에 장애가 생겨도 서비스 유지가 가능하다. (고가용성)
    - **쿠버네티스의 강점**: 쿠버네티스는 이 수평 확장을 자동화하고 관리하는 데 최적화되어 있다.

2. **수직 확장 (Vertical Scaling / Scale-up)**
    - **정의**: 서버 자체의 성능(CPU, RAM)을 높이는 방식이다. (예: RAM 8GB -> 32GB)
    - **단점**: 
        - 성능 향상에 한계가 있으며, 작업 시 서버 중단이 필요할 수 있다.
        - 비용 대비 성능 향상 폭이 점차 줄어든다.

이번 실습에서는 Spring Boot 서버를 3대로 늘리는 **수평 확장(Scale-out)** 상황을 가정해 본다.

1. **Spring Boot 프로젝트 구성**
    - `01_pods/springbootapp` 디렉토리의 프로젝트를 사용한다.
    - `DemoApplication.java`에 간단한 컨트롤러를 작성한다.

    **AppController (DemoApplication.java)**
    ```java
    @RestController
    public class DemoApplication {
      @GetMapping("/")
      public String home() {
        System.out.println("Hello, World!"); // 추후 디버깅용
        return "Hello, World!";
      }
    }
    ```

2. **Spring Boot 프로젝트 빌드하기**
    ```bash
    $ gradle clean build
    ```

3. **Dockerfile 작성하기**
    **Dockerfile**
    ```docker
    FROM eclipse-temurin:17-jdk
    COPY build/libs/*SNAPSHOT.jar app.jar
    ENTRYPOINT ["java", "-jar", "/app.jar"]
    ```

4. **Dockerfile을 바탕으로 이미지 빌드하기**
    ```bash
    $ docker build -t spring-server .
    ```

5. **매니페스트 파일 작성하기**
    **spring-pod-triple.yaml**
    ```yaml
    apiVersion: v1
    kind: Pod
    metadata:
      name: spring-pod-1
    spec:
      containers:
        - name: spring-container
          image: spring-server
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
    ---
    apiVersion: v1
    kind: Pod
    metadata:
      name: spring-pod-2
    spec:
      containers:
        - name: spring-container
          image: spring-server
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
    ---
    apiVersion: v1
    kind: Pod
    metadata:
      name: spring-pod-3
    spec:
      containers:
        - name: spring-container
          image: spring-server
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
    ```

6. **매니페스트 파일을 기반으로 파드(Pod) 생성하기**
    ```bash
    $ kubectl apply -f spring-pod-triple.yaml
    ```

7. **파드(Pod) 생성 확인**
    ```bash
    $ kubectl get pods
    ```

8. **파드 삭제하기 (실습 종료 후)**
    ```bash
    $ kubectl delete pod spring-pod-1 spring-pod-2 spring-pod-3
    ```
