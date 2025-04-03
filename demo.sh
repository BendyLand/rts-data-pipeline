#!/bin/bash

# Helper to find sbt process PID
find_sbt_pid() {
  ps -eo pid,comm | grep sbt | grep -v grep | awk '{print $1}'
}

# Helper to find JVM process PID (just for fallback if needed)
find_java_pid() {
  ps -eo pid,comm | grep java | grep -v grep | awk '{print $1}'
}

./stop.sh
./start.sh
./reset_topic.sh

cd generator
go build -o generator_bin

echo "Starting data generator..."
./generator_bin &
GEN_PS=$!

sleep 20

echo "Shutting down data generator..."
kill -SIGINT "$GEN_PS"
wait "$GEN_PS"
echo "Data generator has stopped."

cd ../rts_data_pipeline
echo "Starting Spark job..."
sbt run
SBT_PID=$!
wait "$SBT_PID"

cd ..
./combine.sh

