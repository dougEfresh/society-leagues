FROM dougefresh/society-base

MAINTAINER Douglas Chimento "dchimento@gmail.com"

ADD build/libs/society-leagues-server.jar /srv/service.jar
ADD society-leagues-web/ /srv/webapp/
ADD contrib/docker/ngnix.conf etc/nginx/sites-enabled/default

EXPOSE 8000

ENTRYPOINT java -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -verbose:gc -Xloggc:/tmp/jvmdebug.log -Xmx512m -Dembedded=true  -jar /srv/service.jar 
