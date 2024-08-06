#!/bin/bash
#
# Start script for search-api-ch-gov-uk

PORT=8080

exec java -jar -Dserver.port="${PORT}" "search-api-ch-gov-uk.jar"
