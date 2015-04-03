git pull && ./gradlew clean build && \
(cd society-leagues-web && git pull && npm install && ./scripts/ugly.sh && rm -rf node_modules) && \
docker build -t dougefresh/society-leagues-server .
docker push dougefresh/society-leagues-server:latest
