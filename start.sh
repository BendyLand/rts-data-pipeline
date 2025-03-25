#!/bin/bash

echo "Starting ZooKeeper..."
/opt/homebrew/bin/zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties > /tmp/zookeeper.log 2>&1 &
ZK_PID=$!
sleep 2

if ps -p $ZK_PID > /dev/null; then
  echo $ZK_PID > /tmp/zookeeper.pid
  echo "ZooKeeper started with PID $ZK_PID"
else
  echo "ZooKeeper failed to start. Check /tmp/zookeeper.log"
  exit 1
fi

echo "Starting Kafka..."
rm -rf /tmp/kafka-logs  # optionally clear stale state
/opt/homebrew/bin/kafka-server-start /opt/homebrew/etc/kafka/server.properties > /tmp/kafka.log 2>&1 &
KAFKA_PID=$!

# Wait and dump logs if it fails
echo "Waiting for Kafka to start..."
TIMEOUT=10
STARTED=0
for ((i=0; i<$TIMEOUT; i++)); do
  echo -n "."
  if grep -q "KafkaServer.*started" /tmp/kafka.log; then
    STARTED=1
    echo
    break
  fi
  sleep 1
done

if [ "$STARTED" -eq 1 ]; then
  echo $KAFKA_PID > /tmp/kafka.pid
  echo "Kafka started with PID $KAFKA_PID"
else
  echo
  echo "Kafka failed to start. Here's the last few log lines:"
  tail -n 30 /tmp/kafka.log
  kill $KAFKA_PID 2>/dev/null
  exit 1
fi

