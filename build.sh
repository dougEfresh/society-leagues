[ -d build ] || mkdir build 
git archive --format=tar.gz -o build/league.tar.gz ${1:?}
