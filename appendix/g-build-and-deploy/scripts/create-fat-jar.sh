#!/bin/bash
# Fat JAR作成スクリプト

# 1. 作業ディレクトリの準備
mkdir temp
cd temp

# 2. 外部ライブラリ（gson-2.10.1.jar）を展開
jar xf ../lib/gson-2.10.1.jar

# 3. アプリケーションのクラスファイルをコピー
cp ../JsonProcessorApp*.class .

# 4. マニフェストファイルの作成
echo "Main-Class: JsonProcessorApp" > manifest.txt
echo "" >> manifest.txt

# 5. すべてを含むFat JARの作成
jar cvfm ../JsonProcessorApp-all.jar manifest.txt .

# 6. 一時ディレクトリの削除
cd ..
rm -rf temp