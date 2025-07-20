package com.example.theory.structures;

/**
 * リスコフの置換原則（LSP）の例
 * リストAC-10, AC-11を含む
 */
public class LiskovSubstitution {
    
    /**
     * LSP違反の例
     * リストAC-10
     */
    public static class Rectangle {
        protected int width, height;
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public int getArea() {
            return width * height;
        }
    }
    
    public static class Square extends Rectangle {
        @Override
        public void setWidth(int width) {
            this.width = this.height = width; // 前提条件を変更
        }
        
        @Override
        public void setHeight(int height) {
            this.width = this.height = height; // 前提条件を変更
        }
    }
    
    /**
     * LSP遵守の設計
     * リストAC-11
     */
    public interface Shape {
        int getArea();
        Shape resize(double factor);
    }
    
    public static class ImmutableRectangle implements Shape {
        private final int width, height;
        
        public ImmutableRectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public int getArea() {
            return width * height;
        }
        
        @Override
        public Shape resize(double factor) {
            return new ImmutableRectangle(
                (int)(width * factor), 
                (int)(height * factor)
            );
        }
    }
    
    public static class ImmutableSquare implements Shape {
        private final int side;
        
        public ImmutableSquare(int side) {
            this.side = side;
        }
        
        @Override
        public int getArea() {
            return side * side;
        }
        
        @Override
        public Shape resize(double factor) {
            return new ImmutableSquare((int)(side * factor));
        }
    }
    
    /**
     * LSP違反を示すテスト
     */
    public static void testLSPViolation() {
        Rectangle rect = new Square(); // サブタイプで置換
        rect.setWidth(5);
        rect.setHeight(4);
        
        // Rectangle では 20 を期待するが、Square では 16 になる
        assert rect.getArea() == 20 : "LSP violation: Expected 20 but got " + rect.getArea();
    }
    
    /**
     * LSP遵守を示すテスト
     */
    public static void testLSPCompliance() {
        Shape rect = new ImmutableRectangle(5, 4);
        Shape square = new ImmutableSquare(4);
        
        // どちらも期待通りの動作をする
        assert rect.getArea() == 20 : "Rectangle area should be 20";
        assert square.getArea() == 16 : "Square area should be 16";
        
        // resizeも期待通り動作
        Shape resizedRect = rect.resize(2.0);
        Shape resizedSquare = square.resize(2.0);
        
        assert resizedRect.getArea() == 80 : "Resized rectangle area should be 80";
        assert resizedSquare.getArea() == 64 : "Resized square area should be 64";
    }
}