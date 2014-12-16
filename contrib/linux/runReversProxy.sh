# $1 is the ip of your real host
# Make sure the webapp is listening on 8080 on your machine
docker run -d -p 80:80 --add-host webapp:${1:?}  dougefresh/society-nginx
