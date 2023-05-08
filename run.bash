#!/bin/bash

export port=8080
export path='/home/artem/Desktop/uploadFiles/'

export PGPASSWORD="postgres"
test=$(psql -h localhost -t -U postgres  -c  "CREATE DATABASE filessharing")
echo $test


mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=${port} - --upload.path=${path}"
