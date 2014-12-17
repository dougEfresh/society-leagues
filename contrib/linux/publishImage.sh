./gradlew clean build && rev=`git rev-list HEAD  | head -1 ` && dockerRev=`docker build . |  grep 'uccessfully built' | awk '{print $3}' ` && imageId=`docker run -d  $dockerRev ` && docker commit $imageId dougefresh/society-rest:$rev && docker push dougefresh/society-rest:$rev
 
