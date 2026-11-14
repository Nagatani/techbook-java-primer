package com.example.vtable;

import java.util.*;

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
            
            // メソッド名からインデックスを探す（見つからなければ-1）
            public int indexOf(String methodName) {
                for (VTableEntry entry : entries) {
                    if (entry.methodName.equals(methodName)) {
                        return entry.index;
                    }
                }
                return -1;
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
            
            // 3. メソッドのインデックスを取得
            //    （実際のJVMでは、呼び出し箇所を初めて実行するときに一度だけ決まる。
            //     オーバーライドしたメソッドは親クラスと同じインデックスを使う）
            int methodIndex = vtable.indexOf(methodName);
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
     * 時間の比を文字列にする（0で割らないようにする）
     */
    static String ratio(long a, long b) {
        if (a <= 0 || b <= 0) {
            return "n/a (too fast to measure)";
        }
        return "%.2fx".formatted((double) a / b);
    }
    
    // 測定結果を捨てずに使うための変数（JITが計算ごと消さないようにする）
    static double blackhole = 0;
    
    /**
     * 呼び出し箇所に現れる型の数による違いのデモンストレーション
     * 測定値は環境で大きく変わる。正確に測るにはJMHを使う
     */
    public static class OptimizationDemo {
        private static final int SHAPES = 120;
        private static final int REPEAT = 10_000;
        
        // 3つのメソッドはそれぞれ別の呼び出し箇所を持つ。
        // JITは呼び出し箇所ごとに、現れた型を記録（プロファイル）する
        
        // 1種類の型だけが現れる呼び出し箇所（モノモーフィック）
        private static double sumMonomorphic(List<Shape> shapes) {
            double total = 0;
            for (int i = 0; i < REPEAT; i++) {
                for (Shape shape : shapes) {
                    total += shape.area();
                }
            }
            return total;
        }
        
        // 2種類の型が現れる呼び出し箇所（バイモーフィック）
        private static double sumBimorphic(List<Shape> shapes) {
            double total = 0;
            for (int i = 0; i < REPEAT; i++) {
                for (Shape shape : shapes) {
                    total += shape.area();
                }
            }
            return total;
        }
        
        // 3種類以上の型が現れる呼び出し箇所（メガモーフィック）
        private static double sumMegamorphic(List<Shape> shapes) {
            double total = 0;
            for (int i = 0; i < REPEAT; i++) {
                for (Shape shape : shapes) {
                    total += shape.area();
                }
            }
            return total;
        }
        
        public static void compareCallSites() {
            IO.println("=== Monomorphic / Bimorphic / Megamorphic Call Sites ===");
            IO.println("Each call site calls area() " + (SHAPES * REPEAT) + " times");
            
            List<Shape> mono = createShapes(1);
            List<Shape> bi = createShapes(2);
            List<Shape> mega = createShapes(3);
            
            // ウォームアップ（各呼び出し箇所をJITコンパイルさせる）
            for (int round = 0; round < 5; round++) {
                blackhole += sumMonomorphic(mono);
                blackhole += sumBimorphic(bi);
                blackhole += sumMegamorphic(mega);
            }
            
            long monoTime = measure(() -> sumMonomorphic(mono));
            long biTime = measure(() -> sumBimorphic(bi));
            long megaTime = measure(() -> sumMegamorphic(mega));
            
            IO.println("Monomorphic (Circle only):            %d μs".formatted(monoTime / 1000));
            IO.println("Bimorphic (Circle, Rectangle):        %d μs".formatted(biTime / 1000));
            IO.println("Megamorphic (Circle, Rectangle, Triangle): %d μs".formatted(megaTime / 1000));
            IO.println("Megamorphic / monomorphic: " + ratio(megaTime, monoTime));
            IO.println("Monomorphic and bimorphic call sites can be inlined with a type check;");
            IO.println("megamorphic call sites usually go through the vtable.");
            IO.println();
        }
        
        private static long measure(java.util.function.DoubleSupplier task) {
            long start = System.nanoTime();
            for (int i = 0; i < 5; i++) {
                blackhole += task.getAsDouble();
            }
            return (System.nanoTime() - start) / 5;
        }
        
        // 指定した種類数の図形を、同じ数ずつ交互に並べる
        private static List<Shape> createShapes(int kinds) {
            List<Shape> shapes = new ArrayList<>();
            for (int i = 0; i < SHAPES; i++) {
                double size = 1 + i % 5;
                switch (i % kinds) {
                    case 0 -> shapes.add(new Circle(0, 0, size));
                    case 1 -> shapes.add(new Rectangle(0, 0, size, size + 1));
                    default -> shapes.add(new Triangle(0, 0, size, size + 2));
                }
            }
            return shapes;
        }
    }
    
    /**
     * finalの有無による違いのデモンストレーション
     * HotSpotのJITはクラス階層解析（CHA）によって、オーバーライドされていない
     * メソッドならfinalがなくてもインライン化する。そのため、時間はふつう同程度になる
     */
    public static class FinalModifierDemo {
        
        /**
         * finalのないクラス（ただし、オーバーライドするサブクラスはない）
         */
        public static class Calculator {
            public int add(int a, int b) {
                return a + b;
            }
        }
        
        /**
         * finalクラス（継承不可）
         */
        public static final class FinalCalculator {
            public int add(int a, int b) {
                return a + b;
            }
        }
        
        private static int sumWithCalculator(Calculator calc, int n) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += calc.add(i, 1);
            }
            return sum;
        }
        
        private static int sumWithFinalCalculator(FinalCalculator calc, int n) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += calc.add(i, 1);
            }
            return sum;
        }
        
        public static void demonstrateFinalModifier() {
            IO.println("=== Final Modifier and Inlining ===");
            
            Calculator calc = new Calculator();
            FinalCalculator finalCalc = new FinalCalculator();
            int n = 1_000_000;
            
            // ウォームアップ
            for (int round = 0; round < 10; round++) {
                blackhole += sumWithCalculator(calc, n);
                blackhole += sumWithFinalCalculator(finalCalc, n);
            }
            
            long start = System.nanoTime();
            int sum1 = sumWithCalculator(calc, n);
            long normalTime = System.nanoTime() - start;
            
            start = System.nanoTime();
            int sum2 = sumWithFinalCalculator(finalCalc, n);
            long finalTime = System.nanoTime() - start;
            
            IO.println("Results (should be equal): " + sum1 + ", " + sum2);
            IO.println("Non-final class: %d μs".formatted(normalTime / 1000));
            IO.println("Final class:     %d μs".formatted(finalTime / 1000));
            IO.println("Non-final / final: " + ratio(normalTime, finalTime));
            IO.println("With class hierarchy analysis (CHA), the JIT inlines");
            IO.println("non-final methods that are not overridden.");
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
        
        private static double sumViaClass(DrawableCircle circle, int n) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += circle.getArea();      // invokevirtual
            }
            return sum;
        }
        
        private static double sumViaInterface(Drawable drawable, int n) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += drawable.getArea();    // invokeinterface
            }
            return sum;
        }
        
        public static void demonstrateInterfaceOverhead() {
            IO.println("=== Interface vs Class Call ===");
            
            DrawableCircle circle = new DrawableCircle(5.0);
            Drawable drawable = circle;
            int n = 1_000_000;
            
            // ウォームアップ
            for (int round = 0; round < 10; round++) {
                blackhole += sumViaClass(circle, n);
                blackhole += sumViaInterface(drawable, n);
            }
            
            long start = System.nanoTime();
            double sum1 = sumViaClass(circle, n);
            long classTime = System.nanoTime() - start;
            
            start = System.nanoTime();
            double sum2 = sumViaInterface(drawable, n);
            long interfaceTime = System.nanoTime() - start;
            
            IO.println("Results (should be equal): " + (sum1 == sum2));
            IO.println("Class call:     %d μs".formatted(classTime / 1000));
            IO.println("Interface call: %d μs".formatted(interfaceTime / 1000));
            IO.println("Interface / class: " + ratio(interfaceTime, classTime));
            IO.println("When only one implementation appears at the call site,");
            IO.println("interface calls are inlined just like class calls.");
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
        
        OptimizationDemo.compareCallSites();
        FinalModifierDemo.demonstrateFinalModifier();
        InterfaceVsClassDemo.demonstrateInterfaceOverhead();
        
        IO.println("Note: Timings vary greatly by environment. Use JMH for accurate measurements.");
        
        IO.println("\nKey Takeaways:");
        IO.println("✓ JVM uses vtables (and itables for interfaces) for dynamic dispatch");
        IO.println("✓ The JIT records the receiver types seen at each call site");
        IO.println("✓ Monomorphic and bimorphic call sites can be inlined with a type check");
        IO.println("✓ Megamorphic call sites usually go through the vtable or itable");
        IO.println("✓ CHA lets the JIT inline non-final methods that are not overridden");
        IO.println("✓ Use 'final' to express design intent, not for speed");
    }
}