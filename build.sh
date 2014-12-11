[ -d build ] || mkdir build 
branch=`git rev-parse --abbrev-ref HEAD`
if [ -n "$1" ] ; then 
git archive --format=tar.gz -o build/league.tar.gz $branch
else 
tar  --exclude-backups --exclude-vcs --exclude=build -zcf  build/league.tar.gz .
fi

docker build . 
