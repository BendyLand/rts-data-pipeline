#!/bin/bash

pids=`ps | awk '{print $1, $4}' | grep "java" | awk '{print $1}'`
echo $pids | pbcopy
echo "PIDs copied to system clipboard."

