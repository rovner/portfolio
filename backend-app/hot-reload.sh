#!/bin/bash
chmod a+x gradlew
./gradlew bootRun &
while true; do
  inotifywait -e modify,create,delete,move -r ./src/ && ./gradlew compileJava
done