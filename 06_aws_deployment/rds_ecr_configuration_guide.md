# AWS RDS 및 ECR 연동 가이드

이 문서는 AWS의 관리형 데이터베이스 서비스인 **RDS**와 컨테이너 이미지 저장소인 **ECR**을 활용하여 Spring Boot 애플리케이션을 배포하는 상세 설정을 설명합니다.

---

## 1. AWS RDS(MySQL) 설정

### ✅ RDS 인스턴스 생성
1. **엔진 옵션**: `MySQL` 선택
2. **템플릿**: `프리 티어` (학습용)
3. **설정**:
   - DB 인스턴스 식별자: `database-1`
   - 마스터 사용자 이름: `admin`
   - 마스터 암호: `password123` (실습용)

![RDS 생성 설정](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/6b0d9e8c-8b8a-4d4a-9b8c-8b8a4d4a9b8c/image.png)
*참고: RDS 생성 시 엔진 버전과 인스턴스 사양을 확인하세요.*

4. **연결**:
   - 퍼블릭 액세스: `예` (로컬 테스트를 위해 잠시 허용하거나, 보안 그룹으로 제어)
   - VPC 보안 그룹: `새로 생성` 또는 `기존 선택`

### ✅ 보안 그룹 설정 (매우 중요)
EC2 인스턴스(k3s)에서 RDS에 접속하기 위해서는 RDS의 보안 그룹에서 인바운드 규칙을 추가해야 합니다.

![RDS 보안 그룹 설정](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/7c1e8f9d-9c9a-4e5b-ac9d-9c9a4e5b8c8d/image.png)
*참고: 인바운드 규칙에 3306 포트를 열어주어야 합니다.*

- **유형**: `MYSQL/Aurora`
- **프로토콜**: `TCP`
- **포트 범위**: `3306`
- **소스**: `EC2 인스턴스의 보안 그룹 ID` 또는 `VPC 대역 IP`

---

## 2. AWS ECR(이미지 저장소) 설정

### ✅ 리포지토리 생성
1. AWS ECR 콘솔에서 `리포지토리 생성` 클릭
2. 리포지토리 이름: `spring-server`

![ECR 리포지토리 생성](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/8d2f9e0a-0a0b-4c0c-8d2f-9e0a0b4c0c0c/image.png)

3. 생성 완료 후 `푸시 명령 보기`를 통해 로그인 및 푸시 명령어를 확인할 수 있습니다.

### ✅ 로컬에서 ECR로 이미지 푸시
![ECR 푸시 명령](https://prod-files-secure.s3.us-west-2.amazonaws.com/e35a8144-c5ff-40f0-b123-384a331e35bb/9e3f0a1b-1b1c-4d1d-9e3f-0a1b1c1d1d1d/image.png)
*참고: AWS 콘솔의 '푸시 명령 보기' 버튼을 누르면 위와 같은 안내가 나옵니다.*

```bash
# 1. AWS CLI 로그인을 통한 인증 토큰 가져오기
$ aws ecr get-login-password --region [REGION] | docker login --username AWS --password-stdin [AWS_ACCOUNT_ID].dkr.ecr.[REGION].amazonaws.com

# 2. 이미지 빌드 (이미 있다면 생략)
$ docker build -t spring-server .

# 3. 이미지 태그 설정 (ECR 주소에 맞게 태깅)
$ docker tag spring-server:latest [AWS_ACCOUNT_ID].dkr.ecr.[REGION].amazonaws.com/spring-server:latest

# 4. 이미지 푸시
$ docker push [AWS_ACCOUNT_ID].dkr.ecr.[REGION].amazonaws.com/spring-server:latest
```

---

## 3. 쿠버네티스(k3s) 배포 설정

### ✅ 환경 변수 주입 (Deployment)
애플리케이션이 RDS에 접속할 수 있도록 `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` 등을 환경 변수로 설정합니다.

```yaml
env:
  - name: DB_HOST
    value: "database-1.xxxxxx.ap-northeast-2.rds.amazonaws.com" # RDS 엔드포인트
  - name: DB_PORT
    value: "3306"
  - name: DB_NAME
    value: "kub_practice"
  - name: DB_USERNAME
    value: "admin"
  - name: DB_PASSWORD
    value: "password123"
```

### ✅ 이미지 경로 수정
`image` 항목을 방금 푸시한 ECR 이미지 경로로 수정합니다.
```yaml
image: [AWS_ACCOUNT_ID].dkr.ecr.[REGION].amazonaws.com/spring-server:latest
imagePullPolicy: Always
```

---

## 4. 요약 및 주의사항
- **보안**: RDS 암호나 ECR 주소와 같은 민감한 정보는 실제 운영 환경에서는 **Secret** 오브젝트를 사용하여 관리하는 것이 좋습니다.
- **비용**: RDS와 ECR은 사용량에 따라 비용이 발생할 수 있으므로, 실습 후에는 리소스를 삭제하거나 중지하는 것을 권장합니다.
- **네트워크**: EC2와 RDS가 같은 VPC 내에 있는 경우 내부 통신이 가능하도록 라우팅 및 보안 그룹이 적절히 설정되어야 합니다.
