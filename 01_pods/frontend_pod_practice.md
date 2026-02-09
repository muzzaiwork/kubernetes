# 프론트엔드(HTML, CSS, Nginx) 서버를 파드로 띄워보기

단순한 정적 웹 페이지(HTML, CSS)를 Nginx 이미지에 포함시켜 도커 이미지를 빌드하고, 이를 쿠버네티스 파드로 띄워보는 실습입니다.

## 1. 정적 자원 및 Dockerfile 준비

실습을 위해 `01_pods/frontend/` 디렉토리에 다음 파일들을 준비합니다.

### index.html
Nginx의 기본 설정에 따라 메인 페이지 파일명은 `index.html`이어야 합니다.

```html
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>My Web Page</h1>
</body>
</html>
```

### style.css
간단한 스타일을 적용합니다.

```css
* {
  color: blue;
}
```

### Dockerfile
Nginx 공식 이미지를 기반으로, 현재 디렉토리의 파일들을 Nginx의 기본 웹 루트 디렉토리로 복사합니다.

```dockerfile
FROM nginx 
COPY ./ /usr/share/nginx/html
```

---

## 2. 이미지 빌드 및 확인

터미널에서 이미지를 빌드합니다.

```bash
# frontend 디렉토리 내에서 빌드
$ docker build -t my-web-server 01_pods/frontend/
```

이미지가 잘 생성되었는지 확인합니다.
```bash
$ docker image ls my-web-server
```

---

## 3. 매니페스트 파일 작성

`01_pods/web-server-pod.yaml` 파일을 작성하여 방금 만든 이미지를 사용하는 파드를 정의합니다.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: web-server-pod
spec:
  containers:
    - name: web-server-container
      image: my-web-server
      imagePullPolicy: IfNotPresent # 로컬 이미지를 우선 사용
      ports:
        - containerPort: 80
```

---

## 4. 파드 생성 및 확인

매니페스트 파일을 기반으로 파드를 생성합니다.

```bash
$ kubectl apply -f 01_pods/web-server-pod.yaml 
```

파드가 정상적으로 생성되고 `Running` 상태인지 확인합니다.

```bash
$ kubectl get pods
```

---

## 5. 접속 확인 (Port Forwarding)

로컬 PC에서 파드에 접속하기 위해 포트 포워딩을 수행합니다.

```bash
# 로컬의 5000번 포트를 파드의 80번 포트로 연결
$ kubectl port-forward web-server-pod 5000:80
```

브라우저에서 `http://localhost:5000`에 접속하여 파란색 글씨의 "My Web Page"가 나오는지 확인합니다.

---

## 6. 리소스 정리

실습이 끝난 후 생성한 파드를 삭제합니다.

```bash
$ kubectl delete pod web-server-pod
```
