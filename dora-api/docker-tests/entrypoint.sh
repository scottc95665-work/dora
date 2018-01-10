#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

if ([ -z "$DORA_URL" ]); then
  echo "DORA_URL variable is required"
  exit 1
fi

if ([ -z "$TEST_TYPE" ]); then
  echo "TEST_TYPE variable is required"
  exit 1
fi

if [ "$TEST_TYPE" = "smoke" ]; then
  echo "Executing the Smoke Test..."
  TEST_CLASS=gov.ca.cwds.rest.resources.DoraSmokeTest
else
  echo "Unexpected TEST_TYPE: '$TEST_TYPE'"
  exit 1
fi

java ${JAVA_OPT} -Ddora.url="${DORA_URL}" -cp /opt/dora-tests/resources:dora-tests.jar org.junit.runner.JUnitCore ${TEST_CLASS}
