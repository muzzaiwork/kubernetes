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

> **💡 화면 가이드**: AWS 콘솔에서 '데이터베이스 생성' 클릭 후, [표준 생성] -> [MySQL] -> [템플릿: 프리 티어] 순서로 선택하세요. 하단의 [설정] 섹션에서 위 정보를 입력합니다.

4. **연결**:
   - 퍼블릭 액세스: `예` (로컬 테스트를 위해 잠시 허용하거나, 보안 그룹으로 제어)
   - VPC 보안 그룹: `새로 생성` 또는 `기존 선택`

### ✅ 보안 그룹 설정 (매우 중요)
EC2 인스턴스(k3s)에서 RDS에 접속하기 위해서는 RDS의 보안 그룹에서 인바운드 규칙을 추가해야 합니다.

```text
[보안 그룹 인바운드 규칙 설정 예시]
- 유형: MYSQL/Aurora
- 프로토콜: TCP
- 포트 범위: 3306
- 소스: 사용자 지정 (EC2 인스턴스의 보안 그룹 ID를 입력하세요)
```

> **💡 화면 가이드**: RDS 상세 페이지의 [연결 및 보안] 탭에서 'VPC 보안 그룹' 링크를 클릭합니다. 보안 그룹 상세 페이지 하단의 [인바운드 규칙 편집] 버튼을 눌러 위 규칙을 추가하세요.

- **유형**: `MYSQL/Aurora`
- **프로토콜**: `TCP`
- **포트 범위**: `3306`
- **소스**: `EC2 인스턴스의 보안 그룹 ID` 또는 `VPC 대역 IP`

---

## 2. AWS ECR(이미지 저장소) 설정

### ✅ 리포지토리 생성
1. AWS ECR 콘솔에서 `리포지토리 생성` 클릭
2. 리포지토리 이름: `spring-server`

> **💡 화면 가이드**: ECR 서비스 메인화면에서 주황색 [리포지토리 생성] 버튼을 클릭하고, [가시성 설정: Private] 확인 후 리포지토리 이름을 입력하고 하단의 [리포지토리 생성]을 누릅니다.

3. 생성 완료 후 `푸시 명령 보기`를 통해 로그인 및 푸시 명령어를 확인할 수 있습니다.

### ✅ 로컬에서 ECR로 이미지 푸시
> **💡 화면 가이드**: ECR 리포지토리 목록에서 생성한 `spring-server`를 선택하면 우측 상단에 [푸시 명령 보기] 버튼이 있습니다. 이를 클릭하면 각 운영체제(macOS/Linux/Windows)별 명령어가 팝업으로 나타납니다.

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
