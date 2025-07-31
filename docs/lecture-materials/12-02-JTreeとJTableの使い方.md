---
title: JTreeとJTableの使い方
---

>オブジェクト指向プログラミングおよび演習1 第12回  
>
>課題で使用するコンポーネントを理解しましょう。

## JTree

JTreeは、階層的なデータをツリー形式で表示するためのSwingコンポーネントです。
ファイルシステムのフォルダ構造のようなUIを構築する際に使用されます。

ツリー状で表現可能なデータ形式と相性が良く、組織図などのデータ表現に用いられることもあります。

### JTreeを構成する主要なクラス

JTreeを利用するには、主に以下のクラスの役割を理解する必要があります。

#### TreeNode と DefaultMutableTreeNode

`TreeNode` は、ツリー内の各項目（ノード）を表現するためのインターフェイスです。
一般的には、このインターフェイスを実装した `DefaultMutableTreeNode` クラスを利用します。このクラスは、任意のオブジェクトをデータとして保持でき、子ノードを追加することで階層を構築する機能を提供します。

#### TreeModel と DefaultTreeModel

`TreeModel` は、JTreeに表示するデータ全体の構造を管理するモデルです。JTreeコンポーネントは、このモデルから情報を取得して自身を描画します。
通常は、`DefaultTreeModel` クラスを使用します。これは`TreeNode`の階層構造をラップし、`TreeModel`として扱えるようにするものです。

#### JTree

`JTree` は、`TreeModel`が管理するデータを画面上に実際に描画するコンポーネント本体（ビュー）です。

### JTreeの基本的な使い方

1.  **ノードの作成**: `DefaultMutableTreeNode`のインスタンスを、表示したいデータごとに作成します。
2.  **階層の構築**: 親となるノードの`add()`メソッドを呼び出し、子ノードを追加して階層を定義します。
3.  **JTreeの生成**: 構築した階層のルートノードをコンストラクタに渡して`JTree`インスタンスを生成します。
4.  **スクロールペインへの配置**: JTreeは`JScrollPane`に配置することが推奨されます。これにより、データが多い場合にスクロールバーが自動的に表示されます。
5.  **イベント処理**: `TreeSelectionListener`を登録することで、ユーザーがノードを選択した際のイベントを処理できます。

### JTreeのサンプルコード

```java
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class JTreeExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // フレームの作成
            JFrame frame = new JFrame("JTree サンプル");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 400);

            // ルートノードの作成
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("買い物リスト");

            // カテゴリノードの作成
            DefaultMutableTreeNode categoryFoods = new DefaultMutableTreeNode("食料品");
            DefaultMutableTreeNode categoryBooks = new DefaultMutableTreeNode("書籍");

            // 階層の構築 (ルートにカテゴリを追加)
            root.add(categoryFoods);
            root.add(categoryBooks);

            // 商品ノードを各カテゴリに追加
            categoryFoods.add(new DefaultMutableTreeNode("牛乳"));
            categoryFoods.add(new DefaultMutableTreeNode("パン"));
            categoryFoods.add(new DefaultMutableTreeNode("卵"));

            categoryBooks.add(new DefaultMutableTreeNode("Java入門"));
            categoryBooks.add(new DefaultMutableTreeNode("アルゴリズム図鑑"));

            // JTreeの生成
            JTree shoppingTree = new JTree(root);

            // イベントリスナーの設定 (選択されたノード名を出力)
            shoppingTree.addTreeSelectionListener(e -> {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) shoppingTree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    System.out.println("選択された項目: " + selectedNode.getUserObject());
                }
            });

            // JScrollPaneにJTreeを配置してフレームに追加
            JScrollPane scrollPane = new JScrollPane(shoppingTree);
            frame.add(scrollPane);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

-----

## JTable

JTableは、データを2次元の表形式で表示するためのコンポーネントです。

JTableを効果的に利用するには、そのデータモデルである`TableModel`、とくに`AbstractTableModel`の理解が重要です。

Excelライクな見た目もあって、表形式のデータ編集ツールなどでも使用されます。

### SwingのMVCアーキテクチャ

JTableは、SwingのMVC（Model-View-Controller）アーキテクチャの典型例です。

  * **Model (モデル)**: `TableModel`がこの役割を担います。テーブルのデータ（行数、列数、各セルの値、列名など）を完全に管理します。
  * **View (ビュー)**: `JTable`がこの役割を担います。`TableModel`からデータを取得し、画面描画に専念します。
  * **Controller (コントローラ)**: ユーザーによるソートやセルの編集といった操作を受け付け、モデルに伝達する役割を担います。

この関心の分離により、データの管理ロジックと、その見た目を独立して開発することが可能になります。

### AbstractTableModelの役割

`TableModel` はインターフェイスであり、実装には多くのメソッド定義が必要です。`AbstractTableModel` は`TableModel`インターフェイスを実装した抽象クラスで、イベント通知など定型的な処理を実装済みです。

開発者は`AbstractTableModel`を継承し、最低限、以下の3つの抽象メソッドを実装するだけで、独自のデータモデルを効率的に作成できます。

  * `public int getRowCount()`: テーブルの総行数を返します。
  * `public int getColumnCount()`: テーブルの総列数を返します。
  * `public Object getValueAt(int rowIndex, int columnIndex)`: 指定された行・列に表示するデータを返します。

加えて、列のヘッダー（見出し）を適切に表示するために`getColumnName(int column)`メソッドもオーバーライドすることが一般的です。

### データの更新とイベント通知

`AbstractTableModel`が持つ重要な機能として、イベント通知機構が挙げられます。モデルが内部で保持しているデータが変更された際、その変更をビューである`JTable`に通知し、表示の再描画を促す必要があります。この通知のために、以下のメソッドを使用します。

  * `fireTableDataChanged()`: データ全体が根本的に変更されたことを通知します。テーブル全体が再描画されます。
  * `fireTableRowsInserted(int firstRow, int lastRow)`: 指定した範囲に行が挿入されたことを通知します。
  * `fireTableCellUpdated(int row, int column)`: 特定のセルの値が更新されたことを通知します。

### JTableとAbstractTableModelのサンプルコード

`Product`レコードで商品データを定義し、`ProductTableModel`で管理して`JTable`に表示する例です。ボタン操作でデータを追加し、`fireTableDataChanged()`でテーブル表示が更新される仕組みを示します。

```java
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 1. 表示するデータの構造を定義 (Recordを使用)
record Product(String id, String name, int price) {}

// 2. AbstractTableModelを継承したカスタムモデルクラス
class ProductTableModel extends AbstractTableModel {

    // 列のヘッダー名を定義
    private final String[] columnNames = {"商品ID", "商品名", "価格"};
    // テーブルに表示するデータの実体を保持するリスト
    private final List<Product> productList;

    public ProductTableModel(List<Product> productList) {
        this.productList = productList;
    }

    // --- AbstractTableModelの必須メソッドの実装 ---

    @Override
    public int getRowCount() {
        return productList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = productList.get(rowIndex);
        switch (columnIndex) {
            case 0: return product.id();
            case 1: return product.name();
            case 2: return product.price();
            default: return null;
        }
    }

    // --- 列ヘッダー表示のための推奨メソッドの実装 ---

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}


public class JTableExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JTable & AbstractTableModel サンプル");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);

            // 初期データとテーブルモデルの準備
            List<Product> initialProducts = new ArrayList<>();
            initialProducts.add(new Product("A-001", "ノートPC", 150000));
            initialProducts.add(new Product("B-001", "キーボード", 8000));
            ProductTableModel tableModel = new ProductTableModel(initialProducts);

            // JTableの生成
            JTable productTable = new JTable(tableModel);

            // JScrollPaneにJTableを配置
            JScrollPane scrollPane = new JScrollPane(productTable);

            // データを追加するためのボタンを作成
            JButton addButton = new JButton("商品を追加");
            addButton.addActionListener(e -> {
                // モデルが保持するリストにデータを直接追加
                int newCount = initialProducts.size() + 1;
                initialProducts.add(new Product("C-00" + newCount, "新しい商品", 1000 * newCount));

                // モデルのデータが変更されたことをビュー(JTable)に通知
                tableModel.fireTableDataChanged();
            });

            // フレームにコンポーネントを配置
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.getContentPane().add(addButton, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```