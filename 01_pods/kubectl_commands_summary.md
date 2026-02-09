# 지금까지 배운 주요 명령어 정리

쿠버네티스 파드(Pod)를 다루면서 사용한 주요 명령어들을 정리합니다.

---

### ✅ 파드 조회
현재 클러스터에 생성된 파드의 목록과 상태를 확인합니다.
```bash
$ kubectl get pods
```

### ✅ 파드 포트 포워딩
로컬 PC의 특정 포트를 실행 중인 파드의 포트로 연결하여 접속 가능하게 합니다.
```bash
# kubectl port-forward pod/[파드명] [로컬포트]:[파드포트]
$ kubectl port-forward pod/nginx-pod 80:80
```

### ✅ 파드 삭제
생성된 파드를 삭제합니다.
```bash
# kubectl delete pod [파드명]
$ kubectl delete pod nginx-pod
```

### ✅ 파드 디버깅
파드에 문제가 발생했거나 내부 상태를 확인하고 싶을 때 사용합니다.

1. **파드 세부 정보 조회하기 (Events 확인)**
    ```bash
    # kubectl describe pods [파드명]
    $ kubectl describe pods nginx-pod
    ```
    
2. **파드 로그 확인하기**
    ```bash
    # kubectl logs [파드명]
    $ kubectl logs nginx-pod
    ```
    
3. **파드 내부로 접속하기**
    ```bash
    # kubectl exec -it [파드명] -- bash
    $ kubectl exec -it nginx-pod -- bash
    
    # 만약 bash가 없다면 sh로 접속
    $ kubectl exec -it nginx-pod -- sh
    ```

### ✅ 리소스 생성 및 변경사항 적용
매니페스트 파일(YAML)에 정의된 리소스를 생성하거나 변경된 설정을 적용합니다.
```bash
# kubectl apply -f [파일명]
$ kubectl apply -f nginx-pod.yaml
```
