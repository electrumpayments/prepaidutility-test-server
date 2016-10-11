# the base image is a trusted ubuntu build with java 8 (https://index.docker.io/u/dockerfile/java/)
FROM java:7

# that's me!
MAINTAINER Neil neil@electrum.co.za

# we need this because the workdir is modified in dockerfile/java -> now java:8
WORKDIR /

# run the (java) server as the daemon user
USER daemon

# copy the locally built fat-jar to the image
ADD ${project.artifactId}-${project.version}-jar-with-dependencies.jar ${project.artifactId}-${project.version}-jar-with-dependencies.jar

# the server binds to 9999 - expose that port
EXPOSE 	9999

# run the server when a container based on this image is being run
CMD ["java", "-jar", "${project.artifactId}-${project.version}-jar-with-dependencies.jar", "9999" ]