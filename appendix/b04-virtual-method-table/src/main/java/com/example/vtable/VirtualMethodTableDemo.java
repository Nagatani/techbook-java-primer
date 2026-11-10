package com.example.vtable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 仮想メソッドテーブル（vtable）と動的ディスパッチのデモンストレーション
 * JVMがどのようにポリモーフィックメソッド呼び出しを処理するかを学習
 */
public class VirtualMethodTableDemo {
    
    /**
     * ベースクラス階層（vtable構築の例）
     */
    public static abstract class Shape {
        protected double x, y;
        
        public Shape(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        // 仮想メソッド（vtableエントリ）
        public abstract double area();
        public abstract double perimeter();
        
        // 継承されるメソッド
        public void move(double dx, double dy) {
            this.x += dx;
            this.y += dy;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
        
        // Object継承メソッドのオーバーライド
        @Override
        public String toString() {
            return String.format("%s at (%.1f, %.1f)", 
                                getClass().getSimpleName(), x, y);
        }
    }
    
    public static class Circle extends Shape {
        private final double radius;
        
        public Circle(double x, double y, double radius) {
            super(x, y);
            this.radius = radius;
        }
        
        // vtableエントリの実装
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
        
        @Override
        public double perimeter() {
            return 2 * Math.PI * radius;
        }
        
        // 新規メソッド（vtableに追加）
        public double getRadius() {
            return radius;
        }
        
        @Override
        public String toString() {
            return String.format("Circle(r=%.1f) at (%.1f, %.1f)", 
                               radius, x, y);
        }
    }
    
    public static class Rectangle extends Shape {
        private final double width, height;
        
        public Rectangle(double x, double y, double width, double height) {
            super(x, y);
            this.width = width;
            this.height = height;
        }
        
        // vtableエントリの実装
        @Override
        public double area() {
            return width * height;
        }
        
        @Override
        public double perimeter() {
            return 2 * (width + height);
        }
        
        // 新規メソッド
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        
        @Override
        public String toString() {
            return String.format("Rectangle(%.1fx%.1f) at (%.1f, %.1f)", 
                               width, height, x, y);
        }
    }
    
    public static class Triangle extends Shape {
        private final double base, height;
        
        public Triangle(double x, double y, double base, double height) {
            super(x, y);
            this.base = base;
            this.height = height;
        }
        
        @Override
        public double area() {
            return 0.5 * base * height;
        }
        
        @Override
        public double perimeter() {
            // 正三角形の仮定
            return 3 * base;
        }
        
        public double getBase() { return base; }
        public double getTriangleHeight() { return height; }
        
        @Override
        public String toString() {
            return String.format("Triangle(base=%.1f, h=%.1f) at (%.1f, %.1f)", 
                               base, height, x, y);
        }
    }
    
    /**
     * 仮想メソッドテーブルのシミュレーション
     */
    public static class VTableSimulator {
        
        /**
         * 擬似的なvtableエントリ
         */
        public static class VTableEntry {
            private final String methodName;
            private final Class<?> implementingClass;
            private final int index;
            
            public VTableEntry(String methodName, Class<?> implementingClass, int index) {
                this.methodName = methodName;
                this.implementingClass = implementingClass;
                this.index = index;
            }
            
            @Override
            public String toString() {
                return String.format("[%d] %s -> %s.%s", 
                                   index, methodName, 
                                   implementingClass.getSimpleName(), 
                                   methodName);
            }
        }
        
        /**
         * 擬似的なvtable
         */
        public static class VTable {
            private final Class<?> ownerClass;
            private final List<VTableEntry> entries;
            
            public VTable(Class<?> ownerClass) {
                this.ownerClass = ownerClass;
                this.entries = new ArrayList<>();
            }
            
            public void addEntry(String methodName, Class<?> implementingClass) {
                entries.add(new VTableEntry(methodName, implementingClass, entries.size()));
            }
            
            public VTableEntry getEntry(int index) {
                return entries.get(index);
            }
            
            public int size() {
                return entries.size();
            }
            
            public void printVTable() {
                IO.println("=== " + ownerClass.getSimpleName() + " vtable ===");
                for (VTableEntry entry : entries) {
                    IO.println("  " + entry);
                }
                IO.println();
            }
        }
        
        /**
         * vtableを構築するシミュレーション
         */
        public static VTable buildVTable(Class<?> clazz) {
            VTable vtable = new VTable(clazz);
            
            // Object継承メソッド（簡略化）
            vtable.addEntry("toString", clazz);
            vtable.addEntry("equals", Object.class);
            vtable.addEntry("hashCode", Object.class);
            
            // Shape階層のメソッド
            if (Shape.class.isAssignableFrom(clazz)) {
                vtable.addEntry("move", Shape.class);
                vtable.addEntry("getX", Shape.class);
                vtable.addEntry("getY", Shape.class);
                vtable.addEntry("area", clazz); // 各サブクラスで実装
                vtable.addEntry("perimeter", clazz); // 各サブクラスで実装
                
                // サブクラス固有メソッド
                if (clazz == Circle.class) {
                    vtable.addEntry("getRadius", Circle.class);
                } else if (clazz == Rectangle.class) {
                    vtable.addEntry("getWidth", Rectangle.class);
                    vtable.addEntry("getHeight", Rectangle.class);
                } else if (clazz == Triangle.class) {
                    vtable.addEntry("getBase", Triangle.class);
                    vtable.addEntry("getTriangleHeight", Triangle.class);
                }
            }
            
            return vtable;
        }
    }
    
    /**
     * 動的ディスパッチのシミュレーション
     */
    public static class DispatchSimulator {
        
        /**
         * 仮想メソッド呼び出しのシミュレーション
         */
        public static void simulateVirtualCall(Shape shape, String methodName) {
            IO.println("=== Virtual Method Call Simulation ===");
            IO.println("Object: " + shape.toString());
            IO.println("Calling method: " + methodName);
            
            // 1. オブジェクトの実際の型を取得
            Class<?> actualType = shape.getClass();
            IO.println("Step 1: Actual type = " + actualType.getSimpleName());
            
            // 2. その型のvtableを取得
            VTableSimulator.VTable vtable = VTableSimulator.buildVTable(actualType);
            IO.println("Step 2: Retrieved vtable for " + actualType.getSimpleName());
            
            // 3. メソッドのインデックスを取得（簡略化）
            int methodIndex = findMethodIndex(methodName);
            IO.println("Step 3: Method index = " + methodIndex);
            
            // 4. vtableからメソッドを取得
            if (methodIndex >= 0 && methodIndex < vtable.size()) {
                VTableSimulator.VTableEntry entry = vtable.getEntry(methodIndex);
                IO.println("Step 4: Found method = " + entry);
                
                // 5. 実際にメソッドを呼び出し
                IO.println("Step 5: Invoking method...");
                double result = callMethod(shape, methodName);
                IO.println("Result: " + result);
            } else {
                IO.println("Step 4: Method not found in vtable");
            }
            
            IO.println();
        }
        
        private static int findMethodIndex(String methodName) {
            // 簡略化したインデックス計算
            switch (methodName) {
                case "area": return 5;
                case "perimeter": return 6;
                case "getX": return 4;
                case "getY": return 5;
                default: return -1;
            }
        }
        
        private static double callMethod(Shape shape, String methodName) {
            switch (methodName) {
                case "area": return shape.area();
                case "perimeter": return shape.perimeter();
                case "getX": return shape.getX();
                case "getY": return shape.getY();
                default: return 0.0;
            }
        }
    }
    
    /**
     * 呼び出し頻度によるJVM最適化のデモンストレーション
     */
    public static class OptimizationDemo {
        
        /**
         * モノモーフィック呼び出し（1つの型のみ）
         */
        public static void monomorphicCalls() {
            IO.println("=== Monomorphic Calls (Single Type) ===");
            IO.println("Only Circle objects - JVM can inline and optimize");
            
            List<Circle> circles = Arrays.asList(
                new Circle(0, 0, 5),
                new Circle(1, 1, 3),
                new Circle(2, 2, 7)
            );
            
            long startTime = System.nanoTime();
            double totalArea = 0;
            
            // 同一型のみの呼び出し
            for (int i = 0; i < 10000; i++) {
                for (Circle circle : circles) {
                    totalArea += circle.area(); // 直接呼び出し・インライン化可能
                }
            }
            
            long endTime = System.nanoTime();
            
            IO.println("Total area: " + String.format("%.2f", totalArea));
            IO.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
            IO.println("Optimization: Direct call, likely inlined");
            IO.println();
        }
        
        /**
         * バイモーフィック呼び出し（2つの型）
         */
        public static void bimorphicCalls() {
            IO.println("=== Bimorphic Calls (Two Types) ===");
            IO.println("Circle and Rectangle - JVM can still optimize with branch prediction");
            
            List<Shape> shapes = Arrays.asList(
                new Circle(0, 0, 5),
                new Rectangle(1, 1, 4, 6),
                new Circle(2, 2, 3),
                new Rectangle(3, 3, 5, 5)
            );
            
            long startTime = System.nanoTime();
            double totalArea = 0;
            
            // 2つの型の呼び出し
            for (int i = 0; i < 10000; i++) {
                for (Shape shape : shapes) {
                    totalArea += shape.area(); // vtable経由だが最適化可能
                }
            }
            
            long endTime = System.nanoTime();
            
            IO.println("Total area: " + String.format("%.2f", totalArea));
            IO.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
            IO.println("Optimization: Conditional branch optimization");
            IO.println();
        }
        
        /**
         * メガモーフィック呼び出し（多くの型）
         */
        public static void megamorphicCalls() {
            IO.println("=== Megamorphic Calls (Many Types) ===");
            IO.println("Circle, Rectangle, Triangle - JVM optimization is limited");
            
            List<Shape> shapes = createMixedShapes(100);
            
            long startTime = System.nanoTime();
            double totalArea = 0;
            
            // 多くの型の呼び出し
            for (int i = 0; i < 10000; i++) {
                for (Shape shape : shapes) {
                    totalArea += shape.area(); // vtable経由、最適化困難
                }
            }
            
            long endTime = System.nanoTime();
            
            IO.println("Total area: " + String.format("%.2f", totalArea));
            IO.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
            IO.println("Optimization: vtable lookup, limited optimization");
            IO.println();
        }
        
        private static List<Shape> createMixedShapes(int count) {
            List<Shape> shapes = new ArrayList<>();
            Random random = ThreadLocalRandom.current();
            
            for (int i = 0; i < count; i++) {
                int type = random.nextInt(3);
                double x = random.nextDouble() * 10;
                double y = random.nextDouble() * 10;
                
                switch (type) {
                    case 0:
                        shapes.add(new Circle(x, y, 1 + random.nextDouble() * 5));
                        break;
                    case 1:
                        shapes.add(new Rectangle(x, y, 
                                                1 + random.nextDouble() * 5, 
                                                1 + random.nextDouble() * 5));
                        break;
                    case 2:
                        shapes.add(new Triangle(x, y, 
                                               1 + random.nextDouble() * 5, 
                                               1 + random.nextDouble() * 5));
                        break;
                }
            }
            
            return shapes;
        }
    }
    
    /**
     * final修飾子による最適化のデモンストレーション
     */
    public static class FinalOptimizationDemo {
        
        /**
         * オーバーライド可能なクラス
         */
        public static class Calculator {
            public int add(int a, int b) {
                return a + b;
            }
            
            public int multiply(int a, int b) {
                return a * b;
            }
        }
        
        /**
         * finalクラス（継承不可）
         */
        public static final class FastCalculator {
            public int add(int a, int b) {
                return a + b;
            }
            
            public int multiply(int a, int b) {
                return a * b;
            }
        }
        
        /**
         * finalメソッドを持つクラス
         */
        public static class PartialFinalCalculator {
            public final int add(int a, int b) {
                return a + b;
            }
            
            public int multiply(int a, int b) {
                return a * b;
            }
        }
        
        public static void demonstrateFinalOptimization() {
            IO.println("=== Final Modifier Optimization Demo ===");
            
            Calculator calc = new Calculator();
            FastCalculator fastCalc = new FastCalculator();
            PartialFinalCalculator partialCalc = new PartialFinalCalculator();
            
            int iterations = 1_000_000;
            
            // 通常のクラス（仮想メソッド呼び出し）
            long startTime = System.nanoTime();
            int sum1 = 0;
            for (int i = 0; i < iterations; i++) {
                sum1 += calc.add(i, 1);
            }
            long normalTime = System.nanoTime() - startTime;
            
            // finalクラス（非仮想メソッド呼び出し）
            startTime = System.nanoTime();
            int sum2 = 0;
            for (int i = 0; i < iterations; i++) {
                sum2 += fastCalc.add(i, 1);
            }
            long finalTime = System.nanoTime() - startTime;
            
            // finalメソッド
            startTime = System.nanoTime();
            int sum3 = 0;
            for (int i = 0; i < iterations; i++) {
                sum3 += partialCalc.add(i, 1);
            }
            long finalMethodTime = System.nanoTime() - startTime;
            
            IO.println("Results (all should be equal): " + sum1 + ", " + sum2 + ", " + sum3);
            IO.println("Normal class:      " + normalTime / 1_000_000 + " ms");
            IO.println("Final class:       " + finalTime / 1_000_000 + " ms");
            IO.println("Final method:      " + finalMethodTime / 1_000_000 + " ms");
            
            double speedup1 = (double) normalTime / finalTime;
            double speedup2 = (double) normalTime / finalMethodTime;
            
            IO.println("Final class speedup:   " + String.format("%.2fx", speedup1));
            IO.println("Final method speedup:  " + String.format("%.2fx", speedup2));
            IO.println();
        }
    }
    
    /**
     * インターフェイス呼び出しと実装クラス呼び出しの比較
     */
    public static class InterfaceVsClassDemo {
        
        interface Drawable {
            void draw();
            double getArea();
        }
        
        public static class DrawableCircle implements Drawable {
            private final double radius;
            
            public DrawableCircle(double radius) {
                this.radius = radius;
            }
            
            @Override
            public void draw() {
                // Drawing logic
            }
            
            @Override
            public double getArea() {
                return Math.PI * radius * radius;
            }
        }
        
        public static void demonstrateInterfaceOverhead() {
            IO.println("=== Interface vs Class Call Overhead ===");
            
            DrawableCircle circle = new DrawableCircle(5.0);
            Drawable drawable = circle;
            
            int iterations = 1_000_000;
            
            // 直接クラス呼び出し
            long startTime = System.nanoTime();
            double sum1 = 0;
            for (int i = 0; i < iterations; i++) {
                sum1 += circle.getArea();
            }
            long classTime = System.nanoTime() - startTime;
            
            // インターフェイス経由呼び出し
            startTime = System.nanoTime();
            double sum2 = 0;
            for (int i = 0; i < iterations; i++) {
                sum2 += drawable.getArea();
            }
            long interfaceTime = System.nanoTime() - startTime;
            
            IO.println("Results (should be equal): " + sum1 + ", " + sum2);
            IO.println("Class call:       " + classTime / 1_000_000 + " ms");
            IO.println("Interface call:   " + interfaceTime / 1_000_000 + " ms");
            
            double overhead = (double) interfaceTime / classTime;
            IO.println("Interface overhead: " + String.format("%.2fx", overhead));
            IO.println();
        }
    }
    
    /**
     * vtable構造の可視化
     */
    public static void demonstrateVTableStructure() {
        IO.println("=== Virtual Method Table Structure ===");
        
        // 各クラスのvtableを構築・表示
        VTableSimulator.VTable circleVTable = VTableSimulator.buildVTable(Circle.class);
        VTableSimulator.VTable rectangleVTable = VTableSimulator.buildVTable(Rectangle.class);
        VTableSimulator.VTable triangleVTable = VTableSimulator.buildVTable(Triangle.class);
        
        circleVTable.printVTable();
        rectangleVTable.printVTable();
        triangleVTable.printVTable();
    }
    
    /**
     * 動的ディスパッチのシミュレーション
     */
    public static void demonstrateDynamicDispatch() {
        IO.println("=== Dynamic Dispatch Simulation ===");
        
        Shape[] shapes = {
            new Circle(0, 0, 5),
            new Rectangle(1, 1, 4, 6),
            new Triangle(2, 2, 3, 4)
        };
        
        for (Shape shape : shapes) {
            DispatchSimulator.simulateVirtualCall(shape, "area");
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Virtual Method Table and Dynamic Dispatch Demonstration");
        IO.println("=======================================================");
        
        demonstrateVTableStructure();
        demonstrateDynamicDispatch();
        
        OptimizationDemo.monomorphicCalls();
        OptimizationDemo.bimorphicCalls();
        OptimizationDemo.megamorphicCalls();
        
        FinalOptimizationDemo.demonstrateFinalOptimization();
        InterfaceVsClassDemo.demonstrateInterfaceOverhead();
        
        IO.println("=== Performance Optimization Guidelines ===");
        IO.println("1. Use 'final' for classes and methods when inheritance is not needed");
        IO.println("2. Keep method implementations small to enable inlining");
        IO.println("3. Minimize polymorphic call sites in hot paths");
        IO.println("4. Consider type stability in performance-critical loops");
        IO.println("5. Profile before optimizing - JVM is very good at optimization");
        
        IO.println("\nKey Takeaways:");
        IO.println("✓ JVM uses vtables for dynamic method dispatch");
        IO.println("✓ Monomorphic calls are fastest (direct/inlined)");
        IO.println("✓ Megamorphic calls require vtable lookups");
        IO.println("✓ 'final' enables significant optimizations");
        IO.println("✓ Interface calls have slight overhead vs class calls");
    }
}