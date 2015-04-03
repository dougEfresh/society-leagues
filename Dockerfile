FROM java:openjdk-8u40-jre

MAINTAINER Douglas Chimento "dchimento@gmail.com"

ADD build/libs/society-leagues-server.jar /srv/service.jar

EXPOSE 80

ENTRYPOINT java -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -verbose:gc -Xloggc:/tmp/jvmdebug.log -Xmx512m -jar /srv/service.jar 
