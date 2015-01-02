set -x 
docker run -v $PWD/build/logs:/data/logs  -v $PWD/web:/data/web/leagues -d -p 80:80 ${1:?}
