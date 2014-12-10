FROM dougefresh/society-base

MAINTAINER Douglas Chimento "dchimento@gmail.com"

ADD build/*.tar.gz /data/web/leagues/

ENTRYPOINT bash /srv/start.sh