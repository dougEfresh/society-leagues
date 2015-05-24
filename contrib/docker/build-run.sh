set -x 
docker stop society-leagues-server 
docker rm society-leagues-server 
[ -d /tmp/logs ] || mkdir /tmp/logs
ENV_FILE=""

if [ -n "$1" ] ; then ENV_FILE="--env-file=$1" ; fi

git pull && ./gradlew -x test clean build && \
docker build -t dougefresh/society-leagues-server . && \
docker  run $ENV_FILE --name=society-leagues-server -v /tmp/logs:/tmp/logs  -d  -p 8080:8080  -u www-data dougefresh/society-leagues-server
