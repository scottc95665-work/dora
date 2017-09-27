#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

if ([ -z "$XPACK_ENABLED" ] || $XPACK_ENABLED); then
  echo "X-PACK enabled"
  DORA_CONFIG="config/dora.yml"
else
  echo "X-PACK disabled"
  DORA_CONFIG="config/dora_nosec.yml"
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar ${JAVA_OPT} -jar dora.jar server ${DORA_CONFIG}
else
    java ${JAVA_OPT} -jar dora.jar server ${DORA_CONFIG}
fi
