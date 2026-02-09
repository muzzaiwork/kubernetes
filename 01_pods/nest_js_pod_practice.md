# Nest.js 파드(Pod) 띄우기 실습

Spring Boot에 이어, Node.js 기반의 프레임워크인 **Nest.js** 애플리케이션을 파드로 띄워보는 실습을 진행한다.

## 1. 실습 준비

### ① Nest.js 프로젝트 생성
로컬 환경에 Nest.js CLI가 설치되어 있어야 한다. (이미 프로젝트가 있다면 이 단계를 건너뛰어도 된다.)

```bash
# Nest CLI 설치 (필요한 경우)
$ npm i -g @nestjs/cli

# 새 프로젝트 생성
$ nest new nestapp
```
*생성 시 패키지 매니저는 `npm`을 권장합니다.*

### ② 프로젝트 실행 확인
Docker 이미지로 만들기 전, 로컬에서 정상적으로 실행되는지 확인한다.

```bash
$ cd nestapp
$ npm i
$ npm run start
```
기본적으로 `http://localhost:3000`에서 실행된다.

---

## 2. Docker 이미지 빌드

Nest.js 프로젝트 루트 디렉토리에 `Dockerfile`과 `.dockerignore` 파일을 작성한다.

### ① Dockerfile 작성 (`01_pods/nestapp/Dockerfile`)
```docker
FROM node

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

EXPOSE 3000

ENTRYPOINT [ "node", "dist/main.js" ]
```

### ② .dockerignore 작성 (`01_pods/nestapp/.dockerignore`)
이미지 빌드 시 불필요한 파일을 제외한다.
```text
node_modules
```

### ③ 이미지 빌드
```bash
$ docker build -t nestapp .
$ docker image ls  # nestapp 이미지 생성 확인
```

---

## 3. 쿠버네티스 파드 배포

### ① 매니페스트 파일 작성 (`01_pods/nest-pod.yaml`)
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nest-pod
spec:
  containers:
    - name: nest-container
      image: nestapp
      imagePullPolicy: IfNotPresent  # 로컬 이미지를 우선 사용
      ports:
        - containerPort: 3000         # Nest.js 기본 포트
```

### ② 파드 생성 및 상태 확인
```bash
$ kubectl apply -f 01_pods/nest-pod.yaml
$ kubectl get pods
```

**실행 결과:**
```text
NAME       READY   STATUS    RESTARTS   AGE
nest-pod   1/1     Running   0          15s
```

---

## 4. 접속 확인 및 검증

### ① 포트 포워딩 (Port Forwarding)
호스트의 3000 포트를 파드의 3000 포트로 연결한다.
```bash
$ kubectl port-forward pod/nest-pod 3000:3000
```
브라우저에서 `http://localhost:3000`에 접속하여 "Hello World!"(또는 설정한 응답)가 나오는지 확인한다.

### ② 파드 내부 접속 (kubectl exec)
파드 내부에서 직접 응답을 확인해 볼 수도 있다.
```bash
$ kubectl exec -it nest-pod -- bash
# (파드 내부)
$ curl localhost:3000
```

---

## 5. 정리 (Nginx vs Spring Boot vs Nest.js)

| 항목 | Nginx | Spring Boot | Nest.js |
| :--- | :--- | :--- | :--- |
| **기본 포트** | 80 | 8080 | 3000 |
| **런타임** | Nginx Engine | JVM (Java) | Node.js |
| **이미지 베이스** | nginx:alpine 등 | openjdk 등 | node 등 |

각 프레임워크마다 사용하는 포트와 환경이 다르지만, 쿠버네티스에서는 동일한 **파드(Pod)**라는 단위로 추상화되어 관리됨을 알 수 있다.
