#!/bin/bash

echo "Stopping Kafka..."
kill $(cat /tmp/kafka.pid) 2>/dev/null && echo "Kafka stopped." || echo "Kafka not running?"

echo "Stopping ZooKeeper..."
kill $(cat /tmp/zookeeper.pid) 2>/dev/null && echo "ZooKeeper stopped." || echo "ZooKeeper not running?"

rm -f /tmp/kafka.pid /tmp/zookeeper.pid
