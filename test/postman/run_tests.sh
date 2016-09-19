#!/bin/bash
basedir=$1
testdir=$2
echo "Starting docker container"
docker build -t="ppu-test-server" ${basedir}/target
docker run -d -p 8080:8080 --name ppu-test-server_container ppu-test-server
/git/circlecitools/bin/waitForServer.sh localhost:8080 5000
${testdir}/run_newman.sh ${testdir}
rc=$?
echo "Cleaning up Docker"
docker stop ppu-test-server_container
docker rm ppu-test-server_container
docker rmi ppu-test-server
exit $rc
