# 第24章 発展課題 - リポジトリをインターフェイスにする

## 学習目標
- 既存のクラスからインターフェイスを取り出し、使う側をインターフェイスに依存させる
- 使う実装をコンストラクターで受け取る（依存性の注入）
- 変更の前後で、確認のプログラムが同じ結果を出すことを確かめる

## 課題1: フェーズ3のStudentRepositoryをインターフェイスにする

付録Dのフェーズ3（`appendix/d-integrated-project/phase3/`）のファイルをすべてコピーして始めます。

フェーズ3の`StudentRepository`はクラスで、`StudentService`の中で`new`しています。
このままでは、フェーズ6でデータベースに保存するときに、`StudentService`を書き換えることになります。
`StudentRepository`をインターフェイスにして、実装を差し替えられるようにしてください。

**要件:**

- `StudentRepository<T extends Student>`をインターフェイスにする。メソッドは保存に必要な4つ（`addStudent`・`findById`・`findAll`・`removeStudent`）だけにする
- メモリ上に保存する実装`InMemoryStudentRepository<T>`を作る。`HashMap`と`ArrayList`の二重管理をやめ、`LinkedHashMap`を1つだけ使う
- 平均GPAと学部別の人数の集計は、`StudentService`に移す（`findAll`の結果から計算する）
- `StudentService`に、リポジトリを受け取るコンストラクターを加える。引数なしのコンストラクターは、メモリ上の実装を使う
- `Phase3Test`を変えずに実行し、学生の一覧と集計の結果が変更前と同じになることを確かめる
- `StudentService`を変えずに別の実装を渡せることを、確認のプログラム（`RepositorySwapCheck`）で示す

**ヒント:**

- フェーズ3の`getStudentCountByDepartment`は`HashMap`を返すので、学部の並び順が決まっていない。集計を移すときに、並び順を決めておくと結果を比べやすい（解答例では`TreeMap`で学部名の順にした）
- インターフェイスに置くメソッドは少ないほど、別の実装を作りやすい。検索や集計は`findAll`があれば書ける
