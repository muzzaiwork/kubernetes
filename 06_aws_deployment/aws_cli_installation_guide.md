# [부록] 로컬 환경 AWS CLI 설치 및 설정 가이드

AWS 리소스를 터미널에서 제어하기 위한 필수 도구인 **AWS CLI (Command Line Interface)**를 설치하고 초기 설정(자격 증명)을 진행하는 가이드입니다.

---

## 1. AWS CLI 설치하기

운영체제별로 공식 설치 파일을 다운로드하여 설치할 수 있습니다.

### 🍎 macOS (macOS 10.15 이상)
1. **패키지 다운로드**: [AWSCLIV2.pkg](https://awscli.amazonaws.com/AWSCLIV2.pkg)
2. **설치 실행**: 다운로드한 `.pkg` 파일을 실행하여 지시사항에 따라 설치를 완료합니다.
3. **터미널에서 설치 확인**:
   ```bash
   $ aws --version
   # aws-cli/2.x.x Python/3.x.x ...
   ```

### 🪟 Windows
1. **MSI 설치 프로그램**: [AWSCLIV2.msi](https://awscli.amazonaws.com/AWSCLIV2.msi) 다운로드
2. **설치 실행**: 파일을 실행하여 설치 마법사를 따라 진행합니다.
3. **명령 프롬프트(CMD)에서 확인**: `aws --version`

### 🐧 Linux (x86_64)
```bash
$ curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
$ unzip awscliv2.zip
$ sudo ./aws/install
```

---

## 2. AWS 자격 증명(Configure) 설정

설치 완료 후, 내 AWS 계정의 리소스에 접근할 수 있도록 자격 증명을 등록해야 합니다.

### 🛠️ 등록 절차
1. **자격 증명 명령 실행**:
   ```bash
   $ aws configure
   ```
2. **정보 입력**:
   - **AWS Access Key ID**: IAM 사용자 생성 시 발급받은 액세스 키
   - **AWS Secret Access Key**: 발급받은 비밀 액세스 키
   - **Default region name**: `ap-northeast-2` (서울 리전 권장)
   - **Default output format**: `json`

### ✅ 설정 확인
등록된 설정이 정상적으로 동작하는지 확인하기 위해 현재 로그인된 정보를 조회해봅니다.
```bash
$ aws sts get-caller-identity
```

---

## 3. 주요 활용 팁

- **ECR 로그인**: `aws ecr get-login-password --region [REGION] | docker login --username AWS --password-stdin [AWS_ACCOUNT_ID].dkr.ecr.[REGION].amazonaws.com`
- **프로파일 관리**: 여러 계정을 사용할 경우 `--profile` 옵션을 활용하여 자격 증명을 구분할 수 있습니다. (`aws configure --profile [이름]`)
- **자동 완성**: 탭(Tab) 키를 이용한 자동 완성 기능을 활성화하면 더욱 편리하게 사용할 수 있습니다.

---

## 🔗 참고 자료
- [인파님의 AWS CLI 설치 & 사용법 가이드](https://inpa.tistory.com/entry/AWS-%F0%9F%93%9A-AWS-CLI-%EC%84%A4%EC%B9%98-%EC%82%AC%EC%9A%A9%EB%B2%95-%EC%89%BD%EA%B3%A0-%EB%B9%A0%EB%A5%B4%EA%B2%8C)
- [AWS CLI 공식 문서](https://docs.aws.amazon.com/ko_kr/cli/latest/userguide/getting-started-install.html)
