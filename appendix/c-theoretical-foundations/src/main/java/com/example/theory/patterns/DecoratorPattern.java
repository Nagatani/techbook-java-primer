package com.example.theory.patterns;

/**
 * Decoratorパターンの数学的モデル
 * リストAC-20
 */
public class DecoratorPattern {
    
    /**
     * Component = 基本機能の抽象化
     */
    public interface Coffee {
        double getCost();
        String getDescription();
    }
    
    /**
     * ConcreteComponent = 基本実装
     */
    public static class SimpleCoffee implements Coffee {
        @Override
        public double getCost() { 
            return 1.0; 
        }
        
        @Override
        public String getDescription() { 
            return "Simple Coffee"; 
        }
    }
    
    /**
     * Decorator = 装飾の抽象化
     */
    public static abstract class CoffeeDecorator implements Coffee {
        protected Coffee coffee;
        
        public CoffeeDecorator(Coffee coffee) {
            this.coffee = coffee;
        }
    }
    
    /**
     * ConcreteDecorator = 具体的な装飾（ミルク）
     */
    public static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) { 
            super(coffee); 
        }
        
        @Override
        public double getCost() {
            return coffee.getCost() + 0.5; // f(x) = x + 0.5
        }
        
        @Override
        public String getDescription() {
            return coffee.getDescription() + ", Milk";
        }
    }
    
    /**
     * ConcreteDecorator = 具体的な装飾（砂糖）
     */
    public static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) { 
            super(coffee); 
        }
        
        @Override
        public double getCost() {
            return coffee.getCost() + 0.3; // g(x) = x + 0.3
        }
        
        @Override
        public String getDescription() {
            return coffee.getDescription() + ", Sugar";
        }
    }
    
    /**
     * ConcreteDecorator = 具体的な装飾（ホイップクリーム）
     */
    public static class WhipDecorator extends CoffeeDecorator {
        public WhipDecorator(Coffee coffee) { 
            super(coffee); 
        }
        
        @Override
        public double getCost() {
            return coffee.getCost() + 0.7; // h(x) = x + 0.7
        }
        
        @Override
        public String getDescription() {
            return coffee.getDescription() + ", Whip";
        }
    }
    
    /**
     * 使用例のデモンストレーション
     */
    public static void demonstrateDecoratorPattern() {
        // 基本のコーヒー
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " : $" + coffee.getCost());
        
        // ミルク入りコーヒー
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " : $" + coffee.getCost());
        
        // ミルクと砂糖入りコーヒー
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " : $" + coffee.getCost());
        
        // 関数合成の例: h(g(f(x)))
        Coffee complexCoffee = new WhipDecorator(
            new MilkDecorator(
                new SugarDecorator(
                    new SimpleCoffee()
                )
            )
        );
        System.out.println("\nComplex coffee (function composition):");
        System.out.println(complexCoffee.getDescription() + " : $" + complexCoffee.getCost());
        
        // 数学的に: cost = 1.0 + 0.3 + 0.5 + 0.7 = 2.5
    }
}