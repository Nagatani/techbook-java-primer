/**
 * リストAC-14
 * DataProcessorインターフェース
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (382行目)
 */

// 悪い例：制御フラグを渡す
public void processData(Data data, int mode) {
    if (mode == 1) {
        // 処理A
    } else if (mode == 2) {
        // 処理B
    }
}

// 良い例：ポリモーフィズムを使用
public interface DataProcessor {
    void process(Data data);
}