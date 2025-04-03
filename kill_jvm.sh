#!/bin/bash

kill `ps -a | awk '{print $1, $4}' | grep java | awk '{print $1}' | echo`


