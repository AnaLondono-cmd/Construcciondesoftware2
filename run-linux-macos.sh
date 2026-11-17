#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mvn clean test
mvn spring-boot:run
