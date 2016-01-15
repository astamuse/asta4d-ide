#!/bin/bash

sitefolder=$1
currentFolder=`pwd`

if [ "$sitefolder"X = "X" ]
then
    echo $0 '<site folder>'
    exit 1
fi

mvn clean package

if [ $? -ne 0 ]
then
    echo
    echo
    echo mvn failed.
    exit 1
fi

cd $sitefolder
rm -rf updatesite
cp -R $currentFolder/com.astamuse.asta4d.ide.eclipse.updatesite/target/repository .
mv repository updatesite

echo 
echo 
echo copied files to $sitefolder/updatesite

echo
echo
echo release finished.




