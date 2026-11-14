#!/bin/bash
# Fat JAR作成スクリプト
#
# 前提（このスクリプトを実行するディレクトリの構成）
#   ./lib/gson-2.15.0.jar      外部ライブラリ（Maven Centralから取得する）
#   ./JsonProcessorApp*.class  examples/JsonProcessorApp.javaをコンパイルしたもの
#
# 準備の例
#   cp examples/JsonProcessorApp.java .
#   javac -cp lib/gson-2.15.0.jar JsonProcessorApp.java
#   bash scripts/create-fat-jar.sh
#   java -jar JsonProcessorApp-all.jar

# コマンドが1つでも失敗したら、その場で止める
set -eu

APP_DIR="$(pwd)"

# 1. 作業ディレクトリの準備（既存のディレクトリと重ならない一時ディレクトリを作る）
WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT

# 2. 外部ライブラリ（gson-2.15.0.jar）を展開
(cd "$WORK_DIR/" && jar xf "$APP_DIR/lib/gson-2.15.0.jar")

# 3. アプリケーションのクラスファイルをコピー
cp "$APP_DIR"/JsonProcessorApp*.class "$WORK_DIR/"

# 4. マニフェストファイルの作成（JARに入らないよう作業ディレクトリの外に置く）
MANIFEST="$(mktemp)"
trap 'rm -rf "$WORK_DIR" "$MANIFEST"' EXIT
echo "Main-Class: JsonProcessorApp" > "$MANIFEST"

# 5. すべてを含むFat JARの作成（-C で作業ディレクトリの中身を入れる）
jar cfm "$APP_DIR/JsonProcessorApp-all.jar" "$MANIFEST" -C "$WORK_DIR" .

echo "作成しました: JsonProcessorApp-all.jar"
# 6. 一時ファイルは終了時（trap）に削除する
