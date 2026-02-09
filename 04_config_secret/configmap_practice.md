# [예제] 컨피그맵(ConfigMap)을 활용해 환경변수 분리하기

### ✅ 컨피그맵(ConfigMap)이란?

애플리케이션을 운영하다 보면 개발(Dev), 테스트(Test), 운영(Prod) 환경에 따라 설정값(DB 주소, API 키 등)이 달라진다. 이때 매번 매니페스트 파일의 `env` 항목을 직접 수정하는 것은 번거롭고 실수할 위험이 크다.

쿠버네티스에서는 **환경변수나 설정값들을 별도의 오브젝트로 분리하여 관리**할 수 있는 기능을 제공하는데, 이것이 바로 **컨피그맵(ConfigMap)**이다.

---

### ✅ 컨피그맵을 사용하면 좋은 점

1.  **유연한 관리**: 디플로이먼트 코드는 그대로 두고, 컨피그맵만 교체하여 환경별 설정값을 쉽게 변경할 수 있다.
2.  **재사용성**: 하나의 컨피그맵을 여러 디플로이먼트에서 공유하여 사용할 수 있다.
3.  **가독성**: 비즈니스 로직(Deployment)과 환경 설정(ConfigMap)이 분리되어 매니페스트 파일이 깔끔해진다.

---

### 1. 컨피그맵 매니페스트 작성 (`spring-config.yaml`)

설정값을 Key-Value 형식으로 정의한다.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: spring-config # 컨피그맵의 이름
data:
  my-account: jscode
  my-password: password123
```

---

### 2. 디플로이먼트에서 컨피그맵 참조하기 (`spring-config-deployment.yaml`)

`env` 섹션에서 직접 값을 적는 대신, `valueFrom`을 사용하여 컨피그맵의 값을 가져오도록 수정한다.

```yaml
...
          env:
            - name: MY_ACCOUNT
              valueFrom:
                configMapKeyRef:
                  name: spring-config # 참조할 컨피그맵 이름
                  key: my-account     # 컨피그맵에 정의된 Key
            - name: MY_PASSWORD
              valueFrom:
                configMapKeyRef:
                  name: spring-config
                  key: my-password
...
```

---

### 3. 실습 진행 및 확인

매니페스트 파일을 적용하고 결과가 잘 나오는지 확인한다.

```bash
# 1. 컨피그맵 및 디플로이먼트 적용
$ kubectl apply -f 04_config_secret/spring-config.yaml
$ kubectl apply -f 04_config_secret/spring-config-deployment.yaml
$ kubectl apply -f 04_config_secret/spring-config-service.yaml

# 2. 접속 확인 (NodePort: 30002)
$ curl localhost:30002
```

**실행 결과:**
```text
myAccount: jscode, myPassword: password123
```
컨피그맵에 설정한 `jscode`와 `password123` 값이 정상적으로 출력되는 것을 확인할 수 있다.

---

### 4. 컨피그맵 수정 후 반영하기 (Restart)

컨피그맵의 값을 수정하더라도 이미 실행 중인 파드에는 즉시 반영되지 않는다. 이때는 디플로이먼트를 재시작하여 새로운 설정값을 불러와야 한다.

```bash
# 1. 컨피그맵 수정 (예: jscode -> new-jscode) 후 다시 apply
$ kubectl apply -f 04_config_secret/spring-config.yaml

# 2. 디플로이먼트 재시작 (롤링 업데이트 유도)
$ kubectl rollout restart deployment spring-config-deployment
```

---

### ✅ 요약

1.  **ConfigMap**: 환경변수나 설정값들을 모아놓은 전용 오브젝트다.
2.  **참조**: 디플로이먼트에서 `valueFrom.configMapKeyRef`를 통해 필요한 값을 불러와 환경변수로 등록한다.
3.  **분리**: 설정과 배포 로직을 분리함으로써 더 유연하고 깔끔한 운영이 가능해진다.
