#!/bin/bash

# Check if an argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <profile>"
  echo "Profile must be either 'dev' or 'prod'."
  exit 1
fi

# Assign the argument to a variable
PROFILE=$1

# Validate the profile argument
if [ "$PROFILE" != "dev" ] && [ "$PROFILE" != "prod" ]; then
  echo "Invalid profile: $PROFILE"
  echo "Profile must be either 'dev' or 'prod'."
  exit 1
fi

# Run the Maven command with the provided profile
mvn spring-boot:run -f ./backend/pom.xml -Dspring-boot.run.arguments="--active.profile=$PROFILE"
