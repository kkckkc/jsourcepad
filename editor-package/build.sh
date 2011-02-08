echo -n "Enter buildId (yyyyMMddxx) > "
read buildId

cd ..
mvn -DbuildId=$buildId -Dmaven.test.skip=true -Pwebview,linux install
#mvn -DbuildId=$buildId -Dmaven.test.skip=true -Pwebview,linux_64 install
#mvn -DbuildId=$buildId -Dmaven.test.skip=true -Pwebview,win32 install
cd -

OS=`uname`
if [ "{$OS}" == "Darwin" ]; then
    mvn -DbuildId=$buildId -Pwebview,osx install
fi
