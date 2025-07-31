---
title: Swingにおける代表的なイベントとカスタムイベント
---

> オブジェクト指向プログラミングおよび演習1 第11回


この資料では、Java Swingで作成したGUIアプリケーションを、ユーザーが簡単に実行できるMac用アプリケーション（`.app`）に変換する方法を、正しいディレクトリ構成に重点を置いて解説します。

`jpackage`は非常に強力なツールですが、ディレクトリの扱い方を誤ると「ファイル名が長すぎます (File name too long)」というエラーが発生しがちです。この資料の手順に従うことで、その問題を未然に防ぎ、スムーズにアプリケーションを作成できます。

### この資料のポイント： 入力と出力を完全に分離する

`jpackage`でエラーを起こさないためのポイントは、**「パッケージ化の材料（入力）が入ったディレクトリ」と「作成されたアプリ（出力）を保存するディレクトリ」**を完全に分けることです。

この資料では、以下のディレクトリ構成で進めます。

```bash
/MySwingProject/  <-- プロジェクトのルートディレクトリ
|
|-- SimpleSwingApp.java     <-- アプリのソースコード
|
|-- input_files/            <-- 【入力用】作成したJARファイルをここに入れる
|   └── (ここにJARファイルが作成される)
|
|-- output_app/             <-- 【出力用】作成された.appファイルはここに入る
|   └── (ここに.appファイルが作成される)
```

### ステップ1: プロジェクトの準備とSwingアプリの作成

まず、上記の構成に沿ってプロジェクトの準備を進めます。

#### 1. プロジェクトのルートとなるディレクトリを作成し、移動します。

```bash
mkdir MySwingProject
cd MySwingProject
```

#### 2. 入力用と出力用のディレクトリをあらかじめ作成しておきます。

  ```bash
  mkdir input_files
  mkdir output_app
  ```

#### 3. `MySwingProject` ディレクトリ直下に、`SimpleSwingApp.java` という名前でアプリケーションのソースコードを作成します。

```java
// SimpleSwingApp.java
import javax.swing.*;
import java.awt.FlowLayout;

public class SimpleSwingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Macアプリ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));
            frame.add(new JLabel("jpackageによるMacアプリ化が成功しました！"));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

### ステップ2: JARファイルの作成と配置

次に、Javaコードをコンパイルし、実行可能なJARファイルを作成します。このとき、作成したJARファイルを直接`input_files`ディレクトリに配置するのがポイントです。

#### 1. ソースコードをコンパイルする

`MySwingProject` ディレクトリで、以下のコマンドを実行します。

```bash
javac SimpleSwingApp.java
```

#### 2. マニフェストファイルを作成する
JARファイルにメインクラスを教えるため、`manifest.txt` を作成します。ファイルの最後に必ず改行を入れてください。

```text
Main-Class: SimpleSwingApp

```

#### 3. JARファイルを`input_files`ディレクトリに作成する

`jar`コマンドを使い、JARファイルを入力用ディレクトリ (`input_files`) の中に直接作成します。

```bash
# 書式: jar cvfm [出力先JARパス] [マニフェスト名] [含める.classファイル]
jar cvfm ./input_files/MyCoolApp.jar manifest.txt SimpleSwingApp.class
```

実行後、`input_files`の中に`MyCoolApp.jar`が正しく配置されていることを確認してください。


### ステップ3: `jpackage`でMacアプリを作成する

準備が整いました。入力と出力が分離されたクリーンな状態で、`jpackage`を実行します。

#### 1. `MySwingProject` ディレクトリ（プロジェクトのルート）にいることを確認してください。

#### 2. 以下の`jpackage`コマンドを実行します。

* `--input`には`./input_files`を指定
* `--dest`には`./output_app`を指定

```bash
jpackage --type app-image \
          --name "MyCoolApp" \
          --input ./input_files \
          --main-jar MyCoolApp.jar \
          --dest ./output_app
```

コマンドの各オプションの意味:

* `--type app-image`: `.app`形式のアプリケーションイメージを作成します。インストーラー形式の`.dmg`にしたい場合は`--type dmg`とします。
* `--name "MyCoolApp"`: アプリケーションの名前です。
* `--input ./input_files`: **【重要】**パッケージ化の材料が入っているディレクトリを指定します。
* `--main-jar MyCoolApp.jar`: 入力ディレクトリ内にある、メインのJARファイル名を指定します。
* `--dest ./output_app`: **【重要】**完成したアプリを保存するディレクトリを指定します。

#### 3.  完成したアプリを確認する

コマンドの実行が完了すると、`output_app`ディレクトリの中に`MyCoolApp.app`が作成されています。Finderでこのアプリをダブルクリックし、無事に起動すれば成功です。

この手順を守ることで、`jpackage`が自分自身の出力ファイルを読み込んでしまう再帰的なエラーを確実に回避し、安定してアプリケーションをビルドできます。

