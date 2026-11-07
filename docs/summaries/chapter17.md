---
source: manuscripts/chapter17-gui-basics.md
summarized: 2026-11-07
sections:
  - key: "本章の学習目標"
    hash: e603fde1
  - key: "章の構成"
    hash: 9a6f1624
  - key: "学習の進め方"
    hash: bb42cf3a
  - key: "Part A: 基本コンポーネントとレイアウト"
    hash: 4500c8f7
  - key: "Part B: イベント処理入門"
    hash: 0a67cf35
  - key: "Part C: EDT（Event Dispatch Thread）とスレッド安全性"
    hash: 6d9dec85
  - key: "Part D: カスタムコンポーネントの作成"
    hash: d8af5468
  - key: "章末演習"
    hash: 11ec6752
  - key: "よくあるエラーと対処法"
    hash: 9f86c4e5
---

# 第17章 GUIの基礎 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter17-gui-basics.md` を参照する。

## 章の概要

- Swingで画面を組み立てる基礎を教える。JFrameによるウィンドウ表示、JLabel・JButton・テキスト入力・選択系の各コンポーネント、BorderLayout・GridLayout・FlowLayout、JPanelによる入れ子レイアウトが中心
- 後半はActionListenerとラムダ式によるイベント処理、EDTとSwingWorker・javax.swing.Timer、カスタムコンポーネントの断片的な実装パターン、GUIのエラー集
- 前提は第11章のラムダ式（Part Bで使用）と第15章のマルチスレッド（Part CのEDT・volatile）。ただし前提知識の文では第15章を明示していない
- 旧第18章の分冊ファイル（chapter18a〜18e）を結合した構成で、`<!-- Merged from chapter18x-... -->`のコメントが各パートの境目に残っている

## 構成上の問題

この章は、旧版の分冊ファイル5つを結合したまま整理されていない。

- 冒頭の「章の構成」（L28–50）が結合前の目次のまま
    + リンク先が存在しない`chapter18a-basic-components.md`などの旧ファイル名
    + 「Part B: カスタムコンポーネント」「Part C: 章末演習」と書くが、実際はPart B=イベント処理入門、Part C=EDT、Part D=カスタムコンポーネントで、章末演習はパートではない
    + 「学習の進め方」（L51–62）も同じ旧構成で書かれている
- 第18章との重複（イベント処理の先取りが大きい）
    + Part B（L871–1378）のイベント駆動の概念・3要素・ボタンクリック・匿名クラス→ラムダ式の流れは、第18章の「イベント駆動プログラミングの理解」「ボタンクリックの実装」「ラムダ式による簡潔な記述」とほぼ同じ内容
    + 17-19のチェックボックス・ラジオボタンの選択状態処理は、第18章のItemListener節と重なる
    + Part C（L1379–1951）のEDT・invokeLater・SwingWorkerは、第18章の「EDT」「イベント処理のベストプラクティス」と、第19章の「SwingWorkerによる非同期処理」と重複する
    + 第17章を「画面の組み立て」に絞り、Part Bは第18章へ、Part CのSwingWorkerは第19章へ寄せるのが自然。EDTはinvokeLaterの定型だけを第17章に残す案がある
- 章内の二重説明
    + BorderLayoutを「複数のコンポーネントを配置する」（17-4、L232–276）と「レイアウトマネージャーの基礎」（17-9、L513–562）で2回説明し、領域の特性リストは3回（L267–274、L519–522、L559–562）出てくる
    + 17-1の解説が①〜⑤の箇条書き（L92–103）と番号付きリスト（L105–116）で二重になっている
    + GridLayout・FlowLayoutも特徴リストのあとに同じ説明の1文が続く（L576、L622）
    + SwingWorkerの例が17-23と17-26の2つあり、どちらも進捗バーを更新する同種の例
    + 各パート末尾に「まとめ」（H4）が残り、「次のパートでは…」と旧構成を前提に書かれている
- EDTの扱いの矛盾
    + 17-1〜17-17、17-19、17-20はmainメソッドで直接GUIを作るが、17-21は同じ書き方を「間違った方法」とコメントし、17-25では「必ずEDT上でGUIを初期化する」と述べる
    + 17-18（L1100）はEDTを説明する前にinvokeLaterに「注意してください」と触れている
- Part D（L1952–2058）は基本の実装例が欠落している
    + JComponentを継承してpaintComponentで描く基本例がなく、基底クラスの選択（17-29）からいきなり最適化・アクセシビリティ・独自リスナーの断片コード（17-30〜17-32）になる
    + Part Dのまとめは「マウスイベントやキーボードイベントの処理」「ActionListenerパターン」を学んだとするが、本文にない
- 学習目標と本文の不一致
    + MouseListener・KeyListenerの実装を目標に挙げるが、本文で扱うのはActionListenerだけ（第18章で扱う内容）
    + 「ペイントシステムの理解」も本文で説明していない
    + 前提知識で「イベント処理の基本概念を理解していれば準備完了」とするが、イベント処理は本章と第18章で初めて学ぶ

## 節ごとの要約

- 本章の学習目標（L3–27）
    + GUIの基本概念（イベント駆動・Swingアーキテクチャ・EDT）、基本コンポーネント、3つのレイアウトマネージャー、イベント処理の基礎の4項目
    + MouseListener・KeyListener・ペイントシステムは本文で扱っていない
- 章の構成（L28–50）
    + 旧分冊ファイルへのリンクと旧パート構成。実際の構成と食い違う
- 学習の進め方（L51–62）
    + Part A→B→Cの順で学ぶよう勧め、第18章のイベント処理へつなぐ。旧構成のまま
- Part A: 基本コンポーネントとレイアウト（L63–870）
    + JFrameで最小のウィンドウを表示し、setSize・setDefaultCloseOperation・setLocationRelativeTo・setVisibleの役割を説明（17-1）。setResizableや絶対位置など詳細設定（17-2）
    + Swingコンポーネント継承階層の図（figure17-01）、JLabelの表示（17-3）、BorderLayoutの5領域（17-4）
    + JLabelのHTML表示（17-5）、JButtonの色・サイズ・無効化（17-6）
    + JTextField・JPasswordField・JTextArea・JSpinner・JSliderの入力系（17-7）、JCheckBox・ButtonGroup付きJRadioButton・JComboBox・JListの選択系（17-8）
    + BorderLayout（17-9）、GridLayout（17-10）、FlowLayout（17-11）の特徴と例
    + JPanelの用途とエディタ風画面（17-12）、メニューバー・JSplitPane・JTabbedPaneを含むIDE風画面（17-13）
    + パート末尾に旧版の「まとめ」が残る
- Part B: イベント処理入門（L871–1378）
    + イベント駆動の考え方と、イベントソース・イベントオブジェクト・リスナーの3要素。イベント処理モデルの図（figure17-02）
    + 匿名クラスのActionListenerでボタンクリックを処理（17-14）、JTextFieldの入力をラベルに表示（17-15）
    + 匿名クラスとラムダ式の比較（17-16）、大文字・小文字変換アプリ（17-17）
    + カウンタアプリ（17-18）、isSelectedによる選択状態の処理（17-19）、複数の入力部品を組み合わせたフォーム（17-20）
    + 内容のほとんどが第18章前半と重複する
- Part C: EDT（Event Dispatch Thread）とスレッド安全性（L1379–1951）
    + EDTの役割とSwingがスレッドセーフでないこと。invokeLaterによる初期化（17-21）、isEventDispatchThreadでの確認（17-22）
    + SwingWorkerのdoInBackground・process・done（17-23）、invokeLaterとinvokeAndWaitの違い（17-24）
    + Look&Feel設定を含む初期化パターン（17-25）、publishとsetProgressによる進捗表示（17-26）
    + javax.swing.Timerのデジタル時計（17-27）、volatileフラグと別スレッドからのUI更新（17-28）
- Part D: カスタムコンポーネントの作成（L1952–2058）
    + 基底クラスの選択（JComponent・JButton・JPanel、17-29）
    + needsRepaintフラグとcontainsのオーバーライド（17-30）、フォーカスとAccessibleContext（17-31）、独自リスナーのadd/remove/fire（17-32）
    + いずれも断片コードで、基本の描画例がない
- 章末演習（L2059–2082）
    + 演習リンクが`exercises/chapter18/`を指している（Issue #010）
    + 「本章の基本概念の理解確認」などの汎用リストと、電卓・メモ帳・画像ビューアーの課題リストが並び、README.md参照の文が2回出てくる
    + 章の途中（エラー集の前）に置かれている
- よくあるエラーと対処法（L2083–2410）
    + コンポーネントが表示されない（17-33）、BorderLayoutの位置指定漏れ（17-34、17-35）
    + JPanelの背景色（17-36、17-37）、JTextFieldが編集できない（17-38、17-39）
    + AWT-EventQueue-0の例外とinvokeLater（17-40）
    + JTextAreaのサイズ（17-41、17-42）、JButtonのsetSizeとsetPreferredSize（17-43、17-44）
    + 画面のちらつき（17-45、17-46）、デバッグ用の境界線表示・レイアウト確認・EDT確認（17-47〜17-49）

## 他章との関係

- 第11章（ラムダ式）: Part Bのラムダ式によるリスナー記述の前提
- 第15章（マルチスレッド）: Part CのEDT・volatile・スレッド間通信の前提だが、本文から参照していない
- 第18章（基本的なイベント処理）: Part Bと前半3節がほぼ重複する。EDTとSwingWorkerの説明も重なる。第18章の学習目標は第17章を前提にしている
- 第19章（高度なイベント処理パターン）: SwingWorkerの節がPart Cの17-23・17-26と重複する
- 第20章（JTableとJListの基礎）: 17-8でJListを先に簡単に使う。paintComponentのエラー例は第20章のエラー集にあり、Part Dの内容と関係するが相互参照はない

## 校正で見つけた個別の問題

- L92、L122、L126、L173、L178、L219、L232、L265、L369、L436、L440、L444、L448、L515、L566、L612、L663: 「ボタンとラベル。」「チェックボックス。」のような、見出しだったと思われる語句が1文として残っている
- L122–126: 「ウィンドウの詳細設定例。」「基本的なウィンドウ。」のあとに、17-2と無関係なJFrame一般の説明が続く
- L171–181: 「JLabelで文字を表示する」の冒頭でボタンとラベルの両方を説明している
- L278–280: H4「基本コンポーネントの詳細な使用例」が本文なしで次のH4に続く
- L560–562: 項目名と説明が連結している（「NORTHとSOUTH画面の幅いっぱいに」など）。Issue #013
- L156–161、L373–376、L520–522: 「項目名 - 説明」「項目名: 説明」形式のリスト項目（階層化リストのルール違反）
- L108–116: リスト項目の文末に句点があり、L116はですます調
- L26: 前提知識に第15章（マルチスレッド）を挙げず、「イベント処理の基本概念」を前提にしている
- L1981–1990（17-30）: paintComponentで`needsRepaint`がfalseのとき描画を省くと、再描画要求時に何も描かれなくなる。最適化の例として技術的に誤り
- L2164: 「JPanelのデフォルトでは、背景が透明になることがある」は不正確。JPanelは標準のLook&Feelでは不透明が既定で、setOpaque(true)が必要なのは主にJLabelなど
- L2213–2223: NullPointerExceptionの原因に「EDT以外からのアクセス」を挙げるが、スタックトレースはAWT-EventQueue-0（EDT上）で発生しており、因果関係の説明がかみ合わない
- L2081: 「次のステップ： 基礎課題が完了したら」は項目名＋コロンの形
- L2070、L2079: 「詳細な課題内容と実装のヒントは…README.mdを参照してください」がほぼ同文で2回出てくる
