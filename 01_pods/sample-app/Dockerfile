# 1단계: 빌드용 이미지 (Java 17 사용)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# (실습용이므로 간소화하여 소스 코드를 복사하고 직접 빌드하는 대신 
# 이미 빌드된 JAR가 있다고 가정하거나 이미지를 빌드하는 가이드로 대체합니다.)
# 실제로는 ./gradlew build 등이 필요합니다.

# 2단계: 실행용 이미지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드된 jar 파일을 복사 (파일명은 설정에 따라 다를 수 있음)
# COPY build/libs/*.jar app.jar
# 테스트용 샘플 이미지를 사용할 것이므로 실제 복사 과정은 주석 처리합니다.

CMD ["java", "-jar", "app.jar"]
