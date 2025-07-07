#!/bin/bash

# Build script for Enum Patterns project

echo "Building Enum Patterns Project..."
echo "================================"

# Create output directory
mkdir -p out

# Compile all Java files
echo "Compiling Java files..."
javac -d out src/main/java/com/example/enumpatterns/**/*.java src/main/java/com/example/enumpatterns/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    echo "To run the examples:"
    echo "  java -cp out com.example.enumpatterns.Main"
    echo ""
    echo "Or run specific demos:"
    echo "  java -cp out com.example.enumpatterns.examples.StateMachineDemo"
    echo "  java -cp out com.example.enumpatterns.examples.PermissionDemo"
    echo "  java -cp out com.example.enumpatterns.examples.StrategyPatternDemo"
    echo "  java -cp out com.example.enumpatterns.examples.ConfigurationDemo"
    echo "  java -cp out com.example.enumpatterns.examples.ComprehensiveExample"
    echo "  java -cp out com.example.enumpatterns.performance.PerformanceComparison"
else
    echo "Build failed!"
    exit 1
fi