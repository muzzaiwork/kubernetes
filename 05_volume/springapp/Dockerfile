FROM openjdk:17-jdk-slim

# Gradle 빌드를 통해 생성된 JAR 파일을 컨테이너 내부의 app.jar로 복사합니다.
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
