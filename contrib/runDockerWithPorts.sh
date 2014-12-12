# run docker build 1st 
docker run -d  -p 8888:80 -p 8080:8080 -p 3306:3306 ${1?}
