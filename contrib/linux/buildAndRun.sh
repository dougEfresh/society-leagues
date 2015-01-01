set -x 
./gradlew -x test clean build && dockerRev=`docker build . |  grep 'uccessfully built' | awk '{print $3}' `  && docker run -v $PWD/build/logs:/data/logs  -v $PWD/web:/data/web/leagues -d -p 80:80 $dockerRev 
