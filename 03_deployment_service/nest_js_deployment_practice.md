# [예제] 디플로이먼트, 서비스를 활용해 백엔드(Nest.js) 서버 띄워보기

이전 실습에서 배운 디플로이먼트와 서비스의 개념을 바탕으로, Nest.js 프레임워크를 사용한 백엔드 서버를 구축하고 업데이트하는 전 과정을 실습한다.

---

### ✅ 실습 요구 사항
- 파드 4개 생성 (Replicas: 4)
- 외부에서 `http://localhost:31000`으로 통신 가능하도록 설정 (NodePort)
- 서버 응답을 수정하여 버전 업데이트 수행 (v1.0 -> v1.1)

---

### 1. 프로젝트 준비 및 이미지 빌드

**① Dockerfile 작성**
Nest.js 프로젝트 루트에 아래와 같이 Dockerfile을 작성한다.
```docker
FROM node
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build
EXPOSE 3000
ENTRYPOINT [ "node", "dist/main.js" ]
```

**② 이미지 빌드 (v1.0)**
```bash
$ docker build -t nest-server:1.0 .
```

---

### 2. 매니페스트 파일 생성 및 배포

**① nest-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nest-deployment
spec:
  replicas: 4
  selector:
    matchLabels:
      app: nest-backend
  template:
    metadata:
      labels:
        app: nest-backend
    spec:
      containers:
        - name: nest-container
          image: nest-server:1.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 3000
```

**② nest-service.yaml**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nest-service
spec:
  type: NodePort
  selector:
    app: nest-backend
  ports:
    - protocol: TCP
      port: 3000       # 서비스 내부 포트
      targetPort: 3000 # 파드 포트
      nodePort: 31000  # 외부 접속 포트
```

**③ 오브젝트 생성**
```bash
$ kubectl apply -f 03_deployment_service/nest-deployment.yaml
$ kubectl apply -f 03_deployment_service/nest-service.yaml
```

**④ 접속 확인**
```bash
$ curl localhost:31000
# 출력: Hello World!
```

---

### 3. 버전 업데이트 (v1.1)

애플리케이션 코드를 수정하고 새로운 버전으로 업데이트한다.

**① 소스 코드 수정 (`app.service.ts`)**
```typescript
getHello(): string {
  return 'Hi World!'; // 'Hello World!'에서 변경
}
```

**② 새로운 버전 이미지 빌드**
```bash
$ docker build -t nest-server:1.1 .
```

**③ 매니페스트 수정 및 적용**
`nest-deployment.yaml`의 이미지를 `nest-server:1.1`로 수정 후 적용한다.
```bash
$ kubectl apply -f 03_deployment_service/nest-deployment.yaml
```

**④ 업데이트 확인**
```bash
$ curl localhost:31000
# 출력: Hi World!
```

---

### 💡 실습 포인트
- **라벨 매칭**: 서비스의 `selector`와 디플로이먼트의 `labels`가 `nest-backend`로 정확히 일치해야 트래픽이 전달된다.
- **포트 구성**: 컨테이너 내부 포트(3000) -> 서비스 포트(3000) -> 노드 포트(31000)로 이어지는 흐름을 이해한다.
- **무중단 업데이트**: `kubectl rollout status` 명령을 통해 파드 4개가 순차적으로 교체되는 과정을 관찰할 수 있다.
