@echo off
setlocal EnableDelayedExpansion

rem ビルドスクリプト for Calculator App
rem 使用方法: build.bat

echo =========================================
echo Calculator App Build Script
echo =========================================

rem 変数定義
set SRC_DIR=.
set BUILD_DIR=build
set DIST_DIR=dist
set JAR_NAME=calculator.jar
set MAIN_CLASS=chapter23.solutions.CalculatorApp

rem 1. クリーン処理
echo 1. クリーン処理...
if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
    echo √ ビルドディレクトリを削除しました
)

if exist "%DIST_DIR%" (
    rmdir /s /q "%DIST_DIR%"
    echo √ 配布ディレクトリを削除しました
)

rem ディレクトリ作成
mkdir "%BUILD_DIR%" 2>nul
mkdir "%DIST_DIR%" 2>nul
echo √ ディレクトリを作成しました

rem 2. コンパイル
echo.
echo 2. Javaソースファイルのコンパイル...
javac -d "%BUILD_DIR%" -encoding UTF-8 CalculatorApp.java

if %ERRORLEVEL% EQU 0 (
    echo √ コンパイルが成功しました
) else (
    echo × コンパイルに失敗しました
    exit /b 1
)

rem 3. JARファイルの作成
echo.
echo 3. JARファイルの作成...
cd "%BUILD_DIR%"
jar cfm "..\%DIST_DIR%\%JAR_NAME%" ..\manifest.txt chapter23\solutions\*.class
cd ..

if %ERRORLEVEL% EQU 0 (
    echo √ JARファイルを作成しました: %DIST_DIR%\%JAR_NAME%
) else (
    echo × JARファイルの作成に失敗しました
    exit /b 1
)

rem 4. JARファイルの情報表示
echo.
echo 4. JARファイルの情報:
echo ----------------------------------------
jar tf "%DIST_DIR%\%JAR_NAME%"
echo ----------------------------------------
for %%F in ("%DIST_DIR%\%JAR_NAME%") do echo ファイルサイズ: %%~zF バイト

rem 5. 動作テスト
echo.
echo 5. 動作テストの実行...
echo ----------------------------------------

rem テスト1: 加算
echo テスト1: 加算 ^(10 + 20^)
java -jar "%DIST_DIR%\%JAR_NAME%" add 10 20
if %ERRORLEVEL% EQU 0 (
    echo √ 加算テスト成功
) else (
    echo × 加算テスト失敗
)

rem テスト2: 乗算
echo.
echo テスト2: 乗算 ^(5.5 × 3.2^)
java -jar "%DIST_DIR%\%JAR_NAME%" multiply 5.5 3.2
if %ERRORLEVEL% EQU 0 (
    echo √ 乗算テスト成功
) else (
    echo × 乗算テスト失敗
)

rem テスト3: 除算
echo.
echo テスト3: 除算 ^(100 ÷ 3^)
java -jar "%DIST_DIR%\%JAR_NAME%" divide 100 3
if %ERRORLEVEL% EQU 0 (
    echo √ 除算テスト成功
) else (
    echo × 除算テスト失敗
)

rem テスト4: ゼロ除算（エラーテスト）
echo.
echo テスト4: ゼロ除算（エラーテスト）
java -jar "%DIST_DIR%\%JAR_NAME%" divide 10 0
if %ERRORLEVEL% NEQ 0 (
    echo √ ゼロ除算エラー処理成功
) else (
    echo × ゼロ除算エラー処理失敗
)

rem 6. ビルド完了
echo.
echo =========================================
echo ビルドが完了しました！
echo 実行可能JARファイル: %DIST_DIR%\%JAR_NAME%
echo.
echo 使用方法:
echo   java -jar %DIST_DIR%\%JAR_NAME% [操作] [数値1] [数値2]
echo.
echo 例:
echo   java -jar %DIST_DIR%\%JAR_NAME% add 10 20
echo =========================================

endlocal