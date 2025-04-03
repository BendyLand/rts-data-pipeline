#!/bin/bash

# Function to stop a process by PID and wait
stop_process() {
  local pid_file=$1
  local name=$2

  if [ -f "$pid_file" ]; then
    local pid
    pid=$(cat "$pid_file")

    if ps -p "$pid" > /dev/null; then
      echo "Stopping $name (PID $pid)..."
      kill "$pid"

      # Wait up to 10 seconds for the process to terminate
      for i in {1..10}; do
        if ps -p "$pid" > /dev/null; then
          sleep 1
        else
          echo "$name stopped gracefully."
          break
        fi
      done

      # If still running, force kill
      if ps -p "$pid" > /dev/null; then
        echo "$name did not stop in time. Forcing shutdown..."
        kill -9 "$pid"
        echo "$name force killed."
      fi
    else
      echo "$name not running?"
    fi

    rm -f "$pid_file"
  else
    echo "$name PID file not found."
  fi
}

stop_process /tmp/kafka.pid "Kafka"
stop_process /tmp/zookeeper.pid "ZooKeeper"
if [ -f "/tmp/kafka-consumer.pid" ]; then 
  stop_process /tmp/kafka-consumer.pid "Consumer"
fi

