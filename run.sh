./build.sh
dockerRev=`docker build -q   . | grep Successfully | awk '{print $3}'`
docker run -d  -p 127.0.0.1:8888:80 $dockerRev 

