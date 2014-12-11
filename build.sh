[ -d build ] || mkdir build 
branch=`git rev-parse --abbrev-ref HEAD`
git archive --format=tar.gz -o build/league.tar.gz $branch
