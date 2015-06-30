FROM java:8-jre
MAINTAINER Douglas Chimento "dchimento@gmail.com"
ADD build/libs/society-leagues-billing.jar /srv/service.jar
EXPOSE 8080
ENTRYPOINT java -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -verbose:gc -Xloggc:/tmp/logs/jvdebug.log -Xmx128m  -jar /srv/service.jar  $SERVER_ARGS
