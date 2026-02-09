# [예제] 디플로이먼트를 활용해 백엔드(Spring Boot) 서버 3개 띄워보기

### ✅ 디플로이먼트를 활용해 백엔드(Spring Boot) 서버 3개 띄워보기

<aside>
🧑🏻‍🏫 **교안**  
실제 서비스를 운영하다 보면 트래픽이 증가해서 서버가 버벅거리는 경우가 생긴다. 이때는 서버를 수평적 확장(서버의 개수를 늘리는 방식)을 통해 해결한다. 이런 상황을 가정해 백엔드 서버인 Spring Boot 서버를 3대로 늘려본다.
</aside>

---

### 1. 매니페스트 파일 비교하기

이전 실습에서는 파드 3개를 띄우기 위해 각각의 파드를 정의해야 했다. 하지만 디플로이먼트를 사용하면 하나의 설정으로 여러 개의 파드를 관리할 수 있다.

#### ❌ 기존 방식 (spring-pod-triple.yaml)
각각의 파드 이름을 다르게 지정하여 3번 정의해야 했다.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod-1
...
---
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod-2
...
```

#### ⭕️ 디플로이먼트 방식 (spring-deployment.yaml)
`replicas: 3` 설정 하나로 동일한 파드 3개를 생성한다.
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-deployment
spec:
  replicas: 3 # 파드 개수 지정
  selector:
    matchLabels:
      app: backend-app
  template:
    metadata:
      labels:
        app: backend-app
    spec:
      containers:
        - name: spring-container
          image: spring-server
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
```

---

### 2. 기존 파드 삭제하기

새로운 방식으로 배포하기 전에, 이전에 개별적으로 생성했던 파드들을 삭제한다.
```bash
$ kubectl delete pod spring-pod-1 spring-pod-2 spring-pod-3
$ kubectl get pods # 삭제 확인
```

---

### 3. 디플로이먼트 생성하기

매니페스트 파일을 클러스터에 적용한다.
```bash
$ kubectl apply -f 02_deployments/spring-deployment.yaml
```

---

### 4. 생성 결과 확인하기

디플로이먼트가 생성되면 자동으로 레플리카셋과 파드가 생성된다.

```bash
# 디플로이먼트 확인
$ kubectl get deployment

# 레플리카셋 확인 (디플로이먼트가 생성함)
$ kubectl get replicaset

# 파드 확인 (레플리카셋이 생성함)
$ kubectl get pods
```

![Deployment Result](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/4f5e9ce4-4351-42ec-aa9e-845442be047c/359923ea-bd9f-4a95-bf9c-80e004f1e8f2.png)

---

### ✅ 전체 구조 파악

![Architecture](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/4a0ba65b-7081-4170-9ecf-912b1a521f4d/image.png)

백엔드 서버 3개를 각각의 파드에 띄웠다. 하지만 실제 서비스 시에는 사용자 요청을 이 3개의 서버에 골고루 나누어주어야 한다.

> **다음 단계**: 파드 앞단에서 트래픽을 균등하게 분배해주는 **서비스(Service)**에 대해 알아본다.
