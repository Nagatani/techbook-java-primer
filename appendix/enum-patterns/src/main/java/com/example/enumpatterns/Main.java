package com.example.enumpatterns;

import com.example.enumpatterns.examples.*;
import com.example.enumpatterns.performance.PerformanceComparison;
import java.util.Scanner;

/**
 * Main entry point for the Enum Patterns demonstration.
 * Provides an interactive menu to explore different enum patterns.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("  Java Enum Patterns Demonstration");
        System.out.println("=====================================\n");
        
        if (args.length > 0) {
            // Run specific demo if provided as argument
            runDemo(args[0]);
        } else {
            // Interactive menu
            showInteractiveMenu();
        }
    }
    
    private static void showInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            printMenu();
            System.out.print("\nEnter your choice (0-7): ");
            
            String choice = scanner.nextLine().trim();
            
            if ("0".equals(choice)) {
                System.out.println("\nThank you for exploring Enum Patterns!");
                break;
            }
            
            System.out.println("\n" + "=".repeat(60) + "\n");
            runDemo(choice);
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
        
        scanner.close();
    }
    
    private static void printMenu() {
        System.out.println("\nAvailable Demonstrations:");
        System.out.println("-------------------------");
        System.out.println("1. State Machine Pattern     - Order processing workflow");
        System.out.println("2. Strategy Pattern          - Flexible pricing strategies");
        System.out.println("3. Permission System         - Role-based access control");
        System.out.println("4. Configuration Management  - Type-safe configuration");
        System.out.println("5. Comprehensive Example     - E-commerce system");
        System.out.println("6. Performance Comparison    - Benchmarks and analysis");
        System.out.println("7. Run All Demos            - Execute all demonstrations");
        System.out.println("0. Exit");
    }
    
    private static void runDemo(String choice) {
        try {
            switch (choice) {
                case "1", "state" -> {
                    System.out.println("Running State Machine Demo...\n");
                    StateMachineDemo.main(new String[0]);
                }
                
                case "2", "strategy" -> {
                    System.out.println("Running Strategy Pattern Demo...\n");
                    StrategyPatternDemo.main(new String[0]);
                }
                
                case "3", "permission" -> {
                    System.out.println("Running Permission System Demo...\n");
                    PermissionDemo.main(new String[0]);
                }
                
                case "4", "config" -> {
                    System.out.println("Running Configuration Management Demo...\n");
                    ConfigurationDemo.main(new String[0]);
                }
                
                case "5", "comprehensive" -> {
                    System.out.println("Running Comprehensive Example...\n");
                    ComprehensiveExample.main(new String[0]);
                }
                
                case "6", "performance" -> {
                    System.out.println("Running Performance Comparison...\n");
                    PerformanceComparison.main(new String[0]);
                }
                
                case "7", "all" -> {
                    System.out.println("Running All Demonstrations...\n");
                    
                    String[] demos = {"1", "2", "3", "4", "5", "6"};
                    for (String demo : demos) {
                        System.out.println("\n" + "=".repeat(60) + "\n");
                        runDemo(demo);
                        System.out.println("\n" + "=".repeat(60));
                    }
                }
                
                default -> System.out.println("Invalid choice. Please try again.");
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
        System.out.println("This project demonstrates advanced enum patterns in Java.\n");
        System.out.println("Quick Start:");
        System.out.println("  java Main              - Interactive menu");
        System.out.println("  java Main state        - Run state machine demo");
        System.out.println("  java Main strategy     - Run strategy pattern demo");
        System.out.println("  java Main permission   - Run permission system demo");
        System.out.println("  java Main config       - Run configuration demo");
        System.out.println("  java Main comprehensive- Run comprehensive example");
        System.out.println("  java Main performance  - Run performance benchmarks");
        System.out.println("  java Main all          - Run all demos\n");
    }
}