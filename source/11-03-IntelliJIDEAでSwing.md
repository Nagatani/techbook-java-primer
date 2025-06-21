---
title: IntelliJIDEAでSwing
---

>オブジェクト指向プログラミングおよび演習1 第14回  
>
>GUIオブジェクトの配置をコードで書くと面倒なので、GUIツール使いましょう

*** この資料のスクリーンショットは、日本語化が適用されたもので作成されており、みなさんが使用しているものと若干違う場合があります。適宜読み替えてください。 ***


## 1.プロジェクトの作成

以下の構成のプロジェクトを作成します。

- プロジェクト名 :「FormSample」

## 2.GUIフォームの追加

以下の画像を参考に、「src」ディレクトリ以下にGUIフォームを作成しましょう。

![](gui01.png)

※もし、GUIフォームの新規作成がメニューにない場合、IntelliJ IDEAの環境設定より、プラグインの項目から「UIデザイナー」を探してインストールしましょう。

フォーム名は、`TextInputSample`とします。

![](gui02.png)

フォームを作成すると、以下の画面のように、デザイナ画面が起動します。

![](gui03.png)

今回は、以下の画面構成に従いGUIを作成します。
配置を行う際に、コンポーネントの親子関係を確認しながら設定を行う必要がありますので、画面赤枠で指定されている「コンポーネントツリー」をよく確認して作業を行ってください。
詳しい指示は、講義内で行います。

![](gui04.png)


## 3.インポートの追加

フォームを作成できたら、コードを記述します。

使用するライブラリを事前にimportしましょう。

package構文の下に以下のコードを貼り付けてください。

```java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
```

今回はIntelliJ IDEAの自動インポート機能を使わず、オンデマンドインポートにて使用するライブラリを指定します。

## 4.メインクラスはJFrameを継承する

メインクラスである「TextInputSample」の宣言部を以下のようにします。

```java
public class TextInputSample extends JFrame {

    // ↓これらのオブジェクトはGUIフォームのデザイナで追加されたものです。
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton button1;

}
```

ここで、上記のフィールドが自動的に追加されていない、もしくはフィールド名が異なる場合は、デザイナの設定を確認してください。

## 5.メインメソッドでJFrameの実行スレッドをキューに追加する

以下のコードをTextInputSampleクラスのメインメソッドとして記述します。

```java
    /**
     * メインメソッド
     * @param args
     */
    public static void main(String[] args) {
        // イベントキューに画面の起動スレッドを追加
        EventQueue.invokeLater(() -> {
            new TextInputSample().setVisible(true);
        });
    }
```

## 6.デフォルトコンストラクタを用意する

デフォルトコンストラクタにて、Swingの画面初期化コードを記述します。そのためにデフォルトコンストラクタを用意しておきましょう。

ついでに画面全体の設定を追加しておきます。

```java
    public TextInputSample() {
        // ウィンドウを閉じたら終了させる
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // ウィンドウ起動位置とウィンドウサイズの指定
        setBounds(100, 100, 600, 400);

        // コンテンツパネル設定 ※IntelliJIDEAでのGUI作成では以下の設定が必須です。
        setContentPane(panel1);


    }
```

ここで、`setContentPane(panel1);`を呼び出していますが、これはSwingをコードで全部書いた場合には差し込まれなかったものです。

## 7.ボタンをクリックした場合の処理を追加する

`TextInputSample.form`のデザイナを開き、`button1`を右クリック、「リスナーの作成」を選択してください。

以下のようにボタンに設定できるイベントの一覧が出てきます。

![](gui05.png)

この中で、`ActionListener`の項目を選択すると、以下のようにActionListnerが持つメソッドの一覧が出るので、`actionPerformed`を選択します。
※`ActionListener`には、`actionPerformed`メソッドしか存在しません。

![](gui06.png)

`public TextInputSample()`内に、以下のコードが差し込まれているはずです。(ラムダ記法に変換可能ですが、今回はこのままで行きましょう。)


```java
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
```

これを、以下のようにボタン押下時の処理として`actionPerformed`のメソッドの中身を書きます。

```java
                // ボタン押下時の処理サンプル

                if (textField1.getText().isEmpty()) {
                    // 入力がない場合はフォーカスセットして処理しない
                    textField1.requestFocus();  // テキストフィールドにフォーカスセット
                    return;
                }

                // テキスト領域にテキストフィールドの文字を追加する
                textArea1.append(textField1.getText());
                textArea1.append(System.lineSeparator()); // 改行

                textField1.setText("");     // テキストフィールドクリア
                textField1.requestFocus();  // テキストフィールドにフォーカスセット
```

この処理は、テキストフィールドに入力されたテキストをボタン押下で下にあるテキストエリアへ追加します。

テキストの追加後、テキストフィールドは空文字にし、フォーカスを設定する処理も行っています。

## 8.実行してみる

上記処理が正しく行われているか、各自確認しましょう。

## ex. ソースコード全文（formのコードは割愛）

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextInputSample extends JFrame {

    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton button1;

    /**
     * メインメソッド
     * @param args
     */
    public static void main(String[] args) {
        // イベントキューに画面の起動スレッドを追加
        EventQueue.invokeLater(() -> {
            new TextInputSample().setVisible(true);
        });
    }

    public TextInputSample() {

        // ウィンドウを閉じたら終了させる
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ウィンドウ起動位置とウィンドウサイズの指定
        setBounds(100, 100, 800, 600);

        // コンテンツパネル設定
        setContentPane(panel1);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ボタン押下時の処理サンプル

                if (textField1.getText().isEmpty()) {
                    // 入力がない場合はフォーカスセットして処理しない
                    textField1.requestFocus();  // テキストフィールドにフォーカスセット
                    return;
                }

                // テキスト領域にテキストフィールドの文字を追加する
                textArea1.append(textField1.getText());
                textArea1.append(System.lineSeparator()); // 改行

                textField1.setText("");     // テキストフィールドクリア
                textField1.requestFocus();  // テキストフィールドにフォーカスセット
            }
        });
    }
}
```

----

# テキストファイル操作サンプル

Swingを用いたファイル処理を書く場合、良いところはファイル選択ダイアログを使用できる点です。以下のGUIを作成してみましょう。

## 1.GUIフォームの追加

フォーム名は、`FileDialogSample`とします。

デザインは、以下を参考にしてください。

![](gui07.png)

## 2.コード追加

1. クラス定義に` extends JFrame`を追加
2. メインメソッド追加
    - 内容: `EventQueue.invokeLater(() -> { new TextInputSample().setVisible(true); });`
3. デフォルトコンストラクタ追加
    - 以下3つの設定を行う
    - `setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);`
    - `setBounds(100, 100, 800, 600);`
    - `setContentPane(panel1);`

## 3.ファイル読み込み、保存処理の追加

ファイル読み込み、書き込みの処理は長くなってしまうのでメソッドで以下の2つを先に用意します。
※import構文を省略しているため、各自で適切なものをimportしてください。

```java
    /**
     * テキストファイル読み込みアクションイベント
     * @param evt
     */
    private void openTextFileActionPerformed(ActionEvent evt) {

        // システムのホームディレクトリを初期パスとしてjavax.swing.JFileChooserを初期化
        JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));

        // 開いたファイルを格納するオブジェクトを用意
        File f = null;

        // ダイアログを開き、戻り値を取得
        int selected = jfc.showOpenDialog(this);

        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                f = jfc.getSelectedFile();
                break;
            case JFileChooser.CANCEL_OPTION:
                JOptionPane.showMessageDialog(this, "キャンセル");
                break;
            default: // case JFileChooser.ERROR_OPTION: でも良い
                JOptionPane.showMessageDialog(this, "取り消しまたはエラー発生");
                break;
        }

        if (f != null) {

            // ファイルパスを表示する
            textField1.setText(f.getAbsolutePath());

            // 開かれたファイルに対する処理
            try {
                // ファイル読み込み
                List<String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
                for (String line : lines) {
                    // 1行ずつテキスト領域に追記
                    textArea1.append(line);
                    textArea1.append(System.lineSeparator());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを開く際にエラー発生");
            }
        }
    }

    /**
     * テキストファイル保存アクションイベント
     * @param evt
     */
    private void saveTextFileActionPerformed(ActionEvent evt) {

        JFileChooser jfc = null;
        // 画面上のファイルパスを取得
        String pathString = textField1.getText();
        if (pathString.isEmpty()) {
            // システムのホームディレクトリを初期パスとしてjavax.swing.JFileChooserを初期化
            jfc = new JFileChooser(System.getProperty("user.home"));
        } else {
            Path p = Path.of(pathString);
            // ファイルの親(ディレクトリ)を取得してその絶対パスを文字列化
            pathString = p.getParent().toAbsolutePath().toString();
            jfc = new JFileChooser(pathString);
        }

        // 開いたファイルを格納するオブジェクトを用意
        File f = null;

        // ダイアログを開き、戻り値を取得
        int selected = jfc.showSaveDialog(this);

        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                f = jfc.getSelectedFile();
                break;
            case JFileChooser.CANCEL_OPTION:
                JOptionPane.showMessageDialog(this, "キャンセル");
                break;
            default: // case JFileChooser.ERROR_OPTION: でも良い
                JOptionPane.showMessageDialog(this, "取り消しまたはエラー発生");
                break;
        }

        if (f != null) {
            // 書き込み対象をBufferedWriterで開く
            try (BufferedWriter bw = Files.newBufferedWriter(f.toPath(), Charset.defaultCharset())) {
                // テキスト領域から文字列を読み込み(Scannerを使用するパターン)
                Scanner sc = new Scanner(textArea1.getText());
                while (sc.hasNextLine()) {
                    bw.append(sc.nextLine());
                    bw.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを保存する際にエラー発生");
            }
        }
    }
```

上記メソッドを2つ用意したら、デザイナにてボタンそれぞれのイベントリスナの追加を行います。(`ActionListener`の`actionPerformed`)

読み込みボタンのイベントリスナには、`openTextFileActionPerformed(e);`を呼び、保存ボタンのイベントリスナには、`saveTextFileActionPerformed(e);`を呼ぶようにしましょう。

## 4.動作確認

テキストファイルの読み込み、保存ができるようになっていることを確認してください。

## ex.コード全文

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class FileDialogSample extends JFrame {
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton openButton;
    private JButton saveButton;
    private JPanel panel1;

    /**
     * メインメソッド
     * @param args
     */
    public static void main(String[] args) {
        // イベントキューに画面の起動スレッドを追加
        EventQueue.invokeLater(() -> {
            new FileDialogSample().setVisible(true);
        });
    }

    public FileDialogSample() {

        // ウィンドウを閉じたら終了させる
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ウィンドウ起動位置とウィンドウサイズの指定
        setBounds(100, 100, 800, 600);

        // コンテンツパネル設定
        setContentPane(panel1);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTextFileActionPerformed(e);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTextFileActionPerformed(e);
            }
        });
    }

    /**
     * テキストファイル読み込みアクションイベント
     * @param evt
     */
    private void openTextFileActionPerformed(ActionEvent evt) {

        // システムのホームディレクトリを初期パスとしてjavax.swing.JFileChooserを初期化
        JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));

        // 開いたファイルを格納するオブジェクトを用意
        File f = null;

        // ダイアログを開き、戻り値を取得
        int selected = jfc.showOpenDialog(this);

        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                f = jfc.getSelectedFile();
                break;
            case JFileChooser.CANCEL_OPTION:
                JOptionPane.showMessageDialog(this, "キャンセル");
                break;
            default: // case JFileChooser.ERROR_OPTION: でも良い
                JOptionPane.showMessageDialog(this, "取り消しまたはエラー発生");
                break;
        }

        if (f != null) {

            // ファイルパスを表示する
            textField1.setText(f.getAbsolutePath());

            // 開かれたファイルに対する処理
            try {
                // ファイル読み込み
                List<String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
                for (String line : lines) {
                    // 1行ずつテキスト領域に追記
                    textArea1.append(line);
                    textArea1.append(System.lineSeparator());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを開く際にエラー発生");
            }
        }
    }

    /**
     * テキストファイル保存アクションイベント
     * @param evt
     */
    private void saveTextFileActionPerformed(ActionEvent evt) {

        JFileChooser jfc = null;
        // 画面上のファイルパスを取得
        String pathString = textField1.getText();
        if (pathString.isEmpty()) {
            // システムのホームディレクトリを初期パスとしてjavax.swing.JFileChooserを初期化
            jfc = new JFileChooser(System.getProperty("user.home"));
        } else {
            Path p = Path.of(pathString);
            // ファイルの親(ディレクトリ)を取得してその絶対パスを文字列化
            pathString = p.getParent().toAbsolutePath().toString();
            jfc = new JFileChooser(pathString);
        }

        // 開いたファイルを格納するオブジェクトを用意
        File f = null;

        // ダイアログを開き、戻り値を取得
        int selected = jfc.showSaveDialog(this);

        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                f = jfc.getSelectedFile();
                break;
            case JFileChooser.CANCEL_OPTION:
                JOptionPane.showMessageDialog(this, "キャンセル");
                break;
            default: // case JFileChooser.ERROR_OPTION: でも良い
                JOptionPane.showMessageDialog(this, "取り消しまたはエラー発生");
                break;
        }

        if (f != null) {

            // 書き込み対象をBufferedWriterで開く
            try (BufferedWriter bw = Files.newBufferedWriter(f.toPath(), Charset.defaultCharset())) {
                // テキスト領域から文字列を読み込み(Scannerを使用するパターン)
                Scanner sc = new Scanner(textArea1.getText());
                while (sc.hasNextLine()) {
                    bw.append(sc.nextLine());
                    bw.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを保存する際にエラー発生");
            }
        }
    }
}
```




〜