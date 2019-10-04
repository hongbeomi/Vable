#! /bin/sh

echo "============= ./gradlew cDAT 시작 Unit Test =============";
./gradlew cDAT
if [ "$?" -eq "0" ]; then
	echo "============= ./gradlew build 시작 =============";
	./gradlew build;
	if [ "$?" -eq "0" ]; then
    	exit 0;
	else
    	exit 1;
    	echo "=============  ./gradlew build 실패 =============";
	fi
else
	echo "=============  ./gradlew cDAT 실패 =============";
	exit 1;
fi
