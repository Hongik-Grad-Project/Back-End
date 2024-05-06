# open jdk 17 버전의 환경을 구성
FROM openjdk:17

# /app 디렉토리를 작업 디렉토리로 설정 (Docker 컨테이너 내에서 애플리케이션을 실행하는 데 사용)
WORKDIR /app

# 호스트 시스템의 build/libs/demo-0.0.1-SNAPSHOT.jar 파일을 현재 작업 디렉토리 (/app)로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar

# Docker 컨테이너가 포트 8080에서 애플리케이션을 실행하는 데 사용
EXPOSE 8080

# 컨테이너가 시작될 때 실행될 기본 명령을 정의
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]


## build가 되는 시점에 JAR_FILE이라는 변수 명에 build/libs/*.jar 선언
## build/libs - gradle로 빌드했을 때 jar 파일이 생성되는 경로
#ARG JAR_FILE=/build/libs/*.jar
#
## JAR_FILE을 app.jar로 복사
#COPY ${JAR_FILE} app.jar
#
## Docker 컨테이너에서 실행될 Java 애플리케이션의 진입점 정의
#ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/app.jar"]