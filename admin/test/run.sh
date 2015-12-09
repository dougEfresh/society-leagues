#docker run -i -t --entrypoint=bash rdpanek/casperjs:latest

casperjs test  --web-security=no    --server=http://localhost:8082 *Test.js
