FROM openjdk:17-jdk

# JAR 파일 경로 설정 (gradle 빌드 시 생성되는 경로)
COPY build/libs/*SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
