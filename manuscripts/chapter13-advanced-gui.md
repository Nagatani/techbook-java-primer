# 第13章 高度なGUIコンポーネント

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ`JList`や`JTable`は、**総合演習プロジェクト「ToDoリストアプリケーション」** の中核となる、タスク一覧表示機能を実装するために不可欠なコンポーネントです。

- **`JList`によるタスク表示**: 複数の`Task`オブジェクトを一覧形式で表示します。ユーザーがリストから特定のタスクを選択する操作も、このコンポーネントで実現します。
- **`JTable`による詳細表示**: もしタスクに「優先度」や「期日」といった複数の情報を持たせる場合、`JTable`を使えば、スプレッドシートのような表形式で、より詳細な情報を分かりやすく表示できます。
- **MVCアーキテクチャ**: これらのコンポーネントを効果的に使うために、データの管理（Model）と画面表示（View）を分離する、MVC（Model-View-Controller）という重要な設計パターンを学びます。これにより、アプリケーションの保守性と拡張性が飛躍的に向上します。

## 📋 本章の学習目標

### 前提知識
- **第11章、12章の知識**: Swingの基本的な画面構築とイベント処理を理解している。
- **コレクションフレームワーク**: `List`や`Map`などの基本的な使い方を理解している。

### 到達目標

#### 知識理解目標
- `JList`, `JTable`, `JTree`の役割と基本的な使い方を理解する。
- モデル・ビュー・コントローラ（MVC）アーキテクチャの基本概念を説明できる。
- `DefaultListModel`や`DefaultTableModel`が、コンポーネントのデータを管理するための「モデル」であることを理解する。

#### 技能習得目標
- `JList`を使って、動的に変化するリストを表示できる。
- `JTable`を使って、簡単な表を作成し、データを表示できる。
- MVCの考え方にもとづき、データとビューを分離した簡単なGUIアプリケーションを設計・実装できる。

---

## 13.1 モデル・ビュー・コントローラ（MVC）アーキテクチャ

これまでのGUIプログラミングでは、画面の見た目（ビュー）と、ボタンが押されたときの処理（ロジック）を1つのクラスにまとめて記述してきました。しかし、アプリケーションが複雑になると、この方法ではコードが非常に見通し悪く、修正が困難になります。

そこで、GUIアプリケーションの設計で広く用いられるのが**MVC（Model-View-Controller）**という設計パターンです。MVCは、アプリケーションを以下の3つの役割に分割します。

1.  **Model（モデル）**: アプリケーションの核となるデータと、そのデータを操作するビジネスロジックを担当します。ビューやコントローラについては何も知りません。
2.  **View（ビュー）**: 画面表示を担当します。モデルからデータを取得して表示しますが、データの加工やビジネスロジックは行いません。
3.  **Controller（コントローラ）**: ユーザーからの入力（イベント）を受け取り、それに応じてモデルを更新したり、ビューの表示を切り替えたりする「司令塔」の役割を担います。

この3つを分離することで、それぞれの独立性が高まり、変更やテストが容易になります。たとえば、見た目（View）だけを変更したい場合、ModelやControllerのコードに触る必要がありません。

Swingの`JList`や`JTable`といった高度なコンポーネントは、このMVCアーキテクチャが色濃く反映された設計になっています。

## 13.2 `JList`: 項目の一覧を表示する

`JList`は、複数の項目を一覧表示するためのコンポーネントです。ファイルリストや、メールの件名一覧など、さまざまな場所で使われます。

### `JList`と`DefaultListModel`

`JList`は、表示（View）そのものを担当し、リストのデータ（Model）は`ListModel`インターフェイスを実装したクラスが管理します。最も簡単に使えるモデルが`DefaultListModel`です。

```java
import javax.swing.*;
import java.awt.*;

public class JListExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JList Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250);
        frame.setLayout(new BorderLayout());

        // 1. Modelの作成 (データを管理する)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("りんご");
        listModel.addElement("バナナ");
        listModel.addElement("みかん");

        // 2. Viewの作成 (モデルを渡して表示する)
        JList<String> fruitList = new JList<>(listModel);

        // --- 操作用パネル ---
        JPanel controlPanel = new JPanel(new FlowLayout());
        JTextField inputField = new JTextField(10);
        JButton addButton = new JButton("追加");
        JButton removeButton = new JButton("削除");
        controlPanel.add(inputField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);

        // 3. Controllerの役割 (イベント処理)
        // 追加ボタンの処理
        addButton.addActionListener(e -> {
            String newItem = inputField.getText();
            if (!newItem.isEmpty()) {
                listModel.addElement(newItem); // Modelを更新
                inputField.setText("");
            }
        });

        // 削除ボタンの処理
        removeButton.addActionListener(e -> {
            int selectedIndex = fruitList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex); // Modelを更新
            }
        });

        frame.add(new JScrollPane(fruitList), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```
この例では、`listModel`が**Model**、`fruitList`が**View**、ボタンの`ActionListener`が**Controller**の役割を担っています。重要なのは、**イベント処理では`JList`を直接操作するのではなく、`DefaultListModel`を更新している**点です。モデルが変更されると、ビューである`JList`は自動的に再描画されます。

## 13.3 `JTable`: 表形式のデータを扱う

`JTable`は、スプレッドシートのような表形式でデータを表示・編集するための、非常に強力なコンポーネントです。

### `JTable`と`DefaultTableModel`

`JTable`も`JList`と同様にMVCにもとづいており、表示（View）を`JTable`が、データ（Model）を`TableModel`が担当します。

```java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JTableExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JTable Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // 1. Modelの作成
        // ヘッダーの定義
        String[] columnNames = {"名前", "年齢", "部署"};
        // データの定義
        Object[][] data = {
            {"山田 太郎", 30, "開発部"},
            {"佐藤 花子", 25, "営業部"},
            {"鈴木 一郎", 35, "人事部"}
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // 2. Viewの作成
        JTable table = new JTable(tableModel);

        // --- 操作用パネル ---
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("行を追加");
        
        // 3. Controllerの役割
        addButton.addActionListener(e -> {
            // Modelに行を追加
            tableModel.addRow(new Object[]{"新人 幸子", 22, "研修中"});
        });
        controlPanel.add(addButton);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

## まとめ

本章では、`JList`や`JTable`といった、より複雑なデータを扱うための高度なGUIコンポーネントについて学びました。

-   複雑なGUIアプリケーションを構築する際には、**MVCアーキテクチャ**にもとづいて役割を分離することが重要です。
-   Swingの高度なコンポーネントは、表示を担当する**View**（`JList`, `JTable`）と、データを管理する**Model**（`DefaultListModel`, `DefaultTableModel`）に分かれています。
-   イベント処理（**Controller**）では、Viewを直接操作するのではなく、**Modelを更新**することで、間接的にViewの表示を変化させます。

このMVCの考え方を身につけることで、変更に強く、テストしやすい、プロフェッショナルなGUIアプリケーションを構築するための基礎が固まります。
