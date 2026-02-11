# [실습] 디플로이먼트, 서비스를 활용해 웹 서버(Nginx) 띄워보기

### ✅ 목표
- AWS EC2(k3s) 환경에서 **Deployment**와 **Service**를 활용해 Nginx 웹 서버를 배포한다.
- **NodePort**를 통해 외부에서 실제 웹 서버에 접속이 가능한지 확인한다.

---

### 1. 매니페스트 파일 작성

**nginx-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
# Deployment 기본 정보
metadata:
  name: nginx-deployment # Deployment 이름
# Deployment 세부 정보
spec:
  replicas: 3 # 생성할 파드의 복제본 개수
  selector:
    matchLabels:
      app: nginx # 아래 template에서 정의한 레이블과 일치하는 파드를 선택
  # 배포할 Pod 정의
  template:
    metadata:
      labels: # 레이블 (= 카테고리)
        app: nginx
    spec:
      containers:
      - name: nginx-container # 컨테이너 이름
        image: nginx # 사용할 이미지
        ports:
        - containerPort: 80 # 컨테이너 내부 포트
```

**nginx-service.yaml**
```yaml
apiVersion: v1
kind: Service
# Service 기본 정보
metadata:
  name: nginx-service # Service 이름
# Service 세부 정보
spec:
  type: NodePort # Service의 종류 (외부 접속 허용)
  selector:
    app: nginx # 'app: nginx' 레이블을 가진 파드와 서비스를 연결
  ports:
    - protocol: TCP
      port: 80 # 쿠버네티스 내부에서 Service에 접속하기 위한 포트
      targetPort: 80 # 파드(컨테이너)의 대상 포트
      nodePort: 30000 # 외부에서 사용자들이 접근하게 될 포트 번호
```

---

### 2. 매니페스트 파일 실행

```bash
# 리소스 생성
$ kubectl apply -f 06_aws_deployment/nginx-deployment.yaml
$ kubectl apply -f 06_aws_deployment/nginx-service.yaml
```

---

### 3. 오브젝트 생성 확인

```bash
# 파드, 디플로이먼트, 서비스 상태 일괄 확인
$ kubectl get pods,deploy,svc -l app=nginx
```

**출력 결과 예시:**
```text
NAME                                    READY   STATUS    RESTARTS   AGE
pod/nginx-deployment-7c79c4bf97-2h5xm   1/1     Running   0          1m
pod/nginx-deployment-7c79c4bf97-5l8pq   1/1     Running   0          1m
pod/nginx-deployment-7c79c4bf97-9kjs2   1/1     Running   0          1m

NAME                               READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/nginx-deployment   3/3     3            3           1m

NAME                    TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
service/nginx-service   NodePort   10.43.155.230   <none>        80:30000/TCP   1m
```

---

### 4. 정상 접속 확인

브라우저에서 `http://[EC2-퍼블릭-IP]:30000`으로 접속하여 Nginx 환영 페이지가 나오는지 확인합니다.
(※ AWS 보안 그룹에서 30000 포트가 열려 있어야 합니다.)

**예상 결과:**
> **Welcome to nginx!**
> If you see this page, the nginx web server is successfully installed and working.

---

### ✅ 생성한 오브젝트 정리

실습을 마친 후에는 리소스를 삭제하여 비용 및 자원을 관리합니다.

```bash
# 생성한 리소스 삭제
$ kubectl delete -f 06_aws_deployment/nginx-deployment.yaml
$ kubectl delete -f 06_aws_deployment/nginx-service.yaml
```

---

### 💡 학습 포인트
1.  **Deployment**: 여러 개의 파드를 원하는 개수(replicas)만큼 유지하고 관리합니다.
2.  **Service (NodePort)**: 클러스터 외부에서 특정 포트(30000-32767)를 통해 파드에 접근할 수 있게 합니다.
3.  **Selector/Labels**: 서비스가 어떤 파드에게 트래픽을 전달할지 결정하는 핵심 연결 고리입니다.
