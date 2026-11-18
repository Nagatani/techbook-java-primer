# 付録B（Deep Dive）の主張の検証

付録Bは、JVMの内部やライブラリの実装など、読者が自分では確かめにくい内容を扱います。
誤りがあっても気づかれにくいため、解説の主張ごとに「どうすれば確かめられるか」を台帳に書き、JDKで実際に確かめ直せるようにしています（2026-11-19〜）。

## しくみ

- 節ごとの台帳 `appendix/bXX-*/claims.yml`
  - 解説の主張（`claim`）と、その確かめ方（`verify`）、出典（`sources`）を書く
- 解説の印 `<!-- claim: id -->`
  - 主張を述べた段落・リスト項目・表の直後に置く。1つの印に複数のidを「,」で並べてよい。同じidを何か所に置いてもよい
  - リスト項目に付けるときは、項目の字下げ（2文字）に合わせる（行頭に置くとリストが切れる）
- 観察用のプログラム `appendix/bXX-*/verify/*.java`
  - パッケージを持たない小さなプログラム。読者が自分で動かして観察するためのもので、検査もこれを動かす
  - `check:public-code`のコンパイルの対象にもなる
- 出力の欄 `<!-- output: id -->` 〜 `<!-- /output -->`
  - `npm run check:deep-dive -- --update`が、その主張の検証で得た実際の出力を書き込む。手で書かない
- 検査 `npm run check:deep-dive`（`scripts/check-deep-dive.js`）

## 確かめ方の種類

| kind | 内容 | 主な項目 |
|---|---|---|
| `javac` | プローブをコンパイルし、コンパイラーの出力（エラー・警告）を照合する | `probe`、`fails`（`true`ならコンパイルが失敗すること）、`javac`（追加のオプション）、`release`、`expect`、`absent` |
| `javap` | プローブをコンパイルし、`javap`の出力を正規表現で照合する | `probe`、`class`、`options`（既定は`-c -p`）、`release`（`--release`の版。`none`なら付けずに実行中のJDKでコンパイルする。`--add-exports`と併用するとき）、`expect`、`absent` |
| `run` | プローブ（または節の`src/main/java`）を実行し、標準出力と標準エラー出力を照合する | `probe`か`main`、`class`、`jvm`（JVMのオプション）、`args`、`expect`、`absent`、`attempts`、`timeout`（秒）、`libs`、`resources`（クラスパスに加えるディレクトリ。`META-INF/services`など） |
| `flag` | JVMのフラグが実在し、既定値が一致するか | `flag`、`value` |
| `manual` | 機械では確かめられない主張（仕様の文言、歴史、設計論） | `checked`（確かめた日）、`note`（何を読んで確かめたか）。`sources`が必須 |

- `expect`・`absent`はJavaScriptの正規表現（複数行モード）。`[\s\S]*`で行をまたげる
- JITの判断のように実行ごとにぶれるものは`attempts`（試す回数）を指定し、1回でも当てはまれば合格にする
- 出力の欄に載せる行は`show`（正規表現の配列。省くと`expect`）で選ぶ。`normalize`（正規表現 → 置き換える文字）で番地や番号を消し、`sort: true`で並べて重複を除く。字下げと連続する空白は詰めるが、`keepIndent: true`なら残す（木の表示や表）。`range: { from: 正規表現, to: 正規表現 }`で、ある行から次の行（`to`を省くと空行）までに絞れる（`javap`の1つのメソッドだけを載せるときなど）
- `libs`にはMaven Centralの座標を書く（`.cache/sample-code-libs/`に取得する）。オブジェクトの配置はJOL（`org.openjdk.jol:jol-core:0.17`）で観察できる
- コンパイルが失敗するはずのプローブ（`javac`の`fails: true`）は、`verify/`の下の`compile-errors/`に置き、`check:public-code`の対象から外す（下の「コンパイルできないプローブ」）
- 台帳の先頭に`defaults:`（例：`defaults: { libs: [...] }`）を書くと、`javac`・`javap`・`run`の主張に既定で加わる（主張の側に書いたものが優先）
- 解説から参照しない補助の検証（同じ主張の別の条件など）は`unreferenced: true`を付ける

## 出典の扱い

- 仕様（JLS・JVMS）、APIドキュメント、javaコマンドのリファレンスはOracleの公式サイト（`docs.oracle.com`）の、Java SE 25の版を使う
- 機能が入った版は、JEP（`openjdk.org/jeps/`）を出典にしてよい。JEPはOpenJDKの公式の文書である
- HotSpotの実装（vtable、JITの判断など）は仕様に書かれていない。実装の主張は、出典よりも観察（`run`・`javap`）で確かめる
- `sources`には`title`・`url`・`anchor`（ページにあるはずの文字）を書く。`npm run check:deep-dive -- --online`で、ページが開けるか、`anchor`がページにあるかを確かめる

## 検査の内容

| 検査 | 失敗の扱い |
|---|---|
| 台帳の書式（id・kind・必須の項目、プローブの有無） | エラー |
| 解説の印と台帳の対応（印のidが台帳にない、台帳の主張に印がない） | エラー |
| 解説・コード・pom.xmlなどに書いた`-XX:`のフラグがJDK 25に実在するか（削除されたフラグを説明するときは、台帳の先頭の`removedFlags`に名前を書く） | エラー |
| 各主張の検証（`javap`・`run`・`flag`） | エラー |
| 印のないまとまりで、数値・Javaの版・性能の言い回しを含むもの | 注意（検証していない主張の候補） |
| `manual`の主張で、`checked`から1年を過ぎたもの | 注意（JEPの状態やライブラリの版が変わっていないかを読み直し、`checked`を更新する） |

`--static`はJavaを動かさない検査（上の3つ）だけを行います（約1秒。pre-commitフックで実行）。
`--report`は節ごとの集計と注意の一覧を`docs/analysis/deep-dive-claims.md`に書きます。

## 節を書き直すときの手順

1. 解説の主張を書き出し、確かめ方を決める。確かめられない主張は、確かめられる形に言い換えるか、出典のある`manual`にするか、削る
2. 観察できる主張は`verify/`にプローブを書き、自分で実行して出力を見てから`expect`を書く（出力を想像で書かない）
3. 解説に印と出力の欄を置き、`npm run check:deep-dive -- bXX --update`で出力を書き込む
4. `npm run check:deep-dive -- bXX`で失敗がないこと、注意の候補が主張でないことを確かめる
5. 「シミュレーション」のデモ（JVMの中を見ずに、それらしい表や数値を出すもの）は、観察に置き換えるか、シミュレーションであることを解説に明記する

## 節の構成

書き直した節は、おおむね次の形にそろえます。

1. この節の読み方（印と台帳の説明）
2. ファイル構成（観察用のプログラム、体験用のデモ）
3. 本文（仕組み → 観察する → 説明）。仕様が決めていることと、実装の工夫を分けて書く
4. よくある誤解（本文の節を参照する）
5. 参考資料（仕様の節と、そのURL）

## 気づいたこと（2026-11-19の15節の書き直しから）

15節を書き直し、主張610件（機械で確認506件、手で確認104件）を台帳にした。どの節でも、確かめると誤りが見つかった。

- 見つかった誤りのパターン
  - JVMの実装を仕様のように書く（vtable、エスケープ解析を「スタック割り当て」と断定、共通プールの並列度がコア数-1）
  - 条件の一部だけを書く（HashMapの木が連結リストに戻る条件、Optionalがモナドの法則を満たす、volatileが「即座に」見える）
  - 版と状況が古い（JEP 401、GCの既定の選び方、AtomicIntegerの内部、`-XX:CompileThreshold`）
  - 根拠のない数値・順位・目安（分割効率の順位、「単体70%・統合20%・E2E10%」、ミューテーションスコアの評価、メモリの見積もり）
  - デモの不具合（Permissionの判定、再帰でスタックがあふれるPersistentList、非原子的なStripedCounter、文字化けするReactiveFileReader）。解説の主張を確かめる過程で、コードの誤りも見つかる
  - 本文の誤り（第15章のvolatile）も、付録の観察から見つかった
- 観察のこつ
  - JITの判断は`-XX:+PrintInlining`・`-XX:+PrintCompilation`で見えるが、実行ごとに順序と数値が変わる。`attempts`と`sort: true`を使う
  - 環境で変わる観察（GCの既定、コア数）は`-XX:ActiveProcessorCount`などで条件をそろえ、どの環境で確かめたかを解説に書く。OSで変わるもの（WatchService、属性のビュー）は、どちらでも合格する正規表現にし、Linuxの結果はDockerで確かめる
  - 「起こりうる」だけの結果（データ競合、並べ替え）は、必ず起こる観察と分けて書く。jcstressはこの環境では使えないので、確率的な観察は`attempts`を多めにする
  - 2段階のコンパイル（バイナリ互換）や2段の実行（AOTキャッシュ）は、プローブの中で`javax.tools`や子プロセスを使う
  - 出典の照合は、HTMLの文字参照とタグをまたぐ文に注意する。PDFとACMのページは機械で中身を照合できないので、読んだ内容を`note`に書く
- JDK 25の細部
  - JDK 25の`-XX:+PrintInlining`は、行の途中に「callee changed to」という別の判断の文字が混じる。行の先頭の「メソッド名（バイト数）判断」を照合する
  - C1が35バイトを超えるメソッドをインライン化しないときの表示は「callee is too large」（「too big」ではない）。C2では、よく呼ばれるメソッドが325バイトを超えると「hot method too big」
  - javaコマンドのリファレンス（Java SE 25）には`-XX:CompileThreshold`の説明がない。段階的コンパイルで効かないことは、実行して確かめた（B.04）
  - `MemorySegment.asByteBuffer()`は、2GBを超えるときAPIドキュメントでは`UnsupportedOperationException`だが、JDK 25.0.2では`IllegalStateException`になる（B.13）
- 残した課題は`docs/analysis/issues-tracking.md`のIssue #033

## コンパイルできないプローブ

「この書き方はコンパイルエラーになる」という主張は、`verify/compile-errors/`にプローブを置き、`kind: javac`と`fails: true`で確かめます。
`check:public-code`はこのディレクトリを対象にしません（公開するコードはコンパイルできる状態を保つ方針の例外）。
1つのファイルに1つの誤りだけを書き、`expect`にはjavacの英語のメッセージ（`-J-Duser.language=en`で実行する）を書きます。
