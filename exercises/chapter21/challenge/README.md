# 第21章 チャレンジ課題

## 学習目標
- 依存するもの（保存先、お知らせの送り先、今日の日付）をインターフェイスにし、コンストラクターで受け取る（DI）
- 本物の部品の代わりに、スタブ・フェイク・スパイなどのテストダブルを差し込んでテストする
- Mockitoの`@Mock`・`when().thenReturn()`・`verify()`で、振る舞いを決め、呼び出しを確かめる
- 現在の日付のように毎回変わるものを外から渡し、テストの結果を毎回同じにする
- AAAパターンと`@Nested`・`@DisplayName`で、読みやすいテストを書く

## 課題一覧

### 課題1: 図書館の貸出サービスとそのテスト
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── Book.java                    本（レコード）
├── Loan.java                    貸出の記録（レコード）
├── LoanException.java           貸出・返却できないときの例外
├── BookCatalog.java             蔵書の目録（インターフェイス）
├── LoanRepository.java          貸出の記録の保存先（インターフェイス）
├── Notifier.java                お知らせの送り先（インターフェイス）
├── TodayProvider.java           今日の日付（インターフェイス）
├── LoanService.java             貸出・返却・延滞のお知らせ（テスト対象）
├── InMemoryLoanRepository.java  メモリに保存する実装
├── ConsoleNotifier.java         お知らせを画面に表示する実装
├── LibraryApp.java              mainメソッド（本物の部品で組み立てたデモ）
├── LoanServiceTest.java         手書きのテストダブルを使うテスト
└── LoanServiceMockitoTest.java  Mockitoを使うテスト
```

図書館の貸出を扱う`LoanService`を作り、データベースやメールや実際の日付がなくてもテストできるようにしてください。
`LoanService`は、目録・保存先・お知らせの送り先・今日の日付の4つに依存します。
これらをすべてインターフェイスにして、コンストラクターで受け取ります。

**要求仕様**:
- `LoanService`
    - `borrow(会員番号, ISBN)`で本を貸し出し、返却期限を今日から14日後にした`Loan`を保存して返す
    - 次のときは`LoanException`を投げ、何も保存しない
        + 蔵書にない本
        + 貸出中の本
        + その会員がすでに3冊借りている
        + その会員に返却期限を過ぎた本がある（期限の当日はまだ過ぎていない）
    - `returnBook(ISBN)`で記録を消し、延滞料金（期限を過ぎた日数×10円）を返す。料金が0円より多ければ、会員にお知らせを送る。貸し出していない本なら`LoanException`を投げる
    - `sendOverdueNotices()`で、期限を過ぎた貸出のそれぞれにお知らせを送り、送った件数を返す
    - 今日の日付は`LocalDate.now()`を直接呼ばず、`TodayProvider`から受け取る
- `LoanServiceTest`（手書きのテストダブル）
    - 目録はラムダ式のスタブ、保存先は`InMemoryLoanRepository`（フェイク）、お知らせは送った内容をリストに記録するスパイにする
    - 今日の日付はテストクラスのフィールドにし、テストの途中で日付を進めて延滞を作る
    - 貸出の4つの断る条件、期限の当日の扱い、延滞料金、お知らせの文面を確かめる
- `LoanServiceMockitoTest`（Mockito）
    - 目録・保存先・お知らせをモックにする
    - 貸出で`save`に渡した`Loan`、断ったときに`save`を呼ばないこと、延滞した返却で`delete`とお知らせが呼ばれることを`verify`で確かめる

**実行例**（`LibraryApp`の実行結果）:
```
--- 2026-04-01 ---
M001さんに「Java入門」を貸し出しました（返却期限 2026-04-15）
M001さんに「アルゴリズム図鑑」を貸し出しました（返却期限 2026-04-15）
貸出できません: 「Java入門」は貸出中です
貸出できません: ISBN 978-4-9999 の本は蔵書にありません

--- 2026-04-11 ---
M002さんに「データベースの基礎」を貸し出しました（返却期限 2026-04-25）

--- 2026-04-18 ---
  [お知らせ → M001] 「Java入門」の返却期限（2026-04-15）を3日過ぎています
  [お知らせ → M001] 「アルゴリズム図鑑」の返却期限（2026-04-15）を3日過ぎています
延滞のお知らせ: 2件
貸出できません: M001さんには返却期限を過ぎた本があります
  [お知らせ → M001] 「Java入門」の延滞料金は30円です
978-4-0001 が返却されました（延滞料金 30円）
978-4-0003 が返却されました（延滞料金 0円）
```

`LibraryApp`は、日付を進められるデモ用の`TodayProvider`を使っています。
実際のアプリでは`LocalDate::now`を渡します。

**実行例**（テストの実行結果）:
```
+-- 貸出サービス（手書きのテストダブル） [OK]
| +-- 延滞のお知らせ [OK]
| | +-- 延滞がなければ何も送らない [OK]
| | '-- 期限を過ぎた貸出だけにお知らせを送る [OK]
| +-- 返却 [OK]
| | +-- 期限内の返却は料金0円で、お知らせを送らない [OK]
| | +-- 貸し出していない本は返却できない [OK]
| | '-- 3日遅れの返却は30円で、お知らせを送る [OK]
| '-- 貸出 [OK]
|   +-- 貸出中の本は貸し出せない [OK]
|   +-- 返却期限の当日はまだ借りられる [OK]
|   +-- 返却期限を過ぎた本がある会員は借りられない [OK]
|   +-- 蔵書にない本は貸し出せない [OK]
|   +-- 返却期限は今日から14日後で、記録が保存される [OK]
|   '-- 3冊借りている会員は4冊目を借りられない [OK]
'-- 貸出サービス（Mockito） [OK]
  +-- 貸し出すと、返却期限を入れた記録をリポジトリに保存する [OK]
  +-- 期限内の返却なら、お知らせを送らない [OK]
  +-- 蔵書にない本なら、リポジトリに何も保存しない [OK]
  '-- 5日遅れの返却なら、記録を消して延滞料金のお知らせを送る [OK]

[        15 tests successful      ]
[         0 tests failed          ]
```

## コンパイルと実行

デモは、JUnitやMockitoがなくてもコンパイル・実行できます。

```bash
javac Book.java Loan.java LoanException.java BookCatalog.java LoanRepository.java \
      Notifier.java TodayProvider.java LoanService.java InMemoryLoanRepository.java \
      ConsoleNotifier.java LibraryApp.java
java LibraryApp
```

テストは、IntelliJ IDEAで第21章の手順どおりにJUnit 5とMockito（`org.mockito:mockito-junit-jupiter`）を追加して実行します。
コマンドラインで実行するときは、Maven Centralから次のJARを入手してフォルダーに置き、クラスパスに加えます。

- `junit-platform-console-standalone-1.14.4.jar`
- `mockito-core-5.24.0.jar`、`mockito-junit-jupiter-5.24.0.jar`
- Mockitoが使う`byte-buddy`・`byte-buddy-agent`・`objenesis`のJAR（`mockito-core`のpomに書かれた版）

```bash
CP="junit-platform-console-standalone-1.14.4.jar:mockito-core-5.24.0.jar:mockito-junit-jupiter-5.24.0.jar:byte-buddy-1.17.7.jar:byte-buddy-agent-1.17.7.jar:objenesis-3.3.jar"
javac -cp "$CP" -d out *.java
java -jar junit-platform-console-standalone-1.14.4.jar execute -cp "out:$CP" --scan-class-path --details=tree
```

WindowsではJARの区切りの`:`を`;`にします。
多くのJARを手で集めるのは手間がかかります。
第22章で学ぶMavenを使うと、`pom.xml`に依存関係を書くだけで、必要なJARがそろいます。
実行すると、Mockitoがエージェントを動的に読み込んだという警告が出ることがありますが、テストの結果には影響しません。

## 実装のヒント

```java
// 依存するものはすべてコンストラクターで受け取る
public LoanService(BookCatalog catalog, LoanRepository repository,
                   Notifier notifier, TodayProvider todayProvider) { ... }

// テストでは、今日の日付をフィールドにして、ラムダ式から読む
private LocalDate today = LocalDate.of(2026, 4, 1);
service = new LoanService(catalog, repository, notifier, () -> today);

today = today.plusDays(17);           // 17日後にする
assertEquals(30, service.returnBook("1"));
```

```java
// Mockito: 呼ばれたことと、呼ばれなかったことを確かめる
verify(notifier).send("M001", "「Java入門」の延滞料金は50円です");
verify(repository, never()).save(any());
```

- `LocalDate.now()`をサービスの中で呼ぶと、テストを実行する日によって結果が変わる。日付も「外から渡す依存」として扱う
- `BookCatalog`や`TodayProvider`のようにメソッドが1つのインターフェイスは、テストの中でラムダ式で実装できる
- スパイは、受け取った引数をリストにためるだけのクラスでよい。テストの最後に`assertEquals(List.of(...), notifier.messages)`で中身を比べる
- `Loan`と`Book`をレコードにしておくと`equals`が自動で作られるので、`verify(repository).save(new Loan(...))`のように、同じ内容のオブジェクトで引数を確かめられる
- 日数の差は`ChronoUnit.DAYS.between(返却期限, 今日)`で求める。期限より前なら負になるので、0と比べて大きいほうを使う
- Mockitoは、テストで使わなかった`when(...)`の設定があるとエラーにする。そのテストに必要な振る舞いだけを設定する

## 応用のポイント

1. テストしやすい設計は、変更しやすい設計
    - `Notifier`をメールで送る実装に替えても、`LoanService`は変えなくてよい。テストのために入れたインターフェイスが、そのまま部品の差し替え口になる
2. 手書きとMockitoの使い分け
    - 状態を持つ保存先は、手書きのフェイクにすると自然に書ける。「呼ばれたか」「呼ばれなかったか」を確かめたいお知らせは、Mockitoの`verify`が向いている
3. 境目と日付のテスト
    - 返却期限の当日と翌日、3冊目と4冊目のような境目を、日付を自由に決められるおかげで簡単にテストできる

## 発展（任意）

- 予約の機能（貸出中の本を予約でき、返却されたら予約した会員にお知らせを送る）を、テストを先に書いて加える
- 延滞料金に上限（1冊あたり500円）を設ける。上限の境目（49日・50日・51日の延滞）をパラメーター化テストで確かめる
- 第21章の「継続的インテグレーション（CI）との統合」にならい、第22章でMavenのプロジェクトにしたあと、GitHub Actionsでプッシュのたびにテストを実行する

## 完了チェックリスト

- [ ] `LoanService`が`LocalDate.now()`や具体的なクラスを直接使わず、4つのインターフェイスだけに依存している
- [ ] 手書きのテストダブルを使うテストと、Mockitoを使うテストの両方が成功する
- [ ] 貸出を断る4つの条件と、返却期限の当日の扱いをテストしている
- [ ] 断ったときに何も保存しないことを`verify(..., never())`で確かめている
- [ ] テストを何度実行しても、実行する日が変わっても、同じ結果になる

**次のステップ**: チャレンジ課題が完了したら、第22章「ドキュメントと外部ライブラリ」に進みましょう。
