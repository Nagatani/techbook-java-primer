#!/bin/sh
# 手元で流す検査をまとめて行う（npm run check:local）。
# GitHub Actions（.github/workflows/check.yml・code.yml）は数分で終わる検査とコンパイルだけを行うので、
# 時間のかかる検査（実行結果の照合、JUnitのテスト、主張の検証、textlint、紙面の折り返し）はここで流す。
# 失敗しても止まらずに最後まで流し、終わりに結果の一覧を出す。どれかが失敗したら終了コード1を返す。
#
#   npm run check:local            すべて流す（20分ほど）
#   npm run check:local -- quick   CIと同じ軽い検査とtextlintだけ（2分ほど）

cd "$(git rev-parse --show-toplevel)" || exit 1

mode=${1:-full}
summary=""
failed=0

step() {
    name=$1
    shift
    echo ""
    echo "=== $name: $*"
    start=$(date +%s)
    if "$@"; then
        result="OK"
    else
        result="失敗"
        failed=1
    fi
    summary="$summary
  ${result}	${name}（$(( $(date +%s) - start ))秒）"
}

step "構成" npm run --silent check:structure
step "CLAUDE.mdのルール" npm run --silent check:compliance
step "索引と要約" npm run --silent index:check
step "図" npm run --silent check:diagrams
step "API名" npm run --silent check:api-names
step "主張の台帳（静的）" node scripts/check-claims.js --static
step "textlint" npm run --silent lint

if [ "$mode" != "quick" ]; then
    step "サンプルコード（コンパイル・実行結果）" npm run --silent check:samples
    step "全文のないサンプルの実行結果" npm run --silent check:java:run
    step "付録・演習のコードとテスト" npm run --silent check:public-code
    step "付録Bと本文の主張の検証" node scripts/check-claims.js
    step "紙面のコードの折り返し" npm run --silent check:code-wrap
fi

echo ""
echo "=== 結果$summary"
exit $failed
