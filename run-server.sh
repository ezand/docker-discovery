#!/bin/bash

if [ "$1" == "repl" ]; then
  cd server && lein repl
else
  cd server && lein run
fi
