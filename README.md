# prepaidutility-test-server [![CircleCI](https://circleci.com/gh/electrumpayments/prepaidutility-test-server/tree/master.svg?style=shield)](https://circleci.com/gh/electrumpayments/prepaidutility-test-server/tree/master)
REST server for testing implementations of the [prepaidutility-service-interface](https://github.com/electrumpayments/prepaidutility-service-interface).

## Testing a Client
To test a client implementation an instance of this test server is provided at https://ppu-test-server.herokuapp.com.

Or you can run a containerized version of this test server locally using Docker:
```bash
docker pull electrum/prepaidutility-test-server:3
# Run the test server listening on localhost:8080
docker run -d -p 8080:8080 electrum/prepaidutility-test-server:3
```

Messages sent to this server via the URLs described in the [prepaidutility-service-interface](https://github.com/electrumpayments/prepaidutility-service-interface) will be
validated and responded to with mock data.

### Testing message correctness
Messages will be validated for correctness against the service interface and in the event that a field is missing something similar to the following can be expected:

```json
{
  "errorType": "FORMAT_ERROR",
  "errorMessage": "Bad formatting",
  "detailMessage": {
    "formatErrors": [
      {
        "field": "product",
        "msg": "may not be null",
        "value": "null"
      }
    ]
  }
}
```

An errorType of `FORMAT_ERROR` is returned followed by an explanation of the format errors as follows:

* The `field`  attribute containing the field that has been formatted incorrectly
* The `msg` field contains information on what violation has occurred
* The `value` field contains the incorrectly formatted value that was used

## Testing a Server
Testing a server implementation can be done using [this](https://github.com/electrumpayments/prepaidutility-test-server/tree/master/test/postman) Postman (Collection v2) REST test pack.
These tests consist of correctly formatted JSON messages that validate server responses. Tests may also consist of a message flow in which multiple related messages are sent sequentially to the server to test handling of stateful interactions (such as requests and confirmations).

The test pack is comprised of JSON files: `PrepaidUtilityTestPack.postman_collection.json`, `PPUherokutest.postman_environment.json` and `PPUlocalhosttest.postman_environment.json`.
The postman_collection files are a collection of JSON tests that will be run. They contain JSON request messages and response validation scripts. These tests are dependent on variables contained in the the associated postman_environment files.

### Running tests
There are two possible ways to run this test pack: either via the Postman desktop client or via `newman`, the command line interface for Postman.

#### Postman desktop client
1. Download Postman at: https://www.getpostman.com/apps
2. Import the test collection and environments via the Import option
3. Open the Collection Runner and select the Runs tab
4. Select a sub-collection and hit run

NOTE: If you want to run the sub-collection named 'Lookup-purchase-confirm' you will need to import the data file: `test_meterIds-purchases.csv`

Tests may be run individually from the main Postman view where test conditions and structures may be modified.

#### Newman
1. Install newman (make sure `npm` is installed first):
```
npm install newman -g
```
2. Run the tests (from the root directory of this repo):
```
newman run test/postman/PrepaidUtilityTestPack.postman_collection.json -e test/postman/PPUlocalhosttest.postman_environment.json --folder <folder_name>
```

If you would like to run the 'Lookup-purchase-confirm' folder of tests you will need to append `-d test/postman/test_meterIds-purchases.csv -n 10` to the above command.

This will run tests against a Prepaid Utility Service server implementation hosted on localhost:8080 and provide a basic breakdown of which tests passed and failed.
