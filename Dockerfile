FROM dougefresh/society-base

MAINTAINER Douglas Chimento "dchimento@gmail.com"

ADD build/libs/society-leagues-server.jar /srv/service.jar
ADD society-leagues-web/webapp /srv/webapp/
ADD contrib/docker/nginx.conf /etc/nginx/nginx.conf
ADD contrib/docker/site.conf /etc/nginx/sites-enabled/default
ADD contrib/docker/start.sh /srv/start.sh
EXPOSE 8000

ENTRYPOINT /srv/start.sh
