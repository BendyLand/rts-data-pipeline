#!/bin/bash

./stop.sh
./start.sh
./reset_topic.sh

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
sbt run
SBT_PS=$!
wait "$SBT_PS"

cd ..
./combine.sh

