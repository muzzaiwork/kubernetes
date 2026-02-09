# [예제] 프론트엔드(Next.js) 서버를 파드(Pod)로 띄워보기

### ✅ 프론트엔드(Next.js) 서버를 파드(Pod)로 띄워보기

1. **Next.js 프로젝트 만들기**
    
    ```bash
    $ npx create-next-app
    ```
    

2. **프로젝트 실행시켜보기**
    
    ```bash
    $ npm run dev
    ```

3. **Dockerfile 작성하기**
    
    **Dockerfile**
    
    ```docker
    FROM node:20-alpine
    
    WORKDIR /app
    
    COPY . .
    
    RUN npm install
    
    RUN npm run build
    
    EXPOSE 3000
    
    ENTRYPOINT [ "npm", "run", "start" ]
    ```
    
4. **.dockerignore 작성하기**
    
    **.dockerignore**
    
    ```text
    node_modules
    ```
    
5. **Dockerfile을 바탕으로 이미지 빌드하기**
    
    ```bash
    $ docker build -t next-server .
    ```
    
6. **이미지가 잘 생성됐는지 확인하기**
    
    ```bash
    $ docker image ls
    ```
    
7. **매니페스트 파일 작성하기**
    
    **next-pod.yaml**
    
    ```yaml
    apiVersion: v1
    kind: Pod
    metadata:
      name: next-pod
    spec:
      containers:
        - name: next-container
          image: next-server
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 3000
    ```
    
8. **매니페스트 파일을 기반으로 파드(Pod) 생성하기**
    
    ```bash
    $ kubectl apply -f next-pod.yaml 
    ```
    
9. **파드(Pod)가 잘 생성됐는지 확인**
    
    ```bash
    $ kubectl get pods
    ```
    
10. **포트 포워딩으로 Next.js 서버가 실행됐는지 확인**
    
    ```bash
    $ kubectl port-forward next-pod 3000:3000
    ```
    
11. **파드 삭제하기**
    
    ```bash
    $ kubectl delete pod next-pod
    ```
