#!/usr/bin/env bash

mvn clean package -DskipTests
if [[ $? -ne 0 ]]; then
    echo "build check fail, please fix!!!"
    exit 1
else
    echo "build check success"
fi

# 1, $releaseVersion $newVersion, git pull, git commit && git push
relVersion=$1
newVersion="$2-SNAPSHOT"
echo "**************************************************************************"
echo "*PEPPER-BOOT-RELEASE**  current release version is $relVersion"
echo "*PEPPER-BOOT-RELEASE**  new development version will be $newVersion"
echo "**************************************************************************"
read -p "Press any key to continue." var
# 2, mvn clean package -DskipTests
# 3, if 2->OK then 3.1 else 3.2
#   3.1, set ${revision} as $releaseVersion, git commit && git push
sed -i "s/<revision>.*<\/revision>/<revision>$relVersion<\/revision>/g" pom.xml
git add . && git commit -m "version $relVersion release prepare" && git push
if [[ $? -ne 0 ]]; then
    echo "git push fail, please fix!!!"
    exit 1
else
    echo "git push success"
fi
echo "**************************************************************************"
echo "*PEPPER-BOOT-RELEASE**  prepare phase done, will deploy && tag remote repo"
echo "**************************************************************************"
read -p "Press any key to continue." var
#   3.2, exit and echo error message
# 4, if 3->OK then 4.1 else 4.2
#   4.1, mvn -Pcoohua deploy scm:tag -Drevision={$releaseVersion} -DskipTests
mvn -Pcoohua deploy scm:tag -Drevision=${relVersion} -DskipTests
if [[ $? -ne 0 ]]; then
    echo "mvn deploy fail, please fix!!!"
    exit 1
else
    echo "mvn deploy success"
fi
echo "**************************************************************************"
echo "*PEPPER-BOOT-RELEASE**  release phase done, will update dev version"
echo "**************************************************************************"
read -p "Press any key to continue." var
#   4.2, exit and echo error message
# 5, if 4->OK then 5.1 else 5.2
#   5.1.1 set ${revision} as {$newVersion + SNAPSHOT}
sed -i "s/<revision>.*<\/revision>/<revision>$newVersion<\/revision>/g" pom.xml
git add . && git commit -m "change development version to $newVersion" && git push
#   5.1.2 git commit && git push
#   5.2, exit and echo error message
if [[ $? -ne 0 ]]; then
    echo "git push fail, please fix!!!"
    exit 1
else
    echo "git push success"
fi