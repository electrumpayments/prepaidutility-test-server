#!/bin/bash

testdir=$1
host=localhost
echo "Running newman tests:"

newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-purchases.csv -n 10 --folder Lookup-purchase-confirm
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-purchases.csv -n 1 --folder Lookup-purchase-reverse
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-retries.csv -n 1 --folder Lookup-timeout-retry-confirm
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-keychange.csv -n 1 --folder "Key change requests"
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-keychange.csv -n 1 --folder "Fault reports"
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -d ${testdir}/test_meterIds-keychange.csv -n 1 --folder "Meter lookups"
newman run ${testdir}/PrepaidUtilityTestPack.postman_collection.json -e ${testdir}/PPU${host}test.postman_environment.json -n 1 --folder "Unmatched advices"

if [ "${?}" != 0 ]; then
	echo "Tests failed"
	exit 1
else
	echo "Tests passed"
	exit 0
fi
