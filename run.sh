#!/bin/bash

export port=8080
export path
export PGPASSWORD="postgres"
path=$1
PGPASSWORD=$2
port=$3
test=$(psql -h localhost -t -U postgres  -c  "CREATE DATABASE filessharing")
echo $test


mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=${port} - --upload.path=${path} - --spring.datasource.password=${PGPASSWORD}"
