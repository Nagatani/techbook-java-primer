public enum DocumentType implements Printable {
    PDF {
        @Override
        public void print() {
            IO.println("PDFドキュメントを印刷します");
        }
    },
    WORD {
        @Override
        public void print() {
            IO.println("Wordドキュメントを印刷します");
        }
    },
    TEXT {
        @Override
        public void print() {
            IO.println("テキストファイルを印刷します");
        }
    }
}
