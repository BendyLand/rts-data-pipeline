#!/bin/bash

./scripts/stop.sh
./scripts/start.sh
./scripts/reset_topic.sh

cd generator
go build -o generator_bin

echo "Starting data generator..."
./generator_bin &
GEN_PS=$!

sleep 30

echo "Shutting down data generator..."
kill -SIGINT "$GEN_PS"
wait "$GEN_PS"
echo "Data generator has stopped."

cd ../rts_data_pipeline
echo "Starting Spark job..."
sbt run &
SBT_PS=$!
sleep 30 
echo "Stopping consumer..."
kill -SIGINT "$SBT_PS"

cd ..
./scripts/combine.sh

