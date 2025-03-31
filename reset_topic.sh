#!/bin/bash

TOPIC="sensor-data"
BROKER="localhost:9092"

/opt/homebrew/bin/kafka-topics --bootstrap-server $BROKER --delete --topic $TOPIC

# Wait for deletion to complete
echo "Waiting for topic '$TOPIC' to be fully deleted..."
while /opt/homebrew/bin/kafka-topics --bootstrap-server $BROKER --list | grep -q "^$TOPIC$"; do
  echo -n "."
  sleep 0.5
done

echo "Recreating topic '$TOPIC'..."
/opt/homebrew/bin/kafka-topics --bootstrap-server $BROKER \
  --create --topic $TOPIC --partitions 1 --replication-factor 1

echo "Removing Spark checkpoint..."
rm -rf /tmp/sensor-type-check

echo "Clearing old consumer logs..."
echo "" > /tmp/kafka-consumer.log

