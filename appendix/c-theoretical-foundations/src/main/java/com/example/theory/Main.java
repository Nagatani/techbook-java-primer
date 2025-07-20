package com.example.theory;

import com.example.theory.algorithms.*;
import com.example.theory.complexity.*;
import com.example.theory.concurrent.*;
import com.example.theory.patterns.*;
import com.example.theory.specifications.*;
import com.example.theory.structures.*;

/**
 * 付録C の全実装例を実行するメインクラス
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("付録C: ソフトウェア工学の理論的基盤 - 実装例デモ");
        System.out.println("=================================================\n");
        
        if (args.length == 0) {
            showUsage();
            return;
        }
        
        String demo = args[0].toLowerCase();
        
        switch (demo) {
            case "all":
                runAllDemos();
                break;
            case "complexity":
                runComplexityDemos();
                break;
            case "algorithms":
                runAlgorithmDemos();
                break;
            case "structures":
                runStructureDemos();
                break;
            case "patterns":
                runPatternDemos();
                break;
            case "concurrent":
                runConcurrentDemos();
                break;
            case "specifications":
                runSpecificationDemos();
                break;
            default:
                System.out.println("Unknown demo: " + demo);
                showUsage();
        }
    }
    
    private static void showUsage() {
        System.out.println("Usage: java com.example.theory.Main <demo>");
        System.out.println("\nAvailable demos:");
        System.out.println("  all           - Run all demonstrations");
        System.out.println("  complexity    - Software complexity metrics");
        System.out.println("  algorithms    - Algorithm analysis and structured programming");
        System.out.println("  structures    - Data structures and ADT");
        System.out.println("  patterns      - Design patterns analysis");
        System.out.println("  concurrent    - Concurrent programming theory");
        System.out.println("  specifications - Formal specifications");
    }
    
    private static void runAllDemos() {
        runComplexityDemos();
        runAlgorithmDemos();
        runStructureDemos();
        runPatternDemos();
        runConcurrentDemos();
        runSpecificationDemos();
    }
    
    private static void runComplexityDemos() {
        System.out.println("\n================== COMPLEXITY DEMOS ==================");
        
        System.out.println("\n--- Cyclomatic Complexity Example ---");
        ComplexityExample complexityExample = new ComplexityExample();
        System.out.println("Complex method result: " + 
            complexityExample.processDataComplex(1, true, "Hello World"));
        System.out.println("Refactored method result: " + 
            complexityExample.processData(1, true, "Hello World"));
        
        System.out.println("\n--- Time Complexity Examples ---");
        TimeComplexityExamples timeExample = new TimeComplexityExamples();
        int[] testArray = {5, 2, 8, 1, 9, 3};
        System.out.println("Original array: " + java.util.Arrays.toString(testArray));
        
        int[] bubbleArray = testArray.clone();
        timeExample.bubbleSort(bubbleArray);
        System.out.println("After bubble sort: " + java.util.Arrays.toString(bubbleArray));
        
        System.out.println("\n--- Coupling Examples ---");
        CouplingExamples coupling = new CouplingExamples();
        System.out.println("Data coupling result: " + coupling.calculateTax(50000, 0.2));
        
        System.out.println("\n--- Cohesion Examples ---");
        CohesionExamples.PrimeChecker primeChecker = new CohesionExamples.PrimeChecker();
        System.out.println("Is 17 prime? " + primeChecker.isPrime(17));
        System.out.println("Is 20 prime? " + primeChecker.isPrime(20));
    }
    
    private static void runAlgorithmDemos() {
        System.out.println("\n================== ALGORITHM DEMOS ==================");
        
        System.out.println("\n--- Structured Programming ---");
        StructuredProgramming structured = new StructuredProgramming();
        structured.sequenceExample();
        structured.selectionExample(true);
        structured.iterationExample(3);
        
        System.out.println("\n--- Loop Invariant Example ---");
        int[] numbers = {3, 7, 2, 9, 1, 5};
        System.out.println("Finding max of: " + java.util.Arrays.toString(numbers));
        System.out.println("Max value: " + structured.findMax(numbers));
        
        System.out.println("\n--- Binary Search with Specifications ---");
        BinarySearchWithSpecs binarySearch = new BinarySearchWithSpecs();
        int[] sortedArray = {1, 3, 5, 7, 9, 11, 13, 15};
        System.out.println("Searching in: " + java.util.Arrays.toString(sortedArray));
        System.out.println("Index of 7: " + binarySearch.binarySearch(sortedArray, 7));
        System.out.println("Index of 8: " + binarySearch.binarySearch(sortedArray, 8));
    }
    
    private static void runStructureDemos() {
        System.out.println("\n================== STRUCTURE DEMOS ==================");
        
        System.out.println("\n--- Stack ADT Implementation ---");
        ArrayStack<String> stack = new ArrayStack<>(5);
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        System.out.println("Stack peek: " + stack.peek());
        System.out.println("Stack pop: " + stack.pop());
        System.out.println("Stack peek after pop: " + stack.peek());
        
        System.out.println("\n--- Dynamic Array Amortized Analysis ---");
        DynamicArray.amortizedAnalysisDemo(100);
        
        System.out.println("\n--- Hash Table Performance ---");
        HashTable<String, Integer> hashTable = new HashTable<>(10);
        hashTable.put("apple", 100);
        hashTable.put("banana", 200);
        hashTable.put("cherry", 300);
        hashTable.performanceAnalysis();
        
        System.out.println("\n--- Liskov Substitution Principle ---");
        try {
            LiskovSubstitution.testLSPViolation();
        } catch (AssertionError e) {
            System.out.println("LSP violation detected: " + e.getMessage());
        }
        LiskovSubstitution.testLSPCompliance();
        System.out.println("LSP compliance test passed!");
    }
    
    private static void runPatternDemos() {
        System.out.println("\n================== PATTERN DEMOS ==================");
        
        System.out.println("\n--- Singleton Pattern ---");
        SingletonPattern.DatabaseConnection db1 = 
            SingletonPattern.DatabaseConnection.getInstance();
        SingletonPattern.DatabaseConnection db2 = 
            SingletonPattern.DatabaseConnection.getInstance();
        System.out.println("Same instance? " + (db1 == db2));
        
        SingletonPattern.DatabaseConnectionEnum.INSTANCE.executeQuery("SELECT * FROM users");
        
        System.out.println("\n--- Decorator Pattern ---");
        DecoratorPattern.demonstrateDecoratorPattern();
        
        System.out.println("\n--- Observer Pattern ---");
        ObserverPattern.demonstrateObserverPattern();
    }
    
    private static void runConcurrentDemos() {
        System.out.println("\n================== CONCURRENT DEMOS ==================");
        
        try {
            System.out.println("\n--- Race Condition Demo ---");
            RaceConditionExample.demonstrateRaceCondition();
            
            System.out.println("\n--- Deadlock Demo ---");
            DeadlockExample.demonstrateDeadlock();
            
            System.out.println("\n--- Memory Model Demo ---");
            MemoryModelExample.demonstrateMemoryModel();
            
        } catch (InterruptedException e) {
            System.err.println("Demo interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    private static void runSpecificationDemos() {
        System.out.println("\n================== SPECIFICATION DEMOS ==================");
        
        System.out.println("\n--- Class Invariants Demo ---");
        BankAccountWithInvariants.demonstrateInvariants();
    }
}