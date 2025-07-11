#!/bin/bash

# Run script for Enum Patterns demo project

# First build the project
./build.sh

if [ $? -eq 0 ]; then
    echo ""
    echo "Starting Enum Patterns Demo..."
    echo ""
    
    # Run with arguments if provided, otherwise run interactive menu
    if [ $# -eq 0 ]; then
        java -cp out com.example.enumpatterns.Main
    else
        java -cp out com.example.enumpatterns.Main "$@"
    fi
else
    echo "Build failed. Cannot run demos."
    exit 1
fi