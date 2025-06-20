---
title: Swingで作るGUIアプリケーション
---

>オブジェクト指向プログラミングおよび演習1 第14回  
>
>GUIツールを使わずに、JavaコードでSwingアプリケーションを書いてみよう


## 1.プロジェクトの作成

以下の構成のプロジェクトを作成します。

- プロジェクト名 :「SwingSample」
- メインクラス名 :「jp.ac.ait.swing.SwingSample」

## 2.インポートの追加
使用するライブラリを事前にimportします。

package構文の下に以下のコードを貼り付けてください。

```java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
```

## 3.メインクラスはJFrameを継承する

メインクラスである「SwingSample」の宣言部を以下のようにします。

```java
/**
 * SwingSample
 */
public class SwingSample extends JFrame {

}
```

## 4.メインメソッドでJFrameの実行スレッドをキューに追加する

以下のコードをSwingSampleクラスのメインメソッドとして記述します。

```java
    /**
     * メインメソッド
     * @param args
     */
    public static void main(String[] args) {
        // イベントキューに画面の起動スレッドを追加
        EventQueue.invokeLater(() -> {
            new SwingSample().setVisible(true);
        });
    }
```

## 5.使用するGUI部品をフィールドとして用意する

SwingSampleのクラス宣言の直下あたりに、以下のコードを記述します。

```java
private JLabel label;
private JButton button;
```

そろそろ気づいたかもしれませんが、Swingの部品には、先頭にJが付きます。

## 6.デフォルトコンストラクタを用意する

デフォルトコンストラクタにて、Swingの画面初期化コードを記述します。そのためにデフォルトコンストラクタを用意しておきましょう。

```java
    public SwingSample() {

    }
```


## 7.画面全体の設定を追加

以下のコードを`public SwingSample()`内に記述します。

```java
        // ウィンドウを閉じたら終了させる
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ウィンドウ起動位置とウィンドウサイズの指定
        setBounds(100, 100, 300, 200);
        
```

## 8.ラベルの初期化と画面への配置を行う

以下のコードを`public SwingSample()`内に追加します。

```java
        // ラベル設定
        label = new JLabel();
        label.setText("Hello, World!!");
        // ラベルを画面中央に設定
        getContentPane().add(label, BorderLayout.CENTER);
```

## 9.ボタンの初期化とイベントの設定、画面への配置を行う

以下のコードを`public SwingSample()`内に追加します。

```java
        // ボタン設定
        button = new JButton();
        button.setText("Click!!");
        // クリック時のイベント設定
        button.addActionListener((ActionEvent ae) -> {
            label.setText("Hello, Swing!!");
        });
        // 画面下部にボタンを配置
        getContentPane().add(button, BorderLayout.SOUTH);
```

## 10.実行してみる

ボタンをクリックしたら、画面中央のテキストが`Hello, World!!`から`Hello, Swing!!`に変化するかと思います。各自確認しましょう。

## ex. ソースコード全文

```java
package jp.ac.ait.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * SwingSample
 */
public class SwingSample extends JFrame {

    private JLabel label;
    private JButton button;

    /**
     * デフォルトコンストラクタ
     */
    public SwingSample() {
        
        // ウィンドウを閉じたら終了させる
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ウィンドウ起動位置とウィンドウサイズの指定
        setBounds(100, 100, 300, 200);

        // ラベル設定
        label = new JLabel();
        label.setText("Hello, World!!");
        // ラベルを画面中央に設定
        this.getContentPane().add(label, BorderLayout.CENTER);

        // ボタン設定
        button = new JButton();
        button.setText("Click!!");
        // クリック時のイベント設定
        button.addActionListener((ActionEvent ae) -> {
            label.setText("Hello, Swing!!");
        });
        // 画面下部にボタンを配置
        this.getContentPane().add(button, BorderLayout.SOUTH);
    }

    /**
     * メインメソッド
     * @param args
     */
    public static void main(String[] args) {
        // イベントキューに画面の起動スレッドを追加
        EventQueue.invokeLater(() -> {
            new SwingSample().setVisible(true);
        });
    }
}
```
