#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

if ([ -z "$CANS_API_URL" ]); then
  echo "CANS_API_URL variable is required"
  exit 1
fi

if ([ -z "$TEST_TYPE" ]); then
  TEST_TYPE="smoke"
  echo "Default value is set: TEST_TYPE = smoke"
fi

if [[ "$TEST_TYPE" == "smoke" ]]; then
  echo "Executing the Smoke Test..."
  TEST_CLASS=gov.ca.cwds.cans.SmokeTestSuite
elif [[ "$TEST_TYPE" == "functional" ]]; then
  if ([ -z "$PERRY_URL" ]); then
    echo "PERRY_URL variable is required"
    exit 1
  fi
  echo "Executing the functional Test..."
  TEST_CLASS=gov.ca.cwds.cans.FunctionalTestSuite
else
  echo "Unknown TEST_TYPE: '$TEST_TYPE'"
  echo "Known types: smoke, functional"
  exit 1
fi

echo "Starting tests: "
echo "CANS_API_URL = '$CANS_API_URL'"
echo "PERRY_URL = '$PERRY_URL'"
echo "TEST_TYPE = '$TEST_TYPE'"
echo "TEST_CLASS = '$TEST_CLASS'"

java ${JAVA_OPT} -Dapi.url="${CANS_API_URL}" -Dperry.url="${PERRY_URL}" -cp /opt/cans-api-tests/resources:cans-api-tests.jar org.junit.runner.JUnitCore ${TEST_CLASS}
