#!/bin/bash
# macOS向けjpackageスクリプト
#
# --mac-package-name        メニューバーに表示する名前（16文字未満）
# --mac-package-identifier  バンドル識別子（逆ドメイン形式）
# --mac-signing-key-user-name
#     署名に使う証明書の、チーム名（またはユーザー名）の部分。
#     キーチェーンにある「Developer ID Application: MyCompany Inc. (TEAMID)」の
#     「MyCompany Inc. (TEAMID)」を指定する（security find-identity -v -p codesigning で確認できる）
#
# 署名だけではGatekeeperを通らない。インターネットで配布するdmgは、作成後に
# xcrun notarytool submit TodoManager-1.0.0.dmg --keychain-profile <プロファイル名> --wait
# で公証を受け、xcrun stapler staple TodoManager-1.0.0.dmg で結果を添付する

set -eu

jpackage --type dmg \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.icns \
         --mac-package-name "TodoManager" \
         --mac-package-identifier "com.mycompany.todomanager" \
         --mac-sign \
         --mac-signing-key-user-name "MyCompany Inc. (TEAMID)" \
         --dest ./output
