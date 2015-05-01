java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/society-leagues-server.jar --security-disable=true --server.port=8081 --embedded=false --generate=false
