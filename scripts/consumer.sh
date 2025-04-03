#!/bin/bash

echo "Starting Kafka consumer for topic 'sensor-data'..."
/opt/homebrew/opt/kafka/bin/kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic sensor-data \
  --from-beginning > /tmp/kafka-consumer.log 2>&1 &
CONSUMER_PID=$!
echo $CONSUMER_PID > /tmp/kafka-consumer.pid
echo "Kafka consumer started with PID $CONSUMER_PID"
echo "Logs are being written to /tmp/kafka-consumer.log"


