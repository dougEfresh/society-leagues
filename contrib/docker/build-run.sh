docker stop society-leagues-server 
docker rm society-leagues-server 
[ -d /tmp/logs ] || mkdir /tmp/logs

git pull && ./gradlew -x test clean build && \
(git submodule  update --remote --force && cd society-leagues-web && npm install && ./scripts/ugly.sh ) && \
docker build -t dougefresh/society-leagues-server . && \
docker  run --name=society-leagues-server -v /tmp/logs:/tmp/logs  -d  -p 8000:8000  -u www-data dougefresh/society-leagues-server
