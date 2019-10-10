# Dockerize spring boot app

---

## dockerfile-maven-plugin config

```
<plugin>
  <groupId>com.spotify</groupId>
  <artifactId>dockerfile-maven-plugin</artifactId>
  <version>${dockerfile-maven.version}</version>
  <executions>
    <execution>
      <goals>
        <goal>build</goal>
        <goal>push</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <repository>irianro/${project.artifactId}</repository>
    <googleContainerRegistryEnabled>false</googleContainerRegistryEnabled>
    <tag>${project.version}</tag>
    <buildArgs>
      <JAR_FILE>resource-blog/target/${project.build.finalName}.jar</JAR_FILE>
    </buildArgs>
  </configuration>
</plugin>
```

---

## Dockerfile

```
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

<p>
This Dockerfile is very simple, but that’s all you need to run a Spring Boot app with no frills: just Java and a JAR file. The project JAR file is ADDed to the container as "app.jar" and then executed in the ENTRYPOINT.
</p>

<p>
To reduce Tomcat startup time we added a system property pointing to "/dev/urandom" as a source of entropy. This is not necessary with more recent versions of Spring Boot, if you use the "standard" version of Tomcat (or any other web server).</p>

---

## Dockerfile location


<pre>
└──mono-repo
    │── resource-blog
    │	└── pom.xml
    │── resource-shop
    │	└── pom.xml
    └── Dockerfile
</pre>

<small>If the Dockerfile is not located in the same directory as the pom.xml a <strong>contextDirectory</strong> config is needed.
The JAR_FILE arg should include the application folder("resource-blog")</small>

```
 <configuration>
    <repository>irianro/${project.artifactId}</repository>
    <googleContainerRegistryEnabled>false</googleContainerRegistryEnabled>
    <tag>latest</tag>
    <contextDirectory>..</contextDirectory>
    <buildArgs>
        <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
    </buildArgs>
</configuration>
```               
---

## Usage

```
mvn package
mvn dockerfile:build
mvn verify
mvn dockerfile:push
mvn deploy
```
---

## Resources

- https://github.com/spotify/dockerfile-maven
- https://github.com/spotify/dockerfile-maven/blob/master/docs/authentication.md
- https://spring.io/guides/gs/spring-boot-docker/