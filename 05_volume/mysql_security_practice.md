# [실습] 보안을 위해 외부에서 MySQL 접근 제한하기 (ClusterIP 활용)

### ✅ 목표
- 기존에 `NodePort`로 설정되어 외부(Host PC)에서 직접 접근 가능했던 MySQL 서비스를 `ClusterIP`로 변경하여 보안을 강화한다.
- `ClusterIP` 설정 후에도 쿠버네티스 클러스터 내부의 Spring Boot 서버는 여전히 MySQL에 정상적으로 접속 가능한지 확인한다.

---

### 1. 보안 문제점 파악
기존 설정에서는 `mysql-service`가 `NodePort` 타입으로 생성되어, 호스트 PC의 특정 포트(예: 30004)를 통해 누구나 데이터베이스에 접근할 수 있는 위험이 있었습니다.

---

### 2. 서비스 매니페스트 수정 (`ClusterIP` 적용)

보안을 위해 서비스 타입을 `ClusterIP`로 변경합니다. 실습의 편의를 위해 `NodePort` 버전과 `ClusterIP` 버전의 파일을 각각 준비했습니다.

- **NodePort 버전 (보안 취약)**: `05_volume/mysql-service-nodeport.yaml`
- **ClusterIP 버전 (보안 강화)**: `05_volume/mysql-service-clusterip.yaml`

**mysql-service-clusterip.yaml**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql-service
spec:
  type: ClusterIP # 서비스 타입을 ClusterIP로 변경 (내부 통신 전용)
  selector:
    app: mysql-db
  ports:
    - protocol: TCP
      port: 3306      # 클러스터 내부에서 접근할 포트
      targetPort: 3306 # 파드(컨테이너)의 대상 포트
```

---

### 3. 설정 반영 및 확인

기존의 NodePort 서비스를 삭제하고, ClusterIP 설정을 적용합니다.

```bash
# 기존 서비스 삭제
$ kubectl delete service mysql-service

# 보안이 강화된 ClusterIP 서비스 적용
$ kubectl apply -f 05_volume/mysql-service-clusterip.yaml

# Spring Deployment 재시작 (새로운 ClusterIP 인식)
$ kubectl rollout restart deployment spring-deployment
$ kubectl rollout status deployment spring-deployment
```

---

### 4. 결과 검증

#### 1) 외부 접근 차단 확인
호스트 PC에서 MySQL 포트(30004)로의 접속이 차단되었는지 확인합니다.
```bash
$ nc -zv localhost 30004
# 결과: Connection refused (접속 거부)
```
이제 IntelliJ 등 외부 도구에서 `localhost:30004`로의 직접 접속은 불가능해졌습니다.

#### 2) 클러스터 내부 연동 확인
Spring Boot 서버(30000 포트)에 접속하여 DB 연동 결과가 정상적으로 반환되는지 확인합니다.
```bash
$ curl http://localhost:30000/
# 결과: Hello, World! (정상 응답)
```
Spring Boot는 클러스터 내부의 DNS(`mysql-service`)를 사용하므로, 서비스 타입이 바뀌어도 내부 통신에는 문제가 없습니다.

---

### 5. 그림으로 이해하기 (보안 강화 구조)

![img.png](img.png)

```mermaid
flowchart TD
    subgraph Computer [Host Computer]
        subgraph K8s [Kubernetes Cluster]
            
            subgraph Spring_Layer [Spring Boot Layer]
                Spring_Pod[Spring Pods]
            end

            subgraph MySQL_Layer [MySQL Database Layer]
                MySQL_Pod[MySQL Pod]
            end

            SSVC["Service: spring-service (NodePort)"]
            MSVC["Service: mysql-service (ClusterIP)"]

            %% Traffic Flow
            User((User)) -- "http://localhost:30000" --> SSVC
            SSVC --> Spring_Pod
            Spring_Pod -- "internal DNS: mysql-service" --> MSVC
            MSVC -- "internal ONLY" --> MySQL_Pod
            
            %% Blocked Access
            HUser((External User)) x-- "PORT: 30004 (BLOCKED)" --- MSVC
        end
    end

    %% Styling
    style MSVC fill:#ffcdd2,stroke:#b71c1c
    style SSVC fill:#e0f2f1,stroke:#004d40
    style MySQL_Layer fill:#fff3e0,stroke:#e65100
```

---

### ✅ 요약
- **ClusterIP**는 클러스터 내부 IP만 할당하므로 외부에서 직접 접근할 수 없습니다.
- DB와 같은 민감한 리소스는 `ClusterIP`를 사용하여 내부 애플리케이션만 접근하도록 설정하는 것이 보안상 권장됩니다.
- 외부 노출이 필요한 서비스(예: Web UI)만 `NodePort`나 `LoadBalancer`를 사용합니다.
