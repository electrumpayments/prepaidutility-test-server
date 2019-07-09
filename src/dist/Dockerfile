# the base image is a trusted ubuntu build with java 8 (https://index.docker.io/u/dockerfile/java/)
FROM openjdk:8u212-jdk

# that's me!
LABEL maintainer="Electrum Payments, hello@electrum.co.za"

# we need this because the workdir is modified in dockerfile/java -> now java:8
WORKDIR /

# run the (java) server as the daemon user
USER daemon

# copy the locally built fat-jar to the image
ADD ${project.artifactId}-${project.version}-jar-with-dependencies.jar ${project.artifactId}-${project.version}-jar-with-dependencies.jar

# the server binds to 8080 - expose that port
EXPOSE 8080

# run the server when a container based on this image is being run
CMD ["java", "-jar", "${project.artifactId}-${project.version}-jar-with-dependencies.jar", "8080"]
