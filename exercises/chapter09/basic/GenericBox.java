package chapter09.basic;

/**
 * ジェネリックなBoxクラス
 * 
 * 任意の型のオブジェクトを格納できる汎用的なコンテナクラスです。
 * 
 * @param <T> 格納するオブジェクトの型
 */
public class GenericBox<T> {
    // 格納するオブジェクト
    private T content;
    
    /**
     * デフォルトコンストラクタ
     */
    public GenericBox() {
        // TODO: contentをnullで初期化
        
    }
    
    /**
     * 初期値を指定するコンストラクタ
     * @param content 格納するオブジェクト
     */
    public GenericBox(T content) {
        // TODO: this.contentを初期化
        
    }
    
    /**
     * オブジェクトを格納する
     * TODO: contentフィールドに値を設定
     * @param content 格納するオブジェクト
     */
    public void put(T content) {
        // TODO: 実装してください
        
    }
    
    /**
     * 格納されているオブジェクトを取得する
     * TODO: contentフィールドの値を返す
     * @return 格納されているオブジェクト
     */
    public T get() {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * Boxが空かどうかを判定する
     * TODO: contentがnullかどうかをチェック
     * @return 空の場合true
     */
    public boolean isEmpty() {
        // TODO: 実装してください
        return true;
    }
    
    /**
     * Boxを空にする
     * TODO: contentをnullに設定
     */
    public void clear() {
        // TODO: 実装してください
        
    }
    
    /**
     * 格納されているオブジェクトの情報を表示する
     * TODO: contentの型名と値を表示（nullの場合は"空"と表示）
     */
    public void displayInfo() {
        // TODO: 実装してください
        // ヒント: getClass().getSimpleName()で型名を取得できます
        System.out.println("GenericBox情報:");
    }
    
    /**
     * 文字列表現を返す
     */
    @Override
    public String toString() {
        return "GenericBox{content=" + content + "}";
    }
}