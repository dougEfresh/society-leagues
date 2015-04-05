#!/bin/sh

cd /tmp/
[ -d logs ] || mkdir logs
nginx -c /etc/nginx/nginx.conf
java -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -verbose:gc -Xloggc:/tmp/logs/jvmdebug.log -Xmx512m -Dembedded=true  -jar /srv/service.jar 
