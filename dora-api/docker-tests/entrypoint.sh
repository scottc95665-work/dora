#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

if ([ -z "$DORA_URL" ]); then
  echo "DORA_URL variable is required"
  exit 1
fi

if ([ -z "$TEST_TYPE" ]); then
  echo "TEST_TYPE variable is required Possible values: smoke"
  exit 1
fi

if ([ -z "$AUTH_MODE" ]); then
  echo "AUTH_MODE variable is required. Possible values: dev, integration"
  exit 1
fi

if [ "$AUTH_MODE" = "dev" ]; then
  if ([ -z "$PERRY_URL" ]); then
    echo "PERRY_URL variable is required in dev auth. mode."
    exit 1
  fi
fi

if [ "$TEST_TYPE" = "smoke" ]; then
  echo "Executing the Smoke Test..."
  TEST_CLASS=gov.ca.cwds.rest.resources.DoraSmokeTest
else
  echo "Unexpected TEST_TYPE: '$TEST_TYPE'"
  exit 1
fi

echo "Running Dora Tests of type '$TEST_TYPE' against ${DORA_URL}"

java ${JAVA_OPT} -Ddora.url="${DORA_URL}" -Dauth.mode=${AUTH_MODE} -Dperry.url=$PERRY_URL -cp /opt/dora-tests/resources:dora-tests.jar org.junit.runner.JUnitCore ${TEST_CLASS}
