#!/bin/sh
set -e

# Expand environment variables
export JAVA_OPTS="${JAVA_OPTS} ${JAVA_OPTS_APPEND}"

# Dynamic configuration generation
if [ -n "$CONFIG_SERVER_URL" ]; then
    echo "Fetching configuration from $CONFIG_SERVER_URL"
    curl -sf "$CONFIG_SERVER_URL" > application.properties
fi

# Mount secrets
if [ -d "/run/secrets" ]; then
    for secret in /run/secrets/*; do
        if [ -f "$secret" ]; then
            secret_name=$(basename "$secret" | tr '[:lower:]' '[:upper:]')
            export "$secret_name"=$(cat "$secret")
        fi
    done
fi

# JMX configuration (debug only)
if [ "$ENABLE_JMX" = "true" ]; then
    export JAVA_OPTS="${JAVA_OPTS} \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.port=9999 \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false"
fi

# Start application
exec java ${JAVA_OPTS} -jar app.jar "$@"