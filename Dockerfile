FROM dougefresh/society-base

MAINTAINER Douglas Chimento "dchimento@gmail.com"

ADD build/distributions/leagues.tar.gz /data/web/leagues/
ADD build/libs/society-leagues.jar /srv/service/

EXPOSE 80

ENTRYPOINT bash /srv/start.sh
