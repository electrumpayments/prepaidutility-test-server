#!/bin/bash

testdir=$1
echo "Running newman tests:"

newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPUlocalhosttest.postman_environment.json -d ${testdir}/test_meterIds-purchases.csv -n 10 --folder Lookup-purchase-confirm
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPUlocalhosttest.postman_environment.json -d ${testdir}/test_meterIds-purchases.csv -n 1 --folder Lookup-purchase-reverse
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPUlocalhosttest.postman_environment.json -d ${testdir}/test_meterIds-retries.csv -n 1 --folder Lookup-timeout-retry-confirm
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPUlocalhosttest.postman_environment.json -d ${testdir}/test_meterIds-keychange.csv -n 1 --folder "Key change"
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPUlocalhosttest.postman_environment.json -d ${testdir}/test_meterIds-keychange.csv -n 1 --folder "Fault reports"

if [ "${?}" != 0 ]; then
	echo "Tests failed"
	exit 1
else
	echo "Tests passed"
	exit 0
fi 

