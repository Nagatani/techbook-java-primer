package com.example.enumpatterns.performance;

import java.util.*;
import java.util.concurrent.*;

/**
 * Performance comparison demonstrating the efficiency of enum-based patterns.
 * Shows benchmarks for EnumSet, EnumMap, and enum state machines.
 */
public class PerformanceComparison {
    
    // Sample enum for testing
    enum Operation {
        CREATE, READ, UPDATE, DELETE, EXECUTE, ADMIN, 
        BACKUP, RESTORE, EXPORT, IMPORT, VALIDATE, AUDIT
    }
    
    enum Status {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, SUSPENDED,
        READY, WAITING, ACTIVE, INACTIVE, ARCHIVED, DELETED
    }
    
    public static void main(String[] args) {
        IO.println("=== Enum Pattern Performance Comparison ===\n");
        
        // Warm up JVM
        warmUp();
        
        // Run benchmarks
        benchmarkEnumSetVsHashSet();
        benchmarkEnumMapVsHashMap();
        benchmarkEnumSwitchVsIfElse();
        benchmarkStateTransitions();
        benchmarkMemoryUsage();
    }
    
    private static void warmUp() {
        IO.println("Warming up JVM...");
        for (int i = 0; i < 10000; i++) {
            EnumSet<Operation> set = EnumSet.allOf(Operation.class);
            set.contains(Operation.READ);
            set.add(Operation.UPDATE);
        }
        IO.println("Warm-up complete.\n");
    }
    
    private static void benchmarkEnumSetVsHashSet() {
        IO.println("Benchmark 1: EnumSet vs HashSet");
        IO.println("================================");
        
        int iterations = 10_000_000;
        
        // EnumSet benchmark
        EnumSet<Operation> enumSet = EnumSet.allOf(Operation.class);
        long start = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            enumSet.contains(Operation.READ);
            enumSet.add(Operation.UPDATE);
            enumSet.remove(Operation.DELETE);
            enumSet.containsAll(EnumSet.of(Operation.CREATE, Operation.READ));
        }
        
        long enumSetTime = System.nanoTime() - start;
        
        // HashSet benchmark
        Set<Operation> hashSet = new HashSet<>(Arrays.asList(Operation.values()));
        start = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            hashSet.contains(Operation.READ);
            hashSet.add(Operation.UPDATE);
            hashSet.remove(Operation.DELETE);
            hashSet.containsAll(Set.of(Operation.CREATE, Operation.READ));
        }
        
        long hashSetTime = System.nanoTime() - start;
        
        // TreeSet benchmark
        Set<Operation> treeSet = new TreeSet<>(Arrays.asList(Operation.values()));
        start = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            treeSet.contains(Operation.READ);
            treeSet.add(Operation.UPDATE);
            treeSet.remove(Operation.DELETE);
            treeSet.containsAll(Set.of(Operation.CREATE, Operation.READ));
        }
        
        long treeSetTime = System.nanoTime() - start;
        
        // Results
        IO.println("Operations: %,d".formatted(iterations));
        IO.println("EnumSet:  %,d ms".formatted(enumSetTime / 1_000_000));
        IO.println("HashSet:  %,d ms (%.1fx slower)".formatted(
            hashSetTime / 1_000_000, (double) hashSetTime / enumSetTime));
        IO.println("TreeSet:  %,d ms (%.1fx slower)".formatted(
            treeSetTime / 1_000_000, (double) treeSetTime / enumSetTime));
        
        // Set operations benchmark
        IO.println("\nSet Operations Performance:");
        benchmarkSetOperations();
    }
    
    private static void benchmarkSetOperations() {
        int iterations = 1_000_000;
        
        // Create sets
        EnumSet<Operation> enumSet1 = EnumSet.of(Operation.CREATE, Operation.READ, Operation.UPDATE);
        EnumSet<Operation> enumSet2 = EnumSet.of(Operation.READ, Operation.DELETE, Operation.EXECUTE);
        
        Set<Operation> hashSet1 = new HashSet<>(Arrays.asList(Operation.CREATE, Operation.READ, Operation.UPDATE));
        Set<Operation> hashSet2 = new HashSet<>(Arrays.asList(Operation.READ, Operation.DELETE, Operation.EXECUTE));
        
        // Union operation
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            EnumSet<Operation> union = EnumSet.copyOf(enumSet1);
            union.addAll(enumSet2);
        }
        long enumUnionTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Set<Operation> union = new HashSet<>(hashSet1);
            union.addAll(hashSet2);
        }
        long hashUnionTime = System.nanoTime() - start;
        
        // Intersection operation
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            EnumSet<Operation> intersection = EnumSet.copyOf(enumSet1);
            intersection.retainAll(enumSet2);
        }
        long enumIntersectionTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Set<Operation> intersection = new HashSet<>(hashSet1);
            intersection.retainAll(hashSet2);
        }
        long hashIntersectionTime = System.nanoTime() - start;
        
        IO.println("Union    - EnumSet: %,d ms, HashSet: %,d ms (%.1fx slower)".formatted(
            enumUnionTime / 1_000_000, hashUnionTime / 1_000_000,
            (double) hashUnionTime / enumUnionTime));
        IO.println("Intersect- EnumSet: %,d ms, HashSet: %,d ms (%.1fx slower)".formatted(
            enumIntersectionTime / 1_000_000, hashIntersectionTime / 1_000_000,
            (double) hashIntersectionTime / enumIntersectionTime));
    }
    
    private static void benchmarkEnumMapVsHashMap() {
        IO.println("\n\nBenchmark 2: EnumMap vs HashMap");
        IO.println("================================");
        
        int iterations = 10_000_000;
        
        // EnumMap benchmark
        EnumMap<Status, String> enumMap = new EnumMap<>(Status.class);
        for (Status status : Status.values()) {
            enumMap.put(status, status.name());
        }
        
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            enumMap.get(Status.PROCESSING);
            enumMap.put(Status.COMPLETED, "Done");
            enumMap.containsKey(Status.FAILED);
        }
        long enumMapTime = System.nanoTime() - start;
        
        // HashMap benchmark
        Map<Status, String> hashMap = new HashMap<>();
        for (Status status : Status.values()) {
            hashMap.put(status, status.name());
        }
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            hashMap.get(Status.PROCESSING);
            hashMap.put(Status.COMPLETED, "Done");
            hashMap.containsKey(Status.FAILED);
        }
        long hashMapTime = System.nanoTime() - start;
        
        // TreeMap benchmark
        Map<Status, String> treeMap = new TreeMap<>();
        for (Status status : Status.values()) {
            treeMap.put(status, status.name());
        }
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            treeMap.get(Status.PROCESSING);
            treeMap.put(Status.COMPLETED, "Done");
            treeMap.containsKey(Status.FAILED);
        }
        long treeMapTime = System.nanoTime() - start;
        
        // Results
        IO.println("Operations: %,d".formatted(iterations));
        IO.println("EnumMap:  %,d ms".formatted(enumMapTime / 1_000_000));
        IO.println("HashMap:  %,d ms (%.1fx slower)".formatted(
            hashMapTime / 1_000_000, (double) hashMapTime / enumMapTime));
        IO.println("TreeMap:  %,d ms (%.1fx slower)".formatted(
            treeMapTime / 1_000_000, (double) treeMapTime / enumMapTime));
    }
    
    private static void benchmarkEnumSwitchVsIfElse() {
        IO.println("\n\nBenchmark 3: Enum Switch vs If-Else Chain");
        IO.println("==========================================");
        
        int iterations = 50_000_000;
        Operation[] operations = Operation.values();
        Random random = new Random(42);
        
        // Generate random operations
        Operation[] testOps = new Operation[iterations];
        for (int i = 0; i < iterations; i++) {
            testOps[i] = operations[random.nextInt(operations.length)];
        }
        
        // Switch statement benchmark
        long start = System.nanoTime();
        int switchResult = 0;
        
        for (Operation op : testOps) {
            switchResult += switch (op) {
                case CREATE -> 1;
                case READ -> 2;
                case UPDATE -> 3;
                case DELETE -> 4;
                case EXECUTE -> 5;
                case ADMIN -> 6;
                case BACKUP -> 7;
                case RESTORE -> 8;
                case EXPORT -> 9;
                case IMPORT -> 10;
                case VALIDATE -> 11;
                case AUDIT -> 12;
            };
        }
        
        long switchTime = System.nanoTime() - start;
        
        // If-else chain benchmark
        start = System.nanoTime();
        int ifElseResult = 0;
        
        for (Operation op : testOps) {
            if (op == Operation.CREATE) ifElseResult += 1;
            else if (op == Operation.READ) ifElseResult += 2;
            else if (op == Operation.UPDATE) ifElseResult += 3;
            else if (op == Operation.DELETE) ifElseResult += 4;
            else if (op == Operation.EXECUTE) ifElseResult += 5;
            else if (op == Operation.ADMIN) ifElseResult += 6;
            else if (op == Operation.BACKUP) ifElseResult += 7;
            else if (op == Operation.RESTORE) ifElseResult += 8;
            else if (op == Operation.EXPORT) ifElseResult += 9;
            else if (op == Operation.IMPORT) ifElseResult += 10;
            else if (op == Operation.VALIDATE) ifElseResult += 11;
            else if (op == Operation.AUDIT) ifElseResult += 12;
        }
        
        long ifElseTime = System.nanoTime() - start;
        
        // String comparison benchmark
        start = System.nanoTime();
        int stringResult = 0;
        
        for (Operation op : testOps) {
            String opName = op.name();
            if (opName.equals("CREATE")) stringResult += 1;
            else if (opName.equals("READ")) stringResult += 2;
            else if (opName.equals("UPDATE")) stringResult += 3;
            else if (opName.equals("DELETE")) stringResult += 4;
            else if (opName.equals("EXECUTE")) stringResult += 5;
            else if (opName.equals("ADMIN")) stringResult += 6;
            else if (opName.equals("BACKUP")) stringResult += 7;
            else if (opName.equals("RESTORE")) stringResult += 8;
            else if (opName.equals("EXPORT")) stringResult += 9;
            else if (opName.equals("IMPORT")) stringResult += 10;
            else if (opName.equals("VALIDATE")) stringResult += 11;
            else if (opName.equals("AUDIT")) stringResult += 12;
        }
        
        long stringTime = System.nanoTime() - start;
        
        // Results
        IO.println("Operations: %,d".formatted(iterations));
        IO.println("Switch:      %,d ms".formatted(switchTime / 1_000_000));
        IO.println("If-Else:     %,d ms (%.1fx slower)".formatted(
            ifElseTime / 1_000_000, (double) ifElseTime / switchTime));
        IO.println("String cmp:  %,d ms (%.1fx slower)".formatted(
            stringTime / 1_000_000, (double) stringTime / switchTime));
        
        // Verify results are the same
        IO.println("Results match: %b".formatted(
            switchResult == ifElseResult && ifElseResult == stringResult));
    }
    
    private static void benchmarkStateTransitions() {
        IO.println("\n\nBenchmark 4: State Machine Performance");
        IO.println("======================================");
        
        // Simple state machine enum
        enum State {
            INIT {
                @Override
                State next() { return RUNNING; }
            },
            RUNNING {
                @Override
                State next() { return COMPLETED; }
            },
            COMPLETED {
                @Override
                State next() { return this; }
            };
            
            abstract State next();
        }
        
        int iterations = 50_000_000;
        
        // Enum state machine
        long start = System.nanoTime();
        State state = State.INIT;
        for (int i = 0; i < iterations; i++) {
            state = state.next();
            if (state == State.COMPLETED) {
                state = State.INIT;
            }
        }
        long enumTime = System.nanoTime() - start;
        
        // String-based state machine
        start = System.nanoTime();
        String stringState = "INIT";
        for (int i = 0; i < iterations; i++) {
            if (stringState.equals("INIT")) {
                stringState = "RUNNING";
            } else if (stringState.equals("RUNNING")) {
                stringState = "COMPLETED";
            } else if (stringState.equals("COMPLETED")) {
                stringState = "INIT";
            }
        }
        long stringTime = System.nanoTime() - start;
        
        // Integer-based state machine
        start = System.nanoTime();
        int intState = 0; // 0=INIT, 1=RUNNING, 2=COMPLETED
        for (int i = 0; i < iterations; i++) {
            intState = (intState + 1) % 3;
        }
        long intTime = System.nanoTime() - start;
        
        // Results
        IO.println("Transitions: %,d".formatted(iterations));
        IO.println("Enum:     %,d ms".formatted(enumTime / 1_000_000));
        IO.println("String:   %,d ms (%.1fx slower)".formatted(
            stringTime / 1_000_000, (double) stringTime / enumTime));
        IO.println("Integer:  %,d ms (%.1fx %s)".formatted(
            intTime / 1_000_000, 
            intTime > enumTime ? (double) intTime / enumTime : (double) enumTime / intTime,
            intTime > enumTime ? "slower" : "faster"));
    }
    
    private static void benchmarkMemoryUsage() {
        IO.println("\n\nBenchmark 5: Memory Usage Comparison");
        IO.println("====================================");
        
        int size = 1000;
        
        // Measure EnumSet memory
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();
        
        List<EnumSet<Operation>> enumSets = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            enumSets.add(EnumSet.allOf(Operation.class));
        }
        
        System.gc();
        long enumSetMem = runtime.totalMemory() - runtime.freeMemory() - memBefore;
        
        // Measure HashSet memory
        enumSets.clear();
        System.gc();
        memBefore = runtime.totalMemory() - runtime.freeMemory();
        
        List<Set<Operation>> hashSets = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hashSets.add(new HashSet<>(Arrays.asList(Operation.values())));
        }
        
        System.gc();
        long hashSetMem = runtime.totalMemory() - runtime.freeMemory() - memBefore;
        
        // Results
        IO.println("Creating %,d sets with %d elements each:".formatted(size, Operation.values().length));
        IO.println("EnumSet memory:  ~%,d bytes (%d bytes per set)".formatted(
            enumSetMem, enumSetMem / size));
        IO.println("HashSet memory:  ~%,d bytes (%d bytes per set)".formatted(
            hashSetMem, hashSetMem / size));
        IO.println("Memory savings:  %.1f%%".formatted(
            (1 - (double) enumSetMem / hashSetMem) * 100));
        
        IO.println("\nTheoretical Memory Usage:");
        IO.println("EnumSet: Uses bit vector (1 bit per enum constant)");
        IO.println("  %d enums = %d bits = %d bytes".formatted(
            Operation.values().length, Operation.values().length, 
            (Operation.values().length + 7) / 8));
        IO.println("HashSet: Uses hash table with object references");
        IO.println("  ~%d bytes per entry × %d entries = ~%d bytes".formatted(
            32, Operation.values().length, 32 * Operation.values().length));
    }
}