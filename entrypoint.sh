#!/bin/bash

#The file should have UNIX-style EOL

JAVA_OPTS="-Xms128m -Xmx256m ${JAVA_OPTS}"

if [ -z "$CANS_API_CONFIG" ]
then
  CANS_API_CONFIG="cans-api.yml"
fi

echo "config file: $CANS_API_CONFIG"

if [ -x /paramfolder/parameters.sh ]; then
    source /paramfolder/parameters.sh
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar  ${JAVA_OPTS} -jar cans-api.jar server $CANS_API_CONFIG
else
    java  ${JAVA_OPTS} -jar cans-api.jar server $CANS_API_CONFIG
fi
