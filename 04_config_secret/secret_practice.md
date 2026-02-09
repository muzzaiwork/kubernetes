# [예제] 시크릿(Secret)을 활용해 ‘민감한 값’을 환경 변수로 분리하기

### ✅ 시크릿(Secret)이란?

시크릿(Secret)은 컨피그맵(ConfigMap)과 마찬가지로 설정값을 애플리케이션 외부에서 관리하기 위해 사용한다. 하지만 컨피그맵과 결정적인 차이점이 있는데, 바로 **비밀번호, API 키, 인증서와 같이 보안상 중요한 데이터(Sensitive Data)**를 다루는 데 특화되어 있다는 점이다.

- **ConfigMap**: 일반적인 설정값 (DB 주소, 서비스 이름 등)
- **Secret**: 민감한 설정값 (DB 비밀번호, 개인 키, Access Token 등)

---

### 1. 시크릿 매니페스트 작성 (`spring-secret.yaml`)

보안이 필요한 값을 `stringData` 항목에 정의한다. 쿠버네티스 내부에서는 이 값이 Base64로 인코딩되어 저장된다.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: spring-secret # 시크릿의 이름
type: Opaque         # 일반적인 용도의 시크릿 타입
stringData:
  my-password: my-secret-password # 평문으로 작성하면 쿠버네티스가 인코딩해줌
```

---

### 2. 디플로이먼트에서 시크릿 참조하기 (`spring-secret-deployment.yaml`)

`env` 섹션에서 `secretKeyRef`를 사용하여 시크릿의 값을 가져오도록 설정한다.

```yaml
...
          env:
            - name: MY_ACCOUNT
              valueFrom:
                configMapKeyRef:
                  name: spring-config # 컨피그맵 참조
                  key: my-account
            - name: MY_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: spring-secret # 시크릿 참조
                  key: my-password
...
```

---

### 3. 실습 진행 및 확인

매니페스트 파일을 적용하고 결과가 잘 나오는지 확인한다.

```bash
# 1. 시크릿 및 관련 리소스 적용
$ kubectl apply -f 04_config_secret/spring-secret.yaml
$ kubectl apply -f 04_config_secret/spring-config.yaml
$ kubectl apply -f 04_config_secret/spring-secret-deployment.yaml
$ kubectl apply -f 04_config_secret/spring-secret-service.yaml

# 2. 접속 확인 (NodePort: 30003)
$ curl localhost:30003
```

**실행 결과:**
```text
myAccount: jscode, myPassword: my-secret-password
```
컨피그맵에서 가져온 계정명(`jscode`)과 시크릿에서 가져온 비밀번호(`my-secret-password`)가 정상적으로 결합되어 출력되는 것을 확인할 수 있다.

---

### 4. 시크릿 내부 데이터 확인 (심화)

쿠버네티스에서 시크릿은 어떻게 저장되어 있는지 확인해 본다.

```bash
# 시크릿 정보 상세 조회 (YAML 형식)
$ kubectl get secret spring-secret -o yaml
```

**조회 결과 (일부):**
```yaml
data:
  my-password: bXktc2VjcmV0LXBhc3N3b3Jk  # Base64로 인코딩된 값
```
`stringData`에 넣었던 값은 `data` 항목 아래에 Base64로 인코딩된 문자열로 저장된다. (참고: Base64는 암호화가 아니므로 누구나 디코딩할 수 있다. 실제 운영 환경에서는 RBAC이나 KMS 연동 등을 통해 보안을 강화해야 한다.)

---

### ✅ 요약

1.  **Secret**: 보안이 중요한 민감한 데이터를 관리하는 오브젝트다.
2.  **참조**: 디플로이먼트에서 `valueFrom.secretKeyRef`를 통해 값을 불러온다.
3.  **구분**: 일반 설정은 **ConfigMap**, 민감 정보는 **Secret**에 나누어 관리하는 것이 쿠버네티스 운영의 정석이다.
