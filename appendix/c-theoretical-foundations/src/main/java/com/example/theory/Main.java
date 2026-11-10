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
        IO.println("=================================================");
        IO.println("付録C: ソフトウェア工学の理論的基盤 - 実装例デモ");
        IO.println("=================================================\n");
        
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
                IO.println("Unknown demo: " + demo);
                showUsage();
        }
    }
    
    private static void showUsage() {
        IO.println("Usage: java com.example.theory.Main <demo>");
        IO.println("\nAvailable demos:");
        IO.println("  all           - Run all demonstrations");
        IO.println("  complexity    - Software complexity metrics");
        IO.println("  algorithms    - Algorithm analysis and structured programming");
        IO.println("  structures    - Data structures and ADT");
        IO.println("  patterns      - Design patterns analysis");
        IO.println("  concurrent    - Concurrent programming theory");
        IO.println("  specifications - Formal specifications");
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
        IO.println("\n================== COMPLEXITY DEMOS ==================");
        
        IO.println("\n--- Cyclomatic Complexity Example ---");
        ComplexityExample complexityExample = new ComplexityExample();
        IO.println("Complex method result: " + 
            complexityExample.processDataComplex(1, true, "Hello World"));
        IO.println("Refactored method result: " + 
            complexityExample.processData(1, true, "Hello World"));
        
        IO.println("\n--- Time Complexity Examples ---");
        TimeComplexityExamples timeExample = new TimeComplexityExamples();
        int[] testArray = {5, 2, 8, 1, 9, 3};
        IO.println("Original array: " + java.util.Arrays.toString(testArray));
        
        int[] bubbleArray = testArray.clone();
        timeExample.bubbleSort(bubbleArray);
        IO.println("After bubble sort: " + java.util.Arrays.toString(bubbleArray));
        
        IO.println("\n--- Coupling Examples ---");
        CouplingExamples coupling = new CouplingExamples();
        IO.println("Data coupling result: " + coupling.calculateTax(50000, 0.2));
        
        IO.println("\n--- Cohesion Examples ---");
        CohesionExamples.PrimeChecker primeChecker = new CohesionExamples.PrimeChecker();
        IO.println("Is 17 prime? " + primeChecker.isPrime(17));
        IO.println("Is 20 prime? " + primeChecker.isPrime(20));
    }
    
    private static void runAlgorithmDemos() {
        IO.println("\n================== ALGORITHM DEMOS ==================");
        
        IO.println("\n--- Structured Programming ---");
        StructuredProgramming structured = new StructuredProgramming();
        structured.sequenceExample();
        structured.selectionExample(true);
        structured.iterationExample(3);
        
        IO.println("\n--- Loop Invariant Example ---");
        int[] numbers = {3, 7, 2, 9, 1, 5};
        IO.println("Finding max of: " + java.util.Arrays.toString(numbers));
        IO.println("Max value: " + structured.findMax(numbers));
        
        IO.println("\n--- Binary Search with Specifications ---");
        BinarySearchWithSpecs binarySearch = new BinarySearchWithSpecs();
        int[] sortedArray = {1, 3, 5, 7, 9, 11, 13, 15};
        IO.println("Searching in: " + java.util.Arrays.toString(sortedArray));
        IO.println("Index of 7: " + binarySearch.binarySearch(sortedArray, 7));
        IO.println("Index of 8: " + binarySearch.binarySearch(sortedArray, 8));
    }
    
    private static void runStructureDemos() {
        IO.println("\n================== STRUCTURE DEMOS ==================");
        
        IO.println("\n--- Stack ADT Implementation ---");
        ArrayStack<String> stack = new ArrayStack<>(5);
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        IO.println("Stack peek: " + stack.peek());
        IO.println("Stack pop: " + stack.pop());
        IO.println("Stack peek after pop: " + stack.peek());
        
        IO.println("\n--- Dynamic Array Amortized Analysis ---");
        DynamicArray.amortizedAnalysisDemo(100);
        
        IO.println("\n--- Hash Table Performance ---");
        HashTable<String, Integer> hashTable = new HashTable<>(10);
        hashTable.put("apple", 100);
        hashTable.put("banana", 200);
        hashTable.put("cherry", 300);
        hashTable.performanceAnalysis();
        
        IO.println("\n--- Liskov Substitution Principle ---");
        try {
            LiskovSubstitution.testLSPViolation();
        } catch (AssertionError e) {
            IO.println("LSP violation detected: " + e.getMessage());
        }
        LiskovSubstitution.testLSPCompliance();
        IO.println("LSP compliance test passed!");
    }
    
    private static void runPatternDemos() {
        IO.println("\n================== PATTERN DEMOS ==================");
        
        IO.println("\n--- Singleton Pattern ---");
        SingletonPattern.DatabaseConnection db1 = 
            SingletonPattern.DatabaseConnection.getInstance();
        SingletonPattern.DatabaseConnection db2 = 
            SingletonPattern.DatabaseConnection.getInstance();
        IO.println("Same instance? " + (db1 == db2));
        
        SingletonPattern.DatabaseConnectionEnum.INSTANCE.executeQuery("SELECT * FROM users");
        
        IO.println("\n--- Decorator Pattern ---");
        DecoratorPattern.demonstrateDecoratorPattern();
        
        IO.println("\n--- Observer Pattern ---");
        ObserverPattern.demonstrateObserverPattern();
    }
    
    private static void runConcurrentDemos() {
        IO.println("\n================== CONCURRENT DEMOS ==================");
        
        try {
            IO.println("\n--- Race Condition Demo ---");
            RaceConditionExample.demonstrateRaceCondition();
            
            IO.println("\n--- Deadlock Demo ---");
            DeadlockExample.demonstrateDeadlock();
            
            IO.println("\n--- Memory Model Demo ---");
            MemoryModelExample.demonstrateMemoryModel();
            
        } catch (InterruptedException e) {
            System.err.println("Demo interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    private static void runSpecificationDemos() {
        IO.println("\n================== SPECIFICATION DEMOS ==================");
        
        IO.println("\n--- Class Invariants Demo ---");
        BankAccountWithInvariants.demonstrateInvariants();
    }
}