# 디플로이먼트를 활용한 서버 개수 조절 방법 (Scaling)

### ✅ 트래픽이 늘어나서 서버를 5개로 늘리고 싶다면?

디플로이먼트(Deployment)를 활용하면 매니페스트 파일의 설정값 하나만 바꿔주는 것으로 서버의 개수를 아주 쉽게 늘릴 수 있다. 

---

#### 1. 매니페스트 파일 수정

기존 `3`이었던 `replicas` 값을 `5`로 수정한다.

**spring-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-deployment
spec:
  replicas: 5  # 3에서 5로 변경
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

#### 2. 변경사항 적용

수정된 파일을 쿠버네티스 클러스터에 다시 적용한다.

```bash
$ kubectl apply -f 03_deployment_service/spring-deployment.yaml 
```

`kubectl apply` 명령어는 새롭게 오브젝트(디플로이먼트, 파드 등)를 생성할 때뿐만 아니라, 기존 설정의 **변경 사항을 적용**시킬 때도 사용할 수 있는 매우 편리한 명령어이다.

#### 3. 잘 적용됐는지 확인하기

파드의 개수가 5개로 늘어났는지 확인한다.

```bash
$ kubectl get pods
```

**실행 결과 예시:**
```text
NAME                                 READY   STATUS    RESTARTS   AGE
spring-deployment-68cc49885b-4qptv   1/1     Running   0          5m
spring-deployment-68cc49885b-6z5lw   1/1     Running   0          5m
spring-deployment-68cc49885b-n8v99   1/1     Running   0          5m
spring-deployment-68cc49885b-abcde   1/1     Running   0          10s
spring-deployment-68cc49885b-fghij   1/1     Running   0          10s
```
새로운 파드 2개가 추가로 생성되어 총 5개의 파드가 `Running` 상태인 것을 확인할 수 있다.

---

### 💡 정리
- **수평 확장 (Scale-out)**: `replicas` 값을 늘려 서버의 처리 능력을 높이는 작업.
- **선언적 관리**: 파일의 내용을 "내가 원하는 상태"로 고치고 `apply`만 하면 쿠버네티스가 알아서 그 상태를 만들어 준다.
