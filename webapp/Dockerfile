FROM java:8-jre
MAINTAINER Douglas Chimento "dchimento@gmail.com"
ADD build/libs/society-leagues-server.jar /srv/service.jar
EXPOSE 8080
ENTRYPOINT java -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -verbose:gc -Xloggc:/tmp/logs/jvdebug.log -Xmx2g  -jar /srv/service.jar  $SERVER_ARGS
