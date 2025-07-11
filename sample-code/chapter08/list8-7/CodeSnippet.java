/**
 * リスト8-7
 * Status列挙型
 * 
 * 元ファイル: chapter08-enums.md (271行目)
 */

interface Loggable {
    String getLogMessage();
}

public enum Status implements Loggable {
    SUCCESS {
        public String getLogMessage() { return "処理が成功しました。"; }
    },
    ERROR {
        public String getLogMessage() { return "エラーが発生しました。"; }
    };
}