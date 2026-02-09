# 서버가 죽었을 때 자동으로 복구하는 기능 (Self-Healing)

### ✅ 실행되고 있는 파드 내 서버가 비정상적으로 종료된다면?

쿠버네티스의 가장 큰 장점 중 하나는 서버(컨테이너)가 예기치 않게 종료되었을 때, 이를 감지하고 자동으로 다시 살려내는 **셀프 힐링(Self-Healing)** 기능이다. 

실제로 작동 중인 컨테이너를 강제로 종료시킨 뒤 쿠버네티스가 어떻게 대응하는지 실습을 통해 확인해 본다.

---

#### 1. 특정 파드의 컨테이너 종료시키기

쿠버네티스 파드 내에서 실제로 돌아가고 있는 도커 컨테이너를 찾아 강제로 종료시킨다.

**① 실행 중인 컨테이너 조회하기**
```bash
$ docker ps | grep spring-container
```

**실행 결과 예시:**
```text
CONTAINER ID   IMAGE          COMMAND                ...   NAMES
ba71a96f8609   spring-server  "java -jar /app.jar"   ...   k8s_spring-container_spring-deployment-xxxx
...
```

**② 컨테이너 종료하기**
조회된 컨테이너 ID 중 하나를 골라 `docker kill` 명령어로 종료시킨다.
```bash
# docker kill [컨테이너 ID]
$ docker kill ba71a96f8609
```

#### 2. 파드 상태 조회 및 복구 확인

컨테이너를 죽인 직후 파드의 상태를 조회해 본다.

```bash
$ kubectl get pods
```

**실행 결과:**
```text
NAME                                 READY   STATUS    RESTARTS     AGE
spring-deployment-7565bdff49-hfvjh   1/1     Running   0            10m
spring-deployment-7565bdff49-mwcc7   1/1     Running   1 (2s ago)   10m
spring-deployment-7565bdff49-wlls7   1/1     Running   0            10m
```

**🔍 확인 포인트:**
- `STATUS`는 여전히 `Running`이다.
- 하지만 `RESTARTS` 항목을 보면 `1`이라고 기록되어 있다.
- 이는 쿠버네티스가 파드 내의 컨테이너가 죽은 것을 감지하고, **즉시 새로운 컨테이너를 생성하여 서버를 재시작**시켰음을 의미한다.

---

### ✅ 요약: 셀프 힐링 (Self-Healing)

쿠버네티스는 사용자가 정의한 **'Desired State(원하는 상태)'**를 유지하기 위해 끊임없이 클러스터를 감시한다.

- **자동 복구**: 컨테이너가 응답하지 않거나 종료되면 자동으로 재시작시킨다.
- **고가용성 유지**: 관리자가 개입하지 않아도 24시간 내내 설정된 서버 개수와 상태를 유지한다.

이처럼 장애 발생 시 스스로를 치유하는 기능을 **셀프 힐링(Self-Healing)** 또는 **자동 복구 기능**이라고 부른다.
