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
  TEST_CLASS=gov.ca.cwds.cm.SmokeTestSuite
elif [[ "$TEST_TYPE" == "functional" ]]; then
  if ([ -z "$PERRY_URL" ]); then
    echo "PERRY_URL variable is required"
    exit 1
  fi
  if ([ -z "$DB_NS_JDBC_URL" ]); then
    echo "DB_NS_JDBC_URL variable is required"
    exit 1
  fi
  if ([ -z "$DB_NS_SCHEMA" ]); then
    echo "DB_NS_SCHEMA variable is required"
    exit 1
  fi
  if ([ -z "$DB_NS_USER" ]); then
    echo "DB_NS_USER variable is required"
    exit 1
  fi
  if ([ -z "$DB_NS_PASSWORD" ]); then
    echo "DB_NS_PASSWORD variable is required"
    exit 1
  fi
  echo "Executing the functional Test..."
  TEST_CLASS=gov.ca.cwds.cm.functionalTestSuite
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
echo "DB_NS_JDBC_URL = '$DB_NS_JDBC_URL'"
echo "DB_NS_SCHEMA = '$DB_NS_SCHEMA'"
echo "DB_NS_USER = '$DB_NS_USER'"
echo "DB_NS_PASSWORD = ********"

java ${JAVA_OPT} -Dapi.url="${CANS_API_URL}" -Dperry.url="${PERRY_URL}" -cp /opt/cans-api-tests/resources:cans-api-tests.jar org.junit.runner.JUnitCore ${TEST_CLASS}
