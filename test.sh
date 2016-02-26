 #!/bin/bash

for i in  src/test/java/com/society/leagues/test/* ; do test=`echo $i | tr '/' '.' | sed 's|src\.test\.java\.|| '` ;   ../gradlew test --tests ${test%.java}  ; done
