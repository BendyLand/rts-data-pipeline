#!/bin/bash

PIDS=$(ps -a | awk '{print $1, $4}' | grep java | awk '{print $1}')
if [ -n "$PIDS" ]; then
  echo "Stopping processes:"
  echo $PIDS
  kill $PIDS
  echo "Done!"
else
  echo "No lingering Java processes found."
fi


