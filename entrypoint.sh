#!/bin/bash

if [ -z "$XPACK_ENABLED" || "$XPACK_ENABLED" ]; then
  echo "X-PACK disabled"
  DORA_CONFIG="config/dora_nosec.yml"
else
  echo "X-PACK enabled"
  DORA_CONFIG="config/dora.yml"
fi

java -jar dora.jar server ${dora_config}