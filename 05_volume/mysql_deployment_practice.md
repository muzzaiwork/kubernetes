# [예제] 디플로이먼트(Deployment)를 활용해 MySQL 실행시키기

### ✅ 디플로이먼트(Deployment)를 활용해 MySQL 실행시키기

데이터베이스와 같이 데이터를 저장하는 애플리케이션을 쿠버네티스에서 실행할 때, 볼륨 설정이 왜 중요한지 직접 확인해본다.

---

### 1. 매니페스트 파일 작성

환경 변수 관리를 위한 ConfigMap, Secret과 함께 MySQL 디플로이먼트 및 서비스를 생성한다.

**mysql-secret.yaml**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
stringData:
  mysql-root-password: password123
```

**mysql-config.yaml**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-config
data:
  mysql-database: kub-practice
```

**mysql-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql-db
  template:
    metadata:
      labels:
        app: mysql-db
    spec:
      containers:
        - name: mysql-container
          image: mysql:8.0
          ports:
            - containerPort: 3306
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: mysql-root-password
            - name: MYSQL_DATABASE
              valueFrom:
                configMapKeyRef:
                  name: mysql-config
                  key: mysql-database
```

**mysql-service.yaml**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql-service
spec:
  type: NodePort
  selector:
    app: mysql-db
  ports:
    - protocol: TCP
      port: 3306
      targetPort: 3306
      nodePort: 30004
```

---

### 2. 리소스 생성 및 확인

```bash
$ kubectl apply -f 05_volume/mysql-secret.yaml
$ kubectl apply -f 05_volume/mysql-config.yaml
$ kubectl apply -f 05_volume/mysql-deployment.yaml
$ kubectl apply -f 05_volume/mysql-service.yaml

# 파드 실행 확인
$ kubectl get pods -l app=mysql-db
```

---

### 3. 데이터베이스 접속 및 데이터 생성

MySQL 파드 내부로 접속하여 새로운 데이터베이스를 생성해본다. (IntelliJ 등 GUI 도구에서 `localhost:30004`로 접속해도 동일하다.)

```bash
# 파드 내부에서 MySQL 접속 및 DB 생성
$ kubectl exec -it deployment/mysql-deployment -- mysql -u root -ppassword123 -e "CREATE DATABASE new_db; SHOW DATABASES;"
```

**실행 결과:**
```text
+--------------------+
| Database           |
+--------------------+
| information_schema |
| kub-practice       |
| mysql              |
| new_db             |
| performance_schema |
| sys                |
+--------------------+
```
`new_db`가 정상적으로 생성된 것을 확인할 수 있다.

---

### 4. MySQL 재시작 (데이터 휘발성 테스트)

디플로이먼트를 재시작하여 기존 파드를 삭제하고 새로운 파드를 띄워본다.

```bash
# 디플로이먼트 재시작
$ kubectl rollout restart deployment mysql-deployment

# 새로운 파드가 뜰 때까지 대기
$ kubectl rollout status deployment mysql-deployment
```

재시작 완료 후 다시 데이터베이스 목록을 조회해본다.

```bash
$ kubectl exec -it deployment/mysql-deployment -- mysql -u root -ppassword123 -e "SHOW DATABASES;"
```

**실행 결과:**
```text
+--------------------+
| Database           |
+--------------------+
| information_schema |
| kub-practice       |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```
**이전에 생성했던 `new_db`가 사라진 것을 확인할 수 있다.**

---

### ✅ 결론 및 문제점

- **파드의 일회성**: 파드는 언제든 삭제되고 다시 생성될 수 있으며, 이때 파드 내부(컨테이너 내 데이터)에 저장된 데이터는 모두 유실된다.
- **볼륨의 필요성**: MySQL과 같이 데이터 보존이 필수적인 애플리케이션은 파드가 재시작되어도 데이터가 유지되도록 **외부 저장소(Volume)**를 연결해야 한다.

> **다음 단계**: 파드가 재시작되어도 데이터가 유지되는 **퍼시스턴트 볼륨(PV/PVC)** 실습을 진행한다.
