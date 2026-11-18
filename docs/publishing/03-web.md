# Web版（GitHub Pages）

ALPHA版をHTMLにしてGitHub Pagesで公開しています。公開は自動で、手で行う作業はmainへのpushと結果の確認だけです。

## しくみ

mainへpushすると、GitHub Actionsの`.github/workflows/deploy.yml`が次のことを行います。

1. `npm ci`で依存を入れる
2. `npm run build4web`でALPHA版をHTMLにする（出力先は`dist/`）
3. `dist/`をGitHub Pagesに公開する

Web版は常にALPHA版です。正規版をWebで公開するしくみはありません。正規版を出版した後もWeb版を続けるかどうかは、[07-release.md](07-release.md)の「出版の前に決めること」で決めます。

## 公開する前に手元で確かめる

```bash
npm run build4web
python3 -m http.server -d dist 8000   # http://localhost:8000 で開く（Ctrl+Cで止める）
```

ふだんの確認は`npm run preview`で足ります。`build4web`を手元で流すのは、ビルドの設定を変えたときです。

## 公開の結果を確かめる

```bash
gh run list --workflow deploy.yml --limit 3   # 直近の公開の結果
gh run view --log-failed                      # 失敗したときのログ
```

GitHubのリポジトリの「Actions」タブでも同じものを見られます。公開されたページは、リポジトリの「Settings」→「Pages」に表示されるURLで開きます。

確かめることは次のとおりです。

- 公開のジョブが成功している
- 表紙に「ALPHA VERSION」が出ている
- 奥付の日付がpushした日になっている
- 直した箇所が反映されている（ブラウザーのキャッシュで古いページが出ることがあるので、強制的に再読み込みする）

## 失敗したとき

| 症状 | 原因と対処 |
|---|---|
| `npm ci`で止まる | `package.json`と`package-lock.json`が食い違っている。lockファイルを手で直さず、`npm install`で作り直す（`CLAUDE.md`の「依存関係の管理」） |
| ビルドで止まる | 手元で`npm run build4web`を流して同じエラーが出るか確かめる |
| 図が古い | 図のSVGを作り直したのにコミットしていない。`manuscripts/images/diagrams/`の変更をコミットする |

---

最終更新：2026-11-19
