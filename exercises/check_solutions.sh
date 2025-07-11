#!/bin/bash

# 解答例のコンパイルチェックスクリプト
# 使用方法: ./check_solutions.sh

# 色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# 結果を保存する配列
declare -a success_chapters=()
declare -a failed_chapters=()
declare -a warning_chapters=()

echo "================================================"
echo "Java入門書 - 解答例コンパイルチェック"
echo "================================================"

# 各章のsolutionsディレクトリをチェック
for chapter in {01..23}; do
    chapter_dir="chapter${chapter}"
    solutions_dir="${chapter_dir}/solutions"
    
    if [ ! -d "$solutions_dir" ]; then
        echo -e "${YELLOW}[警告] ${solutions_dir} が存在しません${NC}"
        warning_chapters+=("$chapter")
        continue
    fi
    
    # Javaファイルが存在するかチェック
    java_files=$(find "$solutions_dir" -name "*.java" -type f 2>/dev/null)
    
    if [ -z "$java_files" ]; then
        echo -e "${YELLOW}[警告] ${solutions_dir} にJavaファイルがありません${NC}"
        warning_chapters+=("$chapter")
        continue
    fi
    
    echo "第${chapter}章の解答例をチェック中..."
    
    # 一時ディレクトリを作成
    temp_dir="temp_compile_${chapter}"
    mkdir -p "$temp_dir"
    
    # Javaファイルをコンパイル
    compile_error=false
    while IFS= read -r java_file; do
        # JUnitを含むファイルは特別な処理が必要
        if grep -q "import.*junit" "$java_file" 2>/dev/null || grep -q "@Test" "$java_file" 2>/dev/null; then
            echo "  [スキップ] $(basename "$java_file") (JUnitテストファイル)"
            continue
        fi
        
        # コンパイル実行
        if javac -d "$temp_dir" -cp "$temp_dir" "$java_file" 2>/dev/null; then
            echo -e "  ${GREEN}[OK]${NC} $(basename "$java_file")"
        else
            echo -e "  ${RED}[エラー]${NC} $(basename "$java_file")"
            compile_error=true
        fi
    done <<< "$java_files"
    
    # 結果を記録
    if [ "$compile_error" = true ]; then
        failed_chapters+=("$chapter")
    else
        success_chapters+=("$chapter")
    fi
    
    # 一時ディレクトリを削除
    rm -rf "$temp_dir"
done

echo ""
echo "================================================"
echo "チェック結果サマリー"
echo "================================================"

echo -e "${GREEN}成功: ${#success_chapters[@]}章${NC}"
if [ ${#success_chapters[@]} -gt 0 ]; then
    echo "  第${success_chapters[*]}章"
fi

echo -e "${RED}失敗: ${#failed_chapters[@]}章${NC}"
if [ ${#failed_chapters[@]} -gt 0 ]; then
    echo "  第${failed_chapters[*]}章"
fi

echo -e "${YELLOW}警告: ${#warning_chapters[@]}章${NC}"
if [ ${#warning_chapters[@]} -gt 0 ]; then
    echo "  第${warning_chapters[*]}章"
fi

echo ""
echo "================================================"

# 詳細レポート生成
if [ ${#failed_chapters[@]} -gt 0 ] || [ ${#warning_chapters[@]} -gt 0 ]; then
    echo "詳細レポート:"
    
    # コンパイルエラーの詳細
    if [ ${#failed_chapters[@]} -gt 0 ]; then
        echo ""
        echo "コンパイルエラーが発生した章:"
        for chapter in "${failed_chapters[@]}"; do
            echo "  - 第${chapter}章: exercises/chapter${chapter}/solutions/"
        done
    fi
    
    # 警告の詳細
    if [ ${#warning_chapters[@]} -gt 0 ]; then
        echo ""
        echo "解答例が不足している章:"
        for chapter in "${warning_chapters[@]}"; do
            echo "  - 第${chapter}章: exercises/chapter${chapter}/solutions/"
        done
    fi
fi

echo ""
echo "完了しました。"

# 終了コード
if [ ${#failed_chapters[@]} -gt 0 ]; then
    exit 1
else
    exit 0
fi