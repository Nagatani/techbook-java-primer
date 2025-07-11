# 第2章 チャレンジ課題

## 概要
第2章で学んだすべての概念を統合し、実用的で複雑なアプリケーションの開発に挑戦します。アルゴリズムの効率性、設計の優雅さ、コードの保守性を追求します。

## 学習目標
- 大規模なプログラムの設計と実装ができる
- 効率的なデータ構造とアルゴリズムを選択できる
- ユーザビリティを考慮したインターフェースを設計できる
- 拡張可能で保守しやすいコードを書ける

## 課題一覧

### チャレンジ課題1: テキストエディタの実装
**ファイル名**: `TextBuffer.java`、`Command.java`、`SimpleTextEditor.java`

#### 要求仕様
1. 基本機能：
   - テキストの入力・表示
   - カーソル移動（上下左右）
   - 文字の挿入・削除
   - 行の挿入・削除
2. 編集機能：
   - コピー・カット・ペースト
   - 検索・置換
   - Undo/Redo（最低5回分）
3. ファイル操作：
   - ファイルの読み込み（シミュレート）
   - ファイルの保存（シミュレート）
4. 表示機能：
   - 行番号表示
   - 現在のカーソル位置表示
   - ステータスバー

#### 実装のヒント
- テキストは文字列の配列で管理（各要素が1行）
- コマンドパターンでUndo/Redoを実装
- カーソル位置は行と列で管理

#### 実装例の骨組み
```java
// TextBuffer.java
public class TextBuffer {
    private String[] lines;
    private int lineCount;
    private int cursorRow;
    private int cursorCol;
    
    public void insertChar(char c) {
        // カーソル位置に文字を挿入
    }
    
    public void deletChar() {
        // カーソル位置の文字を削除
    }
}

// Command.java
public abstract class Command {
    protected TextBuffer buffer;
    
    public abstract void execute();
    public abstract void undo();
}

// SimpleTextEditor.java
public class SimpleTextEditor {
    private TextBuffer buffer;
    private Command[] history;
    private int historyIndex;
    
    public static void main(String[] args) {
        // エディタのメインループ
    }
}
```

#### 評価ポイント
- 機能の完成度と安定性
- コマンドパターンの適切な実装
- ユーザーインターフェースの使いやすさ
- エッジケースの処理

#### 発展学習
- シンタックスハイライト
- マルチバッファ対応
- マクロ記録・再生機能

---

### チャレンジ課題2: ゲーム「数独ソルバー」
**ファイル名**: `SudokuBoard.java`、`SudokuSolver.java`、`SudokuGame.java`

#### 要求仕様
1. 数独パズルの実装：
   - 9×9のグリッド表示
   - 初期値の設定
   - ユーザー入力の受付
   - 入力値の妥当性チェック
2. ソルバー機能：
   - バックトラッキングアルゴリズムで解を探索
   - 解の一意性チェック
   - ヒント機能（次の一手を提示）
3. ゲーム機能：
   - 難易度選択（初級/中級/上級）
   - タイマー機能
   - 完成チェック
   - スコア計算
4. 統計機能：
   - プレイ履歴の記録
   - ベストタイムの記録
   - 正解率の表示

#### 実装のヒント
- 2次元配列でボードを表現
- 再帰的なバックトラッキング
- 各セルの候補値を管理

#### 実装例の骨組み
```java
// SudokuBoard.java
public class SudokuBoard {
    private int[][] grid;
    private boolean[][] isFixed;
    
    public boolean isValidMove(int row, int col, int num) {
        // 行・列・3×3ブロックのチェック
    }
    
    public boolean isComplete() {
        // 完成チェック
    }
}

// SudokuSolver.java
public class SudokuSolver {
    public boolean solve(SudokuBoard board) {
        // バックトラッキングアルゴリズム
    }
    
    public int countSolutions(SudokuBoard board) {
        // 解の数を数える
    }
}

// SudokuGame.java
public class SudokuGame {
    private SudokuBoard board;
    private SudokuSolver solver;
    private long startTime;
    private int hintsUsed;
    
    public void play() {
        // ゲームのメインループ
    }
}
```

#### 評価ポイント
- アルゴリズムの効率性
- ゲームとしての完成度
- ユーザー体験の質
- コードの構造化

#### 発展学習
- パズル生成アルゴリズム
- 解法の可視化
- AI対戦モード

## 提出方法
1. 選択した課題のすべてのファイルを実装
2. 詳細な設計ドキュメントを作成
3. 使用方法とサンプル実行例を含むREADME
4. テストケースと実行結果を文書化

## 評価の観点
- 要求仕様の完全な実装
- アルゴリズムの効率性と正確性
- コードの可読性と保守性
- エラー処理の適切性
- ユーザビリティの高さ
- 創造的な追加機能

## 注意事項
- 第2章までの知識で実装可能な範囲で設計
- コメントを充実させ、設計思想を明確に
- パフォーマンスを意識した実装
- テスト駆動開発の考え方を取り入れる