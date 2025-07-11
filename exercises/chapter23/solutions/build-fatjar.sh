#!/bin/bash

# Fat JAR ビルドスクリプト for JSON Tool
# 外部ライブラリを含む実行可能JARを作成

echo "========================================="
echo "JSON Tool Fat JAR Build Script"
echo "========================================="

# 変数定義
SRC_DIR="."
BUILD_DIR="build"
DIST_DIR="dist"
LIB_DIR="lib"
TEMP_DIR="temp"
JAR_NAME="json-tool.jar"
MAIN_CLASS="chapter23.solutions.JsonTool"

# 必要なライブラリのURL（実際の使用時は最新版を確認）
GSON_URL="https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
COMMONS_CLI_URL="https://repo1.maven.org/maven2/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar"

# 色付き出力
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 1. クリーン処理
echo "1. クリーン処理..."
rm -rf "$BUILD_DIR" "$DIST_DIR" "$TEMP_DIR"
mkdir -p "$BUILD_DIR" "$DIST_DIR" "$LIB_DIR" "$TEMP_DIR"
print_success "ディレクトリを初期化しました"

# 2. ライブラリのダウンロード（存在しない場合）
echo ""
echo "2. 依存ライブラリの準備..."

if [ ! -f "$LIB_DIR/gson.jar" ]; then
    echo "Gsonをダウンロード中..."
    curl -L "$GSON_URL" -o "$LIB_DIR/gson.jar"
    if [ $? -eq 0 ]; then
        print_success "Gsonをダウンロードしました"
    else
        print_error "Gsonのダウンロードに失敗しました"
        echo "手動でダウンロードして $LIB_DIR/gson.jar に配置してください"
        exit 1
    fi
else
    print_success "Gsonは既に存在します"
fi

if [ ! -f "$LIB_DIR/commons-cli.jar" ]; then
    echo "Commons CLIをダウンロード中..."
    curl -L "$COMMONS_CLI_URL" -o "$LIB_DIR/commons-cli.jar"
    if [ $? -eq 0 ]; then
        print_success "Commons CLIをダウンロードしました"
    else
        print_error "Commons CLIのダウンロードに失敗しました"
        echo "手動でダウンロードして $LIB_DIR/commons-cli.jar に配置してください"
        exit 1
    fi
else
    print_success "Commons CLIは既に存在します"
fi

# 3. コンパイル
echo ""
echo "3. ソースコードのコンパイル..."
javac -cp "$LIB_DIR/*" -d "$BUILD_DIR" -encoding UTF-8 JsonTool.java

if [ $? -eq 0 ]; then
    print_success "コンパイルが成功しました"
else
    print_error "コンパイルに失敗しました"
    exit 1
fi

# 4. Fat JARの作成
echo ""
echo "4. Fat JARの作成..."

# 一時ディレクトリにすべてのクラスを展開
cd "$TEMP_DIR"

# 自作クラスをコピー
cp -r ../"$BUILD_DIR"/* .

# ライブラリを展開
for jar in ../"$LIB_DIR"/*.jar; do
    echo "展開中: $(basename $jar)"
    jar xf "$jar"
done

# METAINFの競合を解決
rm -rf META-INF/*.SF META-INF/*.DSA META-INF/*.RSA

# マニフェストファイルを作成
cat > META-INF/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: $MAIN_CLASS
Created-By: JSON Tool Build Script
Implementation-Title: JSON Tool
Implementation-Version: 1.0
EOF

# JARファイルを作成
jar cf ../"$DIST_DIR"/"$JAR_NAME" *
cd ..

if [ $? -eq 0 ]; then
    print_success "Fat JARを作成しました: $DIST_DIR/$JAR_NAME"
else
    print_error "Fat JARの作成に失敗しました"
    exit 1
fi

# 5. クリーンアップ
rm -rf "$TEMP_DIR"

# 6. ファイルサイズの確認
echo ""
echo "5. ビルド結果:"
echo "----------------------------------------"
ls -lh "$DIST_DIR/$JAR_NAME"
echo "----------------------------------------"

# 7. 動作テスト
echo ""
echo "6. 動作テストの実行..."

# テスト用のJSONファイルを作成
cat > test.json << 'EOF'
{
  "users": [
    {"id": 1, "name": "Alice", "age": 25},
    {"id": 2, "name": "Bob", "age": 30},
    {"id": 3, "name": "Charlie", "age": 35}
  ]
}
EOF

# ヘルプ表示
echo "テスト1: ヘルプ表示"
java -jar "$DIST_DIR/$JAR_NAME" --help
echo ""

# JSON検証
echo "テスト2: JSON検証"
java -jar "$DIST_DIR/$JAR_NAME" validate test.json
if [ $? -eq 0 ]; then
    print_success "JSON検証テスト成功"
else
    print_error "JSON検証テスト失敗"
fi

# テストファイルのクリーンアップ
rm -f test.json

# 8. 使用方法の表示
echo ""
echo "========================================="
echo -e "${GREEN}ビルドが完了しました！${NC}"
echo ""
echo "実行可能JAR: $DIST_DIR/$JAR_NAME"
echo ""
echo "使用例:"
echo "  # JSONファイルの整形"
echo "  java -jar $DIST_DIR/$JAR_NAME format input.json output.json"
echo ""
echo "  # JSONファイルの検証"
echo "  java -jar $DIST_DIR/$JAR_NAME validate data.json"
echo ""
echo "  # JSONからCSVへの変換"
echo "  java -jar $DIST_DIR/$JAR_NAME convert --from json --to csv data.json data.csv"
echo "========================================="