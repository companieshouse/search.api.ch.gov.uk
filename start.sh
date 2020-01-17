#!/bin/bash
#
# Start script for search.api.ch.gov.uk

APP_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ -z "${MESOS_SLAVE_PID}" ]]; then
    source ~/.chs_env/private_env
    source ~/.chs_env/global_env
    source ~/.chs_env/search.api.ch.gov.uk/env

    PORT="${SEARCH_API_PORT}"
else
    PORT="$1"
    CONFIG_URL="$2"
    ENVIRONMENT="$3"
    APP_NAME="$4"

    echo "Downloading environment from: ${CONFIG_URL}/${ENVIRONMENT}/${APP_NAME}"
    wget -O "${APP_DIR}/private_env" "${CONFIG_URL}/${ENVIRONMENT}/private_env"
    wget -O "${APP_DIR}/global_env" "${CONFIG_URL}/${ENVIRONMENT}/global_env"
    wget -O "${APP_DIR}/app_env" "${CONFIG_URL}/${ENVIRONMENT}/${APP_NAME}/env"
    source "${APP_DIR}/private_env"
    source "${APP_DIR}/global_env"
    source "${APP_DIR}/app_env"
fi

#exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" -Dlog4j.configurationFile="${APP_DIR}/log4j2.xml" "${APP_DIR}/search.api.ch.gov.uk.jar"
exec java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=21092 -jar -Dserver.port="${PORT}" "${APP_DIR}/search.api.ch.gov.uk.jar"