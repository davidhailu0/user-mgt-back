FROM maven:3.8.4-openjdk-17

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

COPY target/hajj-0.0.1-SNAPSHOT.jar /.

CMD ["java","-jar","/hajj-0.0.1-SNAPSHOT.jar"]