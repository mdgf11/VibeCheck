#!/bin/bash

# Check if an argument was provided
if [ -z "$1" ]; then
  echo "Usage: $0 {dev|production|serve}"
  exit 1
fi

# Run the appropriate npm script based on the argument
case "$1" in
  dev)
    npm run --prefix ./frontend dev
    ;;
  production)
    npm run --prefix ./frontend prod
    ;;
  serve)
    npm run --prefix ./frontend serve
    ;;
  *)
    echo "Invalid argument: $1"
    echo "Usage: $0 {dev|production|serve}"
    exit 1
    ;;
esac
