#!/bin/bash
cd backend
mvn -DskipTests clean install
java -jar target/backend-0.0.1-SNAPSHOT.jar
