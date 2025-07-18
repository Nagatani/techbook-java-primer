#!/bin/bash

# サンプルコード番号体系の自動付与スクリプト
# 使用法: ./add-sample-code-numbers.sh chapter番号 ファイルパス

if [ $# -ne 2 ]; then
    echo "使用法: $0 <章番号> <ファイルパス>"
    echo "例: $0 1 chapter01-introduction.md"
    exit 1
fi

CHAPTER_NUM=$1
FILE_PATH=$2

echo "第${CHAPTER_NUM}章のサンプルコード番号を自動付与中..."

# 一時ファイル作成
temp_file=$(mktemp)

# 番号カウンタ
counter=1

# 現在すでに番号が付いているものを抽出
existing_numbers=$(grep -o "サンプルコード${CHAPTER_NUM}-[0-9]\+" "$FILE_PATH" | grep -o "[0-9]\+$" | sort -n | tail -1)

if [ -n "$existing_numbers" ]; then
    counter=$((existing_numbers + 1))
    echo "既存の最大番号: $existing_numbers, 次の番号: $counter から開始"
fi

# ファイルを1行ずつ処理
in_code_block=false
needs_number=false

while IFS= read -r line; do
    # ```java の直前の行をチェック
    if [[ "$line" =~ ^\`\`\`java ]]; then
        if [ "$needs_number" = true ]; then
            # 前の行にサンプルコード番号を挿入
            echo ""
            echo "<span class=\"listing-number\">**サンプルコード${CHAPTER_NUM}-${counter}**</span>"
            echo ""
            counter=$((counter + 1))
        fi
        in_code_block=true
        needs_number=false
    elif [[ "$line" =~ ^\`\`\` ]] && [ "$in_code_block" = true ]; then
        # コードブロック終了
        in_code_block=false
    elif [[ "$line" =~ サンプルコード${CHAPTER_NUM}- ]]; then
        # 既存のサンプルコード番号をスキップ
        needs_number=false
    elif [ "$in_code_block" = false ] && [[ ! "$line" =~ ^\`\`\` ]]; then
        # 通常行で次の```javaに備える
        if [[ "$line" =~ [。！？] ]] || [[ "$line" == "" ]]; then
            needs_number=true
        fi
    fi
    
    echo "$line"
done < "$FILE_PATH" > "$temp_file"

# 元ファイルをバックアップして置換
cp "$FILE_PATH" "${FILE_PATH}.backup"
mv "$temp_file" "$FILE_PATH"

echo "第${CHAPTER_NUM}章のサンプルコード番号付与完了"
echo "バックアップ: ${FILE_PATH}.backup"
echo "付与した番号数: $((counter - 1)) 個"