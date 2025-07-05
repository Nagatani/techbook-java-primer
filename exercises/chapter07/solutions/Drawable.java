/**
 * 描画可能なオブジェクトを表すインターフェイス
 * 
 * 【学習ポイント】
 * 1. インターフェイスの定義と活用
 * 2. デフォルトメソッドの実装
 * 3. 静的メソッドの定義
 * 4. 関数型インターフェイスの設計
 * 5. 複数インターフェイスの実装
 * 
 * 【よくある間違い】
 * - インターフェイスで状態を持とうとする
 * - デフォルトメソッドを過度に使用する
 * - 抽象クラスとインターフェイスの使い分けができない
 * - インターフェイス分離の原則を無視する
 */
public interface Drawable {
    
    // === 抽象メソッド ===
    
    /**
     * 描画処理を実行
     * 実装クラスで具体的な描画ロジックを定義
     */
    void draw();
    
    /**
     * 描画位置を取得
     * @return 描画位置の座標
     */
    Position getPosition();
    
    /**
     * 描画サイズを取得
     * @return 描画サイズ
     */
    Size getSize();
    
    /**
     * 表示・非表示の状態を取得
     * @return 表示状態の場合true
     */
    boolean isVisible();
    
    // === デフォルトメソッド ===
    
    /**
     * 描画領域を取得（デフォルト実装）
     */
    default Rectangle getBounds() {
        Position pos = getPosition();
        Size size = getSize();
        return new Rectangle(pos.getX(), pos.getY(), size.getWidth(), size.getHeight());
    }
    
    /**
     * 指定した点が描画領域内にあるかを判定（デフォルト実装）
     */
    default boolean contains(double x, double y) {
        if (!isVisible()) {
            return false;
        }
        Rectangle bounds = getBounds();
        return bounds.contains(x, y);
    }
    
    /**
     * 他の描画可能オブジェクトと重なるかを判定（デフォルト実装）
     */
    default boolean overlaps(Drawable other) {
        if (!this.isVisible() || !other.isVisible()) {
            return false;
        }
        Rectangle thisBounds = this.getBounds();
        Rectangle otherBounds = other.getBounds();
        return thisBounds.intersects(otherBounds);
    }
    
    /**
     * 描画情報を文字列で取得（デフォルト実装）
     */
    default String getDrawInfo() {
        Position pos = getPosition();
        Size size = getSize();
        return String.format("Position: (%.1f, %.1f), Size: %.1fx%.1f, Visible: %s",
                           pos.getX(), pos.getY(), size.getWidth(), size.getHeight(), isVisible());
    }
    
    /**
     * 中心座標を取得（デフォルト実装）
     */
    default Position getCenter() {
        Position pos = getPosition();
        Size size = getSize();
        return new Position(pos.getX() + size.getWidth() / 2, pos.getY() + size.getHeight() / 2);
    }
    
    /**
     * 描画の前処理（デフォルト実装 - 何もしない）
     */
    default void beforeDraw() {
        // デフォルトでは何もしない
        // 必要に応じて実装クラスでオーバーライド
    }
    
    /**
     * 描画の後処理（デフォルト実装 - 何もしない）
     */
    default void afterDraw() {
        // デフォルトでは何もしない
        // 必要に応じて実装クラスでオーバーライド
    }
    
    /**
     * 完全な描画処理（テンプレートメソッド的な実装）
     */
    default void render() {
        if (!isVisible()) {
            return;  // 非表示の場合は描画しない
        }
        
        beforeDraw();
        draw();
        afterDraw();
    }
    
    // === 静的メソッド ===
    
    /**
     * 複数の描画可能オブジェクトを一括描画
     */
    static void drawAll(Drawable... drawables) {
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.render();
            }
        }
    }
    
    /**
     * 描画可能オブジェクトの配列から表示されているもののみを抽出
     */
    static Drawable[] getVisibleObjects(Drawable[] drawables) {
        return java.util.Arrays.stream(drawables)
                               .filter(Drawable::isVisible)
                               .toArray(Drawable[]::new);
    }
    
    /**
     * 描画可能オブジェクトの配列をZ軸順（描画順）でソート
     * Z軸値を持つオブジェクトには ZOrderable インターフェイスを実装させる
     */
    static void sortByZOrder(Drawable[] drawables) {
        java.util.Arrays.sort(drawables, (a, b) -> {
            if (a instanceof ZOrderable && b instanceof ZOrderable) {
                return Integer.compare(((ZOrderable) a).getZOrder(), ((ZOrderable) b).getZOrder());
            }
            return 0;  // Z軸値がない場合は順序を変更しない
        });
    }
    
    /**
     * 指定した座標にある描画可能オブジェクトを取得
     */
    static Drawable findAt(double x, double y, Drawable[] drawables) {
        // 後ろから検索（上位レイヤーから）
        for (int i = drawables.length - 1; i >= 0; i--) {
            if (drawables[i] != null && drawables[i].contains(x, y)) {
                return drawables[i];
            }
        }
        return null;
    }
    
    // === 関連インターフェイス ===
    
    /**
     * 移動可能な描画オブジェクト
     */
    interface Movable extends Drawable {
        /**
         * オブジェクトを移動
         */
        void moveTo(double x, double y);
        
        /**
         * 相対移動
         */
        default void moveBy(double dx, double dy) {
            Position pos = getPosition();
            moveTo(pos.getX() + dx, pos.getY() + dy);
        }
    }
    
    /**
     * サイズ変更可能な描画オブジェクト
     */
    interface Resizable extends Drawable {
        /**
         * サイズを変更
         */
        void resize(double width, double height);
        
        /**
         * スケールを変更
         */
        default void scale(double factor) {
            Size size = getSize();
            resize(size.getWidth() * factor, size.getHeight() * factor);
        }
    }
    
    /**
     * Z軸順序を持つ描画オブジェクト
     */
    interface ZOrderable extends Drawable {
        /**
         * Z軸値を取得（大きいほど手前）
         */
        int getZOrder();
        
        /**
         * Z軸値を設定
         */
        void setZOrder(int zOrder);
    }
    
    /**
     * 回転可能な描画オブジェクト
     */
    interface Rotatable extends Drawable {
        /**
         * 回転角度を取得（度数）
         */
        double getRotation();
        
        /**
         * 回転角度を設定
         */
        void setRotation(double degrees);
        
        /**
         * 相対回転
         */
        default void rotate(double degrees) {
            setRotation(getRotation() + degrees);
        }
    }
    
    /**
     * アルファ値（透明度）を持つ描画オブジェクト
     */
    interface Transparent extends Drawable {
        /**
         * アルファ値を取得（0.0 = 完全透明, 1.0 = 完全不透明）
         */
        double getAlpha();
        
        /**
         * アルファ値を設定
         */
        void setAlpha(double alpha);
        
        /**
         * 透明かどうかを判定
         */
        default boolean isTransparent() {
            return getAlpha() < 1.0;
        }
        
        /**
         * 完全に透明かどうかを判定
         */
        default boolean isCompletelyTransparent() {
            return getAlpha() <= 0.0;
        }
    }
    
    // === 内部クラス（ユーティリティ） ===
    
    /**
     * 位置を表すクラス
     */
    class Position {
        private final double x, y;
        
        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
        
        public Position add(double dx, double dy) {
            return new Position(x + dx, y + dy);
        }
        
        public double distanceTo(Position other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        @Override
        public String toString() {
            return String.format("Position(%.1f, %.1f)", x, y);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Position position = (Position) obj;
            return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }
    
    /**
     * サイズを表すクラス
     */
    class Size {
        private final double width, height;
        
        public Size(double width, double height) {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("サイズは負の値にできません");
            }
            this.width = width;
            this.height = height;
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        
        public double getArea() {
            return width * height;
        }
        
        public Size scale(double factor) {
            return new Size(width * factor, height * factor);
        }
        
        @Override
        public String toString() {
            return String.format("Size(%.1fx%.1f)", width, height);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Size size = (Size) obj;
            return Double.compare(size.width, width) == 0 && Double.compare(size.height, height) == 0;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(width, height);
        }
    }
    
    /**
     * 矩形を表すクラス
     */
    class Rectangle {
        private final double x, y, width, height;
        
        public Rectangle(double x, double y, double width, double height) {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("サイズは負の値にできません");
            }
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        
        public boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
        
        public boolean intersects(Rectangle other) {
            return !(this.x + this.width < other.x ||
                     other.x + other.width < this.x ||
                     this.y + this.height < other.y ||
                     other.y + other.height < this.y);
        }
        
        public Position getCenter() {
            return new Position(x + width / 2, y + height / 2);
        }
        
        @Override
        public String toString() {
            return String.format("Rectangle(%.1f, %.1f, %.1fx%.1f)", x, y, width, height);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Rectangle rectangle = (Rectangle) obj;
            return Double.compare(rectangle.x, x) == 0 &&
                   Double.compare(rectangle.y, y) == 0 &&
                   Double.compare(rectangle.width, width) == 0 &&
                   Double.compare(rectangle.height, height) == 0;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y, width, height);
        }
    }
    
    // === ファクトリーメソッド ===
    
    /**
     * シンプルな描画可能オブジェクトを作成
     */
    static Drawable createSimple(String name, double x, double y, double width, double height) {
        return new Drawable() {
            private final Position position = new Position(x, y);
            private final Size size = new Size(width, height);
            private boolean visible = true;
            
            @Override
            public void draw() {
                System.out.println("Drawing " + name + " at " + position + " with size " + size);
            }
            
            @Override
            public Position getPosition() {
                return position;
            }
            
            @Override
            public Size getSize() {
                return size;
            }
            
            @Override
            public boolean isVisible() {
                return visible;
            }
            
            public void setVisible(boolean visible) {
                this.visible = visible;
            }
            
            @Override
            public String toString() {
                return String.format("SimpleDrawable{name='%s', %s}", name, getDrawInfo());
            }
        };
    }
}