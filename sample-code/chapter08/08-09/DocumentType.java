public enum DocumentType implements Printable {
    PDF {
        @Override
        public void print() {
            System.out.println("PDFドキュメントを印刷します");
        }
    },
    WORD {
        @Override
        public void print() {
            System.out.println("Wordドキュメントを印刷します");
        }
    },
    TEXT {
        @Override
        public void print() {
            System.out.println("テキストファイルを印刷します");
        }
    }
}
