[ -z "$DB_HOST" ] && DB_HOST="localhost"
export DB_HOST

[ "$DB_HOST" == "localhost" ] && /etc/init.d/mysql start

nohup java $JAVA_OPTS -Xmx128m -jar /srv/service/*.jar --spring.datasource.url=jdbc:mysql://$DB_HOST/league 2> /data/logs/society-api-error.log &

/etc/init.d/php5-fpm start
/etc/init.d/nginx start

while true ; do
sleep 30;
pgrep java > /dev/null || nohup java $JAVA_OPTS -Xmx128m -jar /srv/service/*.jar --spring.datasource.url=jdbc:mysql://$DB_HOST/league &
done  
pkill java
exit 1

