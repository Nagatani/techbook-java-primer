---
source: manuscripts/chapter17-gui-basics.md
summarized: 2026-11-14
sections:
  - key: "本章の学習目標"
    hash: fb3738a5
  - key: "基本コンポーネントとレイアウト"
    hash: 6ec1b0f5
  - key: "カスタムコンポーネントの作成"
    hash: c159ef8d
  - key: "よくあるエラーと対処法"
    hash: f7c62790
  - key: "まとめ"
    hash: ecfa8925
  - key: "章末演習"
    hash: d78bdb69
---

# 第17章 GUIプログラミングの基礎 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter17-gui-basics.md` を参照する。

## 章の概要

- Swingで画面を組み立てる方法に集中する。JFrameによるウィンドウ表示、JLabel・JButton・テキスト入力・選択系の各コンポーネント、BorderLayout・GridLayout・FlowLayout、JPanelによる入れ子レイアウト、paintComponentによる独自の描画
- イベント処理は第18章に任せる、と学習目標の直後で断る（2026-11-11に「イベント処理入門」と「EDT」節を第18章・第19章へ移した）
- EDTは「`SwingUtilities.invokeLater()`でGUIを組み立てる定型」だけを17-2で教え、17-2以降のサンプルはすべてこの形で書く。EDTの役割と理由は第18章へ誘導する
- 前提は第5章の継承、第7章のインターフェイス、第11章のラムダ式（invokeLaterに渡す）

## 構成上の問題

- 第18章・第19章との重複（イベント処理入門、EDT、SwingWorker）は、2026-11-11にそれぞれの章へ移して解消した
- EDTの扱いの矛盾（mainで直接GUIを作るサンプルと「必ずEDTで」という記述の食い違い）は、17-2で定型を示し以降のサンプルをそろえて解消した（2026-11-11）
- 「カスタムコンポーネントの作成」に基本の描画例がない問題は、断片コードをやめ17-15（JPanelを継承した基本の描画）を置いて解消した（2026-11-11）
- 学習目標と本文の不一致（MouseListener・KeyListener、ペイントシステム、イベント処理を前提にする記述）は、学習目標を書き直して解消した（2026-11-11）
- 章内の二重説明
    + BorderLayoutの領域の広がり方を17-5の解説で述べ、「レイアウトマネージャーの基礎」では図と17-10で再び示す。2026-11-11に17-10側は17-5の解説を参照する形にした
    + GridLayout・FlowLayoutは、特徴のリストや段落のあとに同じ内容の1文が続く（L578、L622）（2026-11-12に解消）
    + 17-3の前の「ウィンドウの基本設定」（L119–132）は、17-1の解説と同じJFrameの一般的な説明を繰り返す（2026-11-12に解消）
- 見出しだったと思われる語句が1文として残る箇所が多い（下の個別の問題を参照）（2026-11-12に解消）

## 節ごとの要約

- 本章の学習目標（L3–27）
    + GUIの基本（Swingの構成と階層、invokeLaterの定型）、基本コンポーネント、3つのレイアウトマネージャーとJPanelの入れ子、paintComponentによる独自の描画の4項目
    + イベント処理は第18章で学ぶと断る。前提に第5章・第7章・第11章を挙げる
- 基本コンポーネントとレイアウト（L28–773）
    + JFrameで最小のウィンドウを表示し、①〜⑤の各メソッドの役割を文章で説明（17-1）
    + 「GUIの組み立てはEDTに任せる」で、`SwingUtilities.invokeLater(() -> createAndShowGUI())`の定型を示す（17-2）。理由は第18章へ誘導
    + setResizableや絶対位置などの詳細設定（17-3）
    + Swingコンポーネント継承階層の図（chapter17-swing-hierarchy）。図の前後で、AWTの`Component`・`Container`、Swingの部品の基底`JComponent`、`Window`→`Frame`の系統の`JFrame`を文章で説明する
    + JLabelの表示（17-4）、レイアウトマネージャーの考え方とBorderLayoutの5領域（17-5、領域ごとの広がり方を文章で説明）
    + JLabelのHTML表示（17-6、見出しは「JLabelでHTMLを表示する」）、JButtonの色・サイズ・無効化（17-7）
    + JTextField・JPasswordField・JTextArea・JSpinner・JSliderの入力系（17-8）、JCheckBox・ButtonGroup付きJRadioButton・JComboBox・JListの選択系（17-9）。JListの詳細は第20章へ誘導
    + BorderLayout（図chapter17-borderlayout、17-10）、GridLayout（図chapter17-gridlayout、17-11）、FlowLayout（17-12）
    + JPanelの用途を文章で述べ、エディター風画面（17-13）、メニューバー・JSplitPane・JTabbedPaneを含むIDE風画面（17-14）。17-14の抜粋は`setJMenuBar`でメニューバーをフレームに付ける箇所と左側パネルの生成を含み、左側はJList（src・test・resources）
- カスタムコンポーネントの作成（L774–861）
    + 基底クラスの選択（JComponent・JPanel・既存のコンポーネント）をリストで示す
    + JPanelを継承したDrawingPanelで四角形・円・文字列を描く（17-15。前半がDrawingPanel.java、後半がDrawingPanelExample.javaの`createAndShowGUI()`と断る）。getPreferredSize、paintComponentが呼ばれる時機、`super.paintComponent(g)`、Graphicsの座標と色、repaintで再描画を頼むことを①〜④で説明
    + イベントと組み合わせた描画は第18章・第19章へ誘導
- よくあるエラーと対処法（L862–883）
    + 本文に挙げる問題: コンポーネントが表示されない、BorderLayoutでの重なり、JPanelの背景色、JTextFieldが編集できない、EDT以外からの更新、サイズが意図どおりにならない、独自の描画で画面がちらつく（JFrameの`paint()`や`getGraphics()`で描くとダブルバッファリングが働かない。`paintComponent()`と`repaint()`を使う）。詳細は`appendix/h-common-errors/chapters/chapter17.md`
- まとめ（L884–898）
    + 画面の組み立て、GUIの組み立て方（invokeLater）、カスタムコンポーネントの3項目。次章のイベント処理とEDTへつなぐ
- 章末演習（L899–921）
    + GitHubの`exercises/chapter17/`への案内と課題構成（基礎: プロフィールカード・電卓の画面・アンケートの入力画面・棒グラフ、発展: カレンダー・アナログ時計の文字盤、チャレンジ: 家計簿の画面）。ボタンの処理は第18章で学ぶため、どの課題も画面の表示までを作ると断る（2026-11-13に演習を用意した）

## 他章との関係

- 第5章・第7章: JFrame・JPanelの継承とリスナーのインターフェイスの前提
- 第11章（ラムダ式）: invokeLaterに渡すラムダ式の前提
- 第18章（基本的なイベント処理）: イベント処理とEDTの役割・理由を扱う。本章から複数箇所で誘導する
- 第19章: イベントと組み合わせた描画、SwingWorkerを扱う
- 第20章（JTable・JList・JTree）: 17-9でJListを先に簡単に使い、詳細は第20章へ誘導する

## 校正で見つけた個別の問題

- L123、L127、L179、L184、L381、L440、L444、L448、L452、L562、L566、L618: 「ウィンドウの詳細設定例。」「ボタンとラベル。」「チェックボックス。」のような、見出しだったと思われる語句が1文として残っている（2026-11-12に解消）
- L123–132: 「ウィンドウの詳細設定例。」「基本的なウィンドウ。」のあとに、17-3と無関係なJFrame一般の説明が続く（2026-11-12に解消）
- L179–187: 「JLabelで文字を表示する」の冒頭でボタンとラベルの両方を説明している（2026-11-12に解消。JButtonの説明は17-7の前へ移した）
- L282: H4「基本コンポーネントの詳細な使用例」が本文なしで次のH4に続く（2026-11-12に見出しを削除して解消）
- L162–167、L385–388: 「① タイトルバー文字 - 説明」「JTextField: 説明」形式のリスト項目（階層化リストのルール違反）（2026-11-12に解消）
- L385–388: JFormattedTextFieldを挙げるが、17-8では使わず、代わりにJSpinner・JSliderを使う（2026-11-12にリストをJSpinner・JSliderに合わせて解消）
- L872–873: 「JPanelの背景色が反映されない」の原因を`setOpaque(true)`とするのは不正確。JPanelは標準のLook&Feelでは不透明が既定で、setOpaque(true)が必要なのは主にJLabelなど（2026-11-12に本文と付録HをJLabelの話に改めて解消）
- 17-30の`needsRepaint`の誤りは、断片コードを削除して解消した（2026-11-11に解消）
- 2026-11-14の校閲で見つけ、同日に解消したもの
    + 17-11: 同じコメントが2行続いていた
    + 17-13・17-14: 画面の文字列「エディタ」が本文の「エディター」と合わず、長音のルールにも反していた
    + 17-14: コメント「上部：メニューバーとツールバー」が実際（メニューバーはsetJMenuBar）と違い、抜粋に`leftPanel`の定義がなかった。左側の`JTree`が実行結果の説明（src/test/resourcesのリスト）と違っていた
    + 継承階層の図に説明がなかった。見出し「JLabelとアイコンの活用」の例にアイコンがなかった
    + JPanelの節で同じ内容の文が2つ続いていた。17-8の実行結果の「スクロールバーが付き」は既定の表示（AS_NEEDED）と違っていた
    + 「画面のちらつき」の原因を「追加・削除が1つずつ画面に反映される」としていたのは誤り。本文と付録Hを、`paint()`・`getGraphics()`で描くとダブルバッファリングが働かない話に改めた
- 図chapter17-gridlayoutは2行3列で、直後の17-11（3行2列）と食い違っていた（2026-11-14に解消。図を17-11と同じ3行2列のボタンにした）
- 未解決: 継承階層の図は「基本的なコンポーネントの配置」の冒頭にある。もっと前へ移すかどうかは著者の判断待ち
