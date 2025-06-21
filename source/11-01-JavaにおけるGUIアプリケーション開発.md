---
title: JavaにおけるGUIアプリケーション開発
---

>オブジェクト指向プログラミングおよび演習1 第11回
>
>JavaでのGUIアプリケーション開発についての概要をまとめた資料です

# 1. Java GUIアプリケーション開発のライブラリ

簡単な紹介の情報しか書いていませんので、詳しくはネットで検索するなどしてください。

## 1. アプレット - Java Applet
主に、Webを使用してJavaアプリケーションを配布する目的のライブラリです。  
最近のWebブラウザではほぼ使用できません。

※Appletは、セキュリティの観点から現時点ではすべてのブラウザの最新版で使用不可となっております。
Javaの開発元であるOracleも、Appletが動作するWebブラウザ用プラグインの更新は行っておらず、すべてにおいて非推奨です。

ただ、過去にAppletで開発したプログラムをリプレイスできずに居る一部企業では、使用可能なPCをずっと残してあり、それを使って開発を行っているところもあるにはあります。  
事情があるにせよ、Appletの実行環境はセキュリティホールだらけですので、使用は差し控えるのがよさそうです。

### 参考資料
- なし

（技術的に非推奨となっているため、過去の参考資料もインターネット上から削除されつつあります。）


## 2. Swing

JavaでのGUIアプリケーション開発と言えばこれです。
次の項目で説明するAWTをベースに、よりGUIプログラミングに特化するように拡張されたライブラリ群です。

`javax.swing.*` に含まれます。

Javaでは新しいGUI開発のライブラリが主流となりつつありますが、いまだにSwingでの開発を行っている企業も多いです。

どんなGUIアプリケーションが作れるかと言うと、IntelliJ IDEAがSwingをベースとしてできております。
（ただ、最上位の完成度ではあるので、参考にはならないかと思います。）

今回の講義にてサンプルを使用して解説します。
IntelliJ IDEAやNetBeansなどのIDEを使用すると、画面デザインに関してソースコードに触ることなく設定できます。

※Swingアプリケーションの作成しやすさの観点から見ると、NetBeansに軍配が上がります。（個人的見解です）
ただし、NetBeansの開発自体がOracleのサポート対象から外れてしまっており、継続的に使用できるかは怪しい状況です。

NetBeansのインストールについては、以下のGitHubリポジトリにてインストールスクリプトが配布されております。

- [carljmosca/netbeans-macos-bundle: NetBeans installation script for Mac OS X](https://github.com/carljmosca/netbeans-macos-bundle)

### 参考資料
- [JavaDrive - Swingを使ってみよう - Java GUIプログラミング](http://www.javadrive.jp/tutorial/)
- [創るJava NetBeansでつくって学ぶJava GUI & Webアプリケーション改訂第3版 - きしだなおき - Google ブックス](https://books.google.co.jp/books?id=NBDzb8eVO_QC)

## 3. AWT - Abstract Window Toolkit

Java独自のオープンプラットフォームのGUIライブラリ。
`java.awt.*`に含まれ、標準ライブラリ内にあります。

Swingはこれをベースしており、Swingアプリケーションを作成する際に、awtパッケージ内のクラスを使用する場合があります。

### 参考資料
- [とほほのJava入門](http://www.tohoho-web.com/java/awt.htm)

## 4. SWT - Standard Widget Toolkit

SWTはJavaでネイティブなGUIアプリケーションを開発するためのAPI群です。

SWTは、GUIの各コントロールの描写にJava VMを使用せず、OSのAPI（WindowsであればWin32Api）を呼び出すようにするパイプ処理を行います。  
そのためAWTやSwingに比べ、描写性能に優れています。（OSの機能を直接呼び出すので処理が速いと思ってもらえればよいです）

ただし、SWTはネイティブなAPIを呼び出す特性があるため、それぞれの環境に合わせたライブラリを持ってくる必要があります。
その点を言えば、Java本来のオープンプラットフォームの概念から若干逸脱しているように思います。

※SWTのプロジェクトを作成するには、IntelliJ IDEAやNetBeansよりEclipseの方が良いでしょう。

### 参考資料
- <a href="https://www.eclipse.org/swt/" target="_blank">SWT: The Standard Widget Toolkit | The Eclipse Foundation</a>

## 5. JavaFX

Java8より標準で組み込まれることになってJava11からはしごを外されたMVC（Model-View-Controller）構成のGUIアプリケーションライブラリ。

JavaFXの主な利点は、グラフィックス関連の処理をGPUに移譲できる点があり、グラフィカルなアプリケーションに強いです。

Swingの後継として開発されていたため、JavaFXコンポーネントとSwingコンポーネントにおいて相互で利用できるものもあります。
JavaFXには、IntelliJ IDEAで作成するSwingGUIフォームと同様に、画面の設定項目をFXMLというXMLを使用してGUIを作成し、Javaで書かれるControllerにて操作を行うMVCプログラミングを行います。

一時的に標準ライブラリとして採用されましたが、いまでは協力企業のサポートあってはじめて成立する状況であり、JavaSDKには最初から含まれていません。
JavaFXでの開発を行う場合には、下記協力企業作成のサイトからJavaFXライブラリをダウンロードして、外部ライブラリとして組み込む必要があります。

- <a href="https://openjfx.io/" target="_blank">JavaFX</a>

また、JavaFXには、Scene Builderという独自のデザイナアプリが存在しており、これらを使うとSwingに劣らない立派なGUIアプリが作成できます。

- <a href="https://gluonhq.com/products/scene-builder/" target="_blank">Scene Builder - Gluon</a>


### 参考資料
- [初心者のためのJavaFXプログラミング入門 - libro](http://libro.tuyano.com/index2?id=8356003)
- [Client Technologies: Java Platform, Standard Edition (Java SE) 8 Release 8](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)

# 2. GUIアプリケーションの開発における今後のスタンダード

今から作成されるJavaでのGUIアプリケーションならSwingか、JavaFX（またはWebアプリケーション）といったところです。

現状、業務アプリケーション開発の現場では、過去の資産を活用しているSwingやSWTのアプリケーションが大部分を占めていることでしょう。
（または、Spring Framework、Spring BootやApacheプロジェクトのなにかを使っているWebアプリケーションもあるかも。）

もうすでに作成されているアプリケーションをわざわざ標準から外れてしまったJavaFX,FXMLで書き換える等の作業はおそらく行われないでしょうから、新しいアプリケーションやシステムの開発時には、保守のしやすさの観点から、Webアプリケーションが主流となっていると思われます。



# JavaでのWebアプリ開発について

Javaの標準的なWebアプリケーションは、Webアプリケーションの動作環境であるServletコンテナを基準としたServlet/JSPあたりが基礎知識として必要です。  
（※JavaEE以外のライブラリを使用する前提であれば、Servlet/JSPの知識がなくても問題ありません）

サーブレット（Servlet）とは、Javaを使用して、サーバーサイドプログラムを作成する技術で、Java EEの代表的なしくみの1つです。

サーブレットの基底クラス（javax.servlet.http.HttpServlet）を継承して、サーブレットクラスを開発することで、Webアプリケーションサーバー上でそれらを実行できるようになります。 javax.servletパッケージは、JavaのSDKには含まれておらず、Java EE SDK（GlassFish Webアプリケーション・サーバー含む）に含まれており、Java EEをインストールしないと使用することはできません。

サーブレットクラスは、ブラウザからのリクエストによって実行され、実行結果をHTMLで出力しブラウザにレスポンスされます。

JSPとはJava Server Pagesの略で、実行される中身としてはサーブレットと同じですが、サーブレットクラスを作成しないで、その代わりにHTMLと似たJSPファイルを作成します。  
サーブレットクラスでは、HTMLを出力するのに、HttpServletResponce.getWriter()でストリームを取得し、それに対してprintlnなどで出力していました。

JSPファイルでは、基本的にHTMLファイルと同様の記述で作成する事ができます。PHPでの開発をしたことがある方はそれをイメージするとそのままです。

内部的には、JSPファイルはサーブレットクラスに変換されており、JSPファイルに対してリクエストを受け付けた場合、そのサーブレットクラスの実行結果としてHTMLをレスポンスしています。
現在のJavaにおけるWebアプリケーション開発は、Servlet/JSPをベースに発展としてBeansやJSFなどが広く使われています。

Javaでは、JavaEEのような開発キットを用いることでWebアプリケーションの開発が可能です。

## その他のWebアプリケーションフレームワーク

JavaEEの他には、以下のようなフレームワークも存在しています。

- <a href="https://spring.io/" target="_blank">Spring | Home</a>
    - おそらく一番使われているであろうWebアプリケーションフレームワーク
    - DI（依存性の注入）やAOP（アスペクト志向）などの仕組みが満載で、アノテーションが多用される開発手法を取っている
- <a href="https://struts.apache.org/" target="_blank">Welcome to the Apache Struts project</a>
    - L-Cam多分これ
    - 頻繁にセキュリティホールが見つかり、脆弱性がそのままになっていることもあるため、昨今では別のフレームワークへの移行が進んでいる
- <a href="https://www.playframework.com/" target="_blank">Play Framework - Build Modern & Scalable Web Apps with Java and Scala</a>
    - Ruby on Railsの影響を受けているフレームワーク
- <a href="https://sparkjava.com/" target="_blank">Spark Framework: An expressive web framework for Kotlin and Java</a>
    - RubyのWebフレームワークのSinatoraから影響を受けており、シンプルな構成を実現できる個人的に一押しのフレームワーク
- <a href="http://www.gwtproject.org/" target="_blank">[GWT]</a>
    - Google作のフレームワーク。誰が使っているのかわからんぐらい見たことない
- <a href="https://helidon.io/#/" target="_blank">Helidon Project</a>
    - Oracleが最近開発したマイクロサービス開発に特化したWebフレームワーク
    - 使いやすいのでオススメしたいところではあるが、資料が少なすぎる。
    - IntelliJ IDEA Ultimate（有料版）ではプラットフォームの選択肢の1つとして標準搭載のフレームワーク。

他にもありますが、一応こんなところです。

Webアプリを作成するには、HTML,CSS,JavaScriptの知識が別途必要になります。


# Androidアプリ開発について

Androidアプリの開発については、Kotlinという言語によって行われているものが一般的です。

AndroidはJavaVMで動いている、などと言う古いお話が広まっておりますが嘘です。
過去、JavaVMの互換のようなDalvikVMと呼ばれるもので動いていましたがAndroid 5.0からAndroid RunTime(ART)で動作しており内部的にはJavaVMと大きく異なります。（オープンソースではないためソースコードの中身が見られないず、Googleの主張を全面的に信用すれば。）

JavaのコードをAndroid用にコンパイルしたものは、JavaVMで動作させることはできません。
JavaVM向けにコンパイルしたクラスファイルなどは、そのままAndroidで動作させることはできません。

Androidアプリ開発がしたい場合には以下の選択肢があります。

1. Android Studioを使ってJavaもしくはKotlinで書く（iOS不可）
    - インストール方法は、[Android Studio のインストール  |  Android Developers](https://developer.android.com/studio/install?hl=ja) 参照
    - 開発のしやすさ、デバッグでの情報探索の容易さから、ダントツでオススメの開発環境です。
2. App Inventorを使ってブロックプログラミングを行う（iOS不可）
    - <a href="http://ai2.appinventor.mit.edu/" target="_blank">MIT App Inventor</a>
    - （最近iOSに対応した。正常に動くとは言っていない）
2. Flutterを使ってDartで書く（iOS可）
    - [Flutter - Beautiful native apps in record time](https://flutter.dev/)
3. HTML5,JavaScriptによるハイブリッドアプリ開発を行う（iOS可）
    - Apache Cordova, Monacaなどが有名どころ
4. PWA(Progressive Web Apps)でWebアプリを作る（iOS可）
    - 完全にWeb技術
5. .NET MAUI（.NET Multi-Platform App UI）（iOS可）
    - Visual Studio 2022またはVisual Studio Code（.NET MAUI拡張機能）に搭載された、ネイティブコンパイル可能なプラットフォーム
    - ネイティブアプリを書き出せることから昨今では広く使われ始めているが
    - 言語はC#、画面の構成にXAML（JavaFXのFXMLなどと同じくXMLベースの設定ファイル）を使用する

お好きなのをどうぞ

