# 出版の前に行う検査

どの形で出す場合も、出力を作る前に原稿とコードの検査を通します。GitHub Actionsは軽い検査とコンパイルだけを行い、時間のかかる検査（textlint、実行結果の照合、JUnitのテスト、主張の検証、紙面の折り返し）は手元だけで流します（2026-11-26〜）。出版の前には手元ですべて流します。

## 検査の一覧

上から順に流します。かかる時間は2026-11の手元の端末での目安です。

| コマンド | 確かめること | 時間 | CIでの扱い |
|---|---|---|---|
| `npm run lint` | 原稿の文章の規約（textlint）。表記ゆれ（`prh-rules.yml`）を含む | 約1分 | 手元だけ |
| `npm run check:compliance` | `CLAUDE.md`の表記ルール（リストの導入文のコロン、外部URLのリンクなど） | 数秒 | コミット前の検査 |
| `npm run check:structure` | 章・演習・付録B・用語集・相互参照ガイドの食い違い | 数秒 | 失敗にする |
| `npm run index:check` | 索引と要約（`docs/summaries/`）が原稿に追いついているか | 数秒 | 失敗にする |
| `npm run check:diagrams` | 図の元データ・画像・原稿の対応、図の文字の大きさ | 数十秒 | 失敗にする |
| `npm run check:samples` | 本文のコードと全文（`sample-code/`）の一致、コンパイル、実行結果の照合 | 数分 | 抜粋の一致は失敗にする。コンパイルはコードを変えたときだけ。実行は手元だけ |
| `npm run check:java:run` | 全文を公開していない番号付きのコードのコンパイルと実行結果の照合 | 数分 | 手元だけ |
| `npm run check:public-code` | 付録・演習・統合プロジェクトのコードのコンパイルとJUnitのテスト | 数分 | コンパイルはコードを変えたときだけ。テストは手元だけ |
| `npm run check:code-wrap` | 紙面でコードの行が折り返していないか | 数分 | 手元だけ |

まとめて流すときは`npm run check:local`を使います（`scripts/check-local.sh`）。失敗しても止まらずに最後まで流し、終わりに検査ごとの結果と時間を一覧にします。上の表の検査に加えて、付録Bと本文の主張の検証（`node scripts/check-claims.js`）も流します。全部で20分ほどかかります。

```bash
npm run check:local            # すべて
npm run check:local -- quick   # CIと同じ軽い検査とtextlintだけ（1分ほど）
```

`check:samples`と`check:java:run`はGUIのサンプルも起動するので、検査のあいだにウィンドウが開いたり閉じたりします。macOSではそのままで動きます。

## 結果の読み方

- textlint
    + エラーが0件であることを確かめる（2026-11-16から0件を保っている）
    + 直すときは`docs/guides/writing-guidelines-textlint-compliance.md`に従う。`npm run lint:fix`で直せるものは自動で直し、それ以外は1件ずつ文脈を見て直す
- `check:diagrams`
    + 「注意」が出た図は直す。直し方は`CLAUDE.md`の方針11
- `check:samples`・`check:java:run`
    + 本文のコードは直接直さず、`sample-code/`の全文を直してから`npm run samples:sync`を流す
- `check:public-code`
    + コンパイルの失敗は必ず直す。テストの失敗は、意図して失敗させている演習の雛形（学生が実装する前の状態）でないかを確かめる
- `check:code-wrap`
    + 折り返す行が0件であることを確かめる。出たら全文で改行し、`npm run samples:sync`を流す

## 原稿を直したら

原稿を直したときは、次の2つも行います。

```bash
npm run index          # 索引を作り直す
npm run index:check    # 古くなった要約を一覧にする
```

古くなった要約は、`docs/summaries/chapterXX.md`の該当する節の要約とハッシュを書き直します（`docs/summaries/README.md`）。

## コミット

検査が通ったら、直した内容を種類ごとに分けてコミットします（`fix:`・`docs:`などの接頭辞、日本語のメッセージ）。コミットの前に`check:structure`と`check:compliance`が自動で動き、問題があればコミットが止まります。`--no-verify`で飛ばさず、中身を直してください。

mainへpushすると、軽い検査（`.github/workflows/check.yml`、1分ほど）が動きます。`sample-code/`・`appendix/`・`exercises/`を変えたときはコードのコンパイル（`code.yml`）が、原稿・テーマ・図などを変えたときはWeb版の公開（[03-web.md](03-web.md)）が動きます。いくつかのコミットをまとめてpushする前に`npm run check:local`を流しておきます。

---

最終更新：2026-11-26
