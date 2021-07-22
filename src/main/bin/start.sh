#!/bin/sh
nohup java -jar fiveChess-1.4-SNAPSHOT.jar >/dev/null 2>&1 &
echo $! > process.pid
