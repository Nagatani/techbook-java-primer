#!/bin/bash

# ビルドスクリプト for Calculator App
# 使用方法: ./build.sh

echo "========================================="
echo "Calculator App Build Script"
echo "========================================="

# 変数定義
SRC_DIR="."
BUILD_DIR="build"
DIST_DIR="dist"
JAR_NAME="calculator.jar"
MAIN_CLASS="chapter23.solutions.CalculatorApp"

# 色付き出力の定義
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 成功/失敗メッセージ関数
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 1. クリーン処理
echo "1. クリーン処理..."
if [ -d "$BUILD_DIR" ]; then
    rm -rf "$BUILD_DIR"
    print_success "ビルドディレクトリを削除しました"
fi

if [ -d "$DIST_DIR" ]; then
    rm -rf "$DIST_DIR"
    print_success "配布ディレクトリを削除しました"
fi

# ディレクトリ作成
mkdir -p "$BUILD_DIR"
mkdir -p "$DIST_DIR"
print_success "ディレクトリを作成しました"

# 2. コンパイル
echo ""
echo "2. Javaソースファイルのコンパイル..."
javac -d "$BUILD_DIR" -encoding UTF-8 CalculatorApp.java

if [ $? -eq 0 ]; then
    print_success "コンパイルが成功しました"
else
    print_error "コンパイルに失敗しました"
    exit 1
fi

# 3. JARファイルの作成
echo ""
echo "3. JARファイルの作成..."
cd "$BUILD_DIR"
jar cfm "../$DIST_DIR/$JAR_NAME" ../manifest.txt chapter23/solutions/*.class
cd ..

if [ $? -eq 0 ]; then
    print_success "JARファイルを作成しました: $DIST_DIR/$JAR_NAME"
else
    print_error "JARファイルの作成に失敗しました"
    exit 1
fi

# 4. JARファイルの情報表示
echo ""
echo "4. JARファイルの情報:"
echo "----------------------------------------"
jar tf "$DIST_DIR/$JAR_NAME"
echo "----------------------------------------"
echo "ファイルサイズ: $(ls -lh $DIST_DIR/$JAR_NAME | awk '{print $5}')"

# 5. 動作テスト
echo ""
echo "5. 動作テストの実行..."
echo "----------------------------------------"

# テスト1: 加算
echo "テスト1: 加算 (10 + 20)"
java -jar "$DIST_DIR/$JAR_NAME" add 10 20
if [ $? -eq 0 ]; then
    print_success "加算テスト成功"
else
    print_error "加算テスト失敗"
fi

# テスト2: 乗算
echo ""
echo "テスト2: 乗算 (5.5 × 3.2)"
java -jar "$DIST_DIR/$JAR_NAME" multiply 5.5 3.2
if [ $? -eq 0 ]; then
    print_success "乗算テスト成功"
else
    print_error "乗算テスト失敗"
fi

# テスト3: 除算
echo ""
echo "テスト3: 除算 (100 ÷ 3)"
java -jar "$DIST_DIR/$JAR_NAME" divide 100 3
if [ $? -eq 0 ]; then
    print_success "除算テスト成功"
else
    print_error "除算テスト失敗"
fi

# テスト4: ゼロ除算（エラーテスト）
echo ""
echo "テスト4: ゼロ除算（エラーテスト）"
java -jar "$DIST_DIR/$JAR_NAME" divide 10 0
if [ $? -ne 0 ]; then
    print_success "ゼロ除算エラー処理成功"
else
    print_error "ゼロ除算エラー処理失敗"
fi

# 6. ビルド完了
echo ""
echo "========================================="
echo -e "${GREEN}ビルドが完了しました！${NC}"
echo "実行可能JARファイル: $DIST_DIR/$JAR_NAME"
echo ""
echo "使用方法:"
echo "  java -jar $DIST_DIR/$JAR_NAME [操作] [数値1] [数値2]"
echo ""
echo "例:"
echo "  java -jar $DIST_DIR/$JAR_NAME add 10 20"
echo "========================================="