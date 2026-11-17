# 演習の発展課題・チャレンジ課題の見直し手順（2026-11-12〜）

TODO.mdの「演習の内容を見直す」のうち、`exercises/chapterXX/`の`advanced/`と`challenge/`を作り直すための手順です。
章ごとにサブエージェントへ任せるときにも、この文書を渡します。

## 背景

- `exercises/chapterXX/`には`basic/`、`advanced/`、`challenge/`、`solutions/`がある（第8章は`enums/`と`records/`、第22章は`documentation/`と`api-design/`に分かれる）
- `basic/`と`solutions/`はおおむね整っている。第6章以降の`advanced/`と`challenge/`の多くは、架空の題材（「量子シミュレーター」「意識を持つ…」「時空間…」「AI駆動…」「宇宙規模…」）か、旧章番号のころの別の章の内容のままで、コードもない
- 手本は第5章のチャレンジ課題（`exercises/chapter05/challenge/`）。READMEと、同じフォルダーに置いた動く解答例のコードからなる

## 章ごとの作業

1. `docs/summaries/chapterXX.md`（必要なら`docs/book-index/`の行範囲で本文）を読み、その章とそれまでの章で何を学ぶかをつかむ
2. `exercises/chapterXX/README.md`と、`basic/`・`advanced/`・`challenge/`のREADMEを読む
3. 作り直す章（REWRITE）
    - `advanced/`は現実的な課題を1〜2題、`challenge/`はその章の内容をまとめて使う少し大きめの課題を1題にする
    - 題材は大学生にわかる身近なもの（店、図書館、成績、ToDoリスト、時間割、在庫、銀行口座、テキストファイル、簡単なゲームなど）。架空の技術や流行語（分散、AI、量子、エンタープライズ）は使わない
    - その章までに学ぶ内容で解ける課題にする（CLAUDE.mdの方針12）。後の章の技術を要求しない。その章の主題を中心にし、基礎課題と重ねない
    - READMEの構成は第5章のチャレンジ課題にそろえる。学習目標 / 課題一覧（`### 課題N: 名前`、**ファイル**（このフォルダーのファイルは解答例です）、ファイルの木、説明、**要求仕様**、**実行例**）/ コンパイルと実行 / 実装のヒント / 応用のポイント / 発展（任意）/ 完了チェックリスト / **次のステップ**。リストはである調で句点なし、地の文はですます調
    - 解答例のコードを同じフォルダーに置く。1つのフォルダーに複数の課題を置くときは、クラス名がぶつからないよう課題ごとにサブフォルダー（`advanced/task1-xxx/`）に分けるか、名前を変える
    - 実行例は解答例を実際に動かした出力にする。出力は毎回同じになるようにする（現在時刻、シードのない乱数、並び順の定まらないファイル一覧を出さない）
    - 架空の内容は残さない。`exercises/chapterXX/README.md`の発展課題・チャレンジ課題の一覧も直し、第5章と同じく「このフォルダーのJavaファイルは解答例です」と添える
4. 見直す章（REVIEW）
    - 題材は現実的なので、章の範囲（方針12）に収まっているかを確かめ、誤っている点だけを直す（範囲外の要求、架空の部分、章との食い違い）
    - READMEが存在しないファイルを挙げているときは、解答例のコードを加えるか、ファイルを約束しない書き方にする
5. 原稿の章末演習（`manuscripts/chapterXX-*.md`の末尾）が発展課題・チャレンジ課題の中身を具体的に書いていて食い違うときは、その節だけを直す（`docs/writing-guidelines-textlint-compliance.md`を読み、`npx textlint`で確かめる）

## コードの決まり

- Java 25。`javac --release 25 -Xlint:deprecation,removal`で警告ゼロ
- 標準入出力は`IO.println`・`IO.readln`（`System.out`や`Scanner(System.in)`は使わない。`System.err`はよい）。クラスを宣言し、`public static void main(String[] args)`から実行する。`import module`は使わない。絵文字は使わない。コメントは日本語で短く
- GUIの章はコンパイルできればよい（ウィンドウを開くmainでよい）。ロジックはGUIでないクラスに分け、実行例ではウィンドウの見た目と反応を説明する（作り物のコンソール出力を書かない）
- 対話的な入力は解答例ではなるべく避け、mainで決まった操作を実演する
- JUnitのテストは任意。加えたら`npm run check:public-code -- exercises/chapterXX`で通ること

## 確認

- リポジトリの外（スクラッチ用のディレクトリ）でコンパイル・実行する。`.class`などの生成物をリポジトリに残さない
- `npm run check:public-code -- exercises/chapterXX`がコンパイル（テストがあればテストも）に成功する
- `npm run check:structure`が通る。`git status`に意図したファイルだけが出る
