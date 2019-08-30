#!/bin/bash

if ([ -z "$JAVA_OPT" ]); then
  JAVA_OPT="-Xms512m -Xmx512m"
fi

if ([ -z "$XPACK_ENABLED" ] || $XPACK_ENABLED); then
  echo "X-PACK enabled"
  DORA_CONFIG="config/dora.yml"
else
  echo "X-PACK disabled"
  DORA_CONFIG="config/dora_nosec.yml"
fi

if [ -x /paramfolder/parameters.sh ]; then
    source /paramfolder/parameters.sh
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar ${JAVA_OPT} -jar dora.jar server ${DORA_CONFIG}
else
    java ${JAVA_OPT} -jar dora.jar server ${DORA_CONFIG}
fi
