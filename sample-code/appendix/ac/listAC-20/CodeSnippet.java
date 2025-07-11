/**
 * リストAC-20
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (522行目)
 */

// Component = 基本機能の抽象化
interface Coffee {
    double getCost();
    String getDescription();
}

// ConcreteComponent = 基本実装
class SimpleCoffee implements Coffee {
    public double getCost() { return 1.0; }
    public String getDescription() { return "Simple Coffee"; }
}

// Decorator = 装飾の抽象化
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// ConcreteDecorator = 具体的な装飾
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    
    public double getCost() {
        return coffee.getCost() + 0.5; // f(x) = x + 0.5
    }
    
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
}

// 使用例: 関数合成 g(f(x))
Coffee coffee = new MilkDecorator(
    new SugarDecorator(
        new SimpleCoffee()
    )
);