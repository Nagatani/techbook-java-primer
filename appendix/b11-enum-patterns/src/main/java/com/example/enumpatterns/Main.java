package com.example.enumpatterns;

import com.example.enumpatterns.examples.*;
import com.example.enumpatterns.performance.PerformanceComparison;

/**
 * Main entry point for the Enum Patterns demonstration.
 * Provides an interactive menu to explore different enum patterns.
 */
public class Main {
    
    public static void main(String[] args) {
        IO.println("=====================================");
        IO.println("  Java Enum Patterns Demonstration");
        IO.println("=====================================\n");
        
        if (args.length > 0) {
            // Run specific demo if provided as argument
            runDemo(args[0]);
        } else {
            // Interactive menu
            showInteractiveMenu();
        }
    }
    
    private static void showInteractiveMenu() {
        while (true) {
            printMenu();
            String line = IO.readln("\nEnter your choice (0-7): ");
            if (line == null) {
                break;  // 入力の終わり（Ctrl+DやCtrl+Z）でも終了する
            }
            
            String choice = line.trim();
            
            if ("0".equals(choice)) {
                IO.println("\nThank you for exploring Enum Patterns!");
                break;
            }
            
            IO.println("\n" + "=".repeat(60) + "\n");
            runDemo(choice);
            
            IO.println("\nPress Enter to continue...");
            if (IO.readln() == null) {
                break;
            }
        }
    }
    
    private static void printMenu() {
        IO.println("\nAvailable Demonstrations:");
        IO.println("-------------------------");
        IO.println("1. State Machine Pattern     - Order processing workflow");
        IO.println("2. Strategy Pattern          - Flexible pricing strategies");
        IO.println("3. Permission System         - Role-based access control");
        IO.println("4. Configuration Management  - Type-safe configuration");
        IO.println("5. Comprehensive Example     - E-commerce system");
        IO.println("6. Performance Comparison    - Benchmarks and analysis");
        IO.println("7. Run All Demos            - Execute all demonstrations");
        IO.println("0. Exit");
    }
    
    private static void runDemo(String choice) {
        try {
            switch (choice) {
                case "1", "state" -> {
                    IO.println("Running State Machine Demo...\n");
                    StateMachineDemo.main(new String[0]);
                }
                
                case "2", "strategy" -> {
                    IO.println("Running Strategy Pattern Demo...\n");
                    StrategyPatternDemo.main(new String[0]);
                }
                
                case "3", "permission" -> {
                    IO.println("Running Permission System Demo...\n");
                    PermissionDemo.main(new String[0]);
                }
                
                case "4", "config" -> {
                    IO.println("Running Configuration Management Demo...\n");
                    ConfigurationDemo.main(new String[0]);
                }
                
                case "5", "comprehensive" -> {
                    IO.println("Running Comprehensive Example...\n");
                    ComprehensiveExample.main(new String[0]);
                }
                
                case "6", "performance" -> {
                    IO.println("Running Performance Comparison...\n");
                    PerformanceComparison.main(new String[0]);
                }
                
                case "7", "all" -> {
                    IO.println("Running All Demonstrations...\n");
                    
                    String[] demos = {"1", "2", "3", "4", "5", "6"};
                    for (String demo : demos) {
                        IO.println("\n" + "=".repeat(60) + "\n");
                        runDemo(demo);
                        IO.println("\n" + "=".repeat(60));
                    }
                }
                
                default -> IO.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Quick start guide printed when running without arguments
     */
    static {
        IO.println("This project demonstrates advanced enum patterns in Java.\n");
        IO.println("Quick Start:");
        IO.println("  java Main              - Interactive menu");
        IO.println("  java Main state        - Run state machine demo");
        IO.println("  java Main strategy     - Run strategy pattern demo");
        IO.println("  java Main permission   - Run permission system demo");
        IO.println("  java Main config       - Run configuration demo");
        IO.println("  java Main comprehensive- Run comprehensive example");
        IO.println("  java Main performance  - Run performance benchmarks");
        IO.println("  java Main all          - Run all demos\n");
    }
}