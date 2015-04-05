git diff-index --quiet HEAD && git pull && ./gradlew clean build && \
(git submodule  update --remote --force && cd society-leagues-web && npm install && ./scripts/ugly.sh && rm -rf node_modules) && \
docker build -t dougefresh/society-leagues-server .
docker push dougefresh/society-leagues-server:latest
