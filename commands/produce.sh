#!/bin/sh

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <N>"
  exit 1
fi

N=$1

for (( i=1; i<=N; i++ ))
do
  echo "key$i,Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." | $KAFKA_INSTALL_DIR/bin/kafka-console-producer.sh --topic kafka-test-topic --property key.separator=, --property parse.key=true --bootstrap-server localhost:9092
  echo "Produced message $i"
done

