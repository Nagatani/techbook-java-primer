/**
 * リストAC-10
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (269行目)
 */

class Rectangle {
    protected int width, height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = this.height = width; // 前提条件を変更
    }
    
    @Override
    public void setHeight(int height) {
        this.width = this.height = height; // 前提条件を変更
    }
}

// LSP違反を示すテスト
public void testLSPViolation() {
    Rectangle rect = new Square(); // サブタイプで置換
    rect.setWidth(5);
    rect.setHeight(4);
    
    // Rectangle では 20 を期待するが、Square では 16 になる
    assert rect.getArea() == 20; // 失敗！
}