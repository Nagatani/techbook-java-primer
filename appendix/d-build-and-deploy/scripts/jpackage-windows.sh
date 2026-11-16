#!/bin/bash
# Windows向けjpackageスクリプト（.msi）
#
# Git Bashなどのbashで実行する。WiX Toolset（v5またはv3）が必要
# （../troubleshooting/jpackage-tools.md）。
#
# PowerShellで実行する場合は、行末の「\」をバッククォート「`」に置き換える。
# コマンドプロンプト（cmd.exe）では「^」に置き換える。

set -eu

jpackage --type msi \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --copyright "Copyright (c) 2026 MyCompany Inc." \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.ico \
         --win-menu \
         --win-shortcut \
         --win-dir-chooser \
         --win-per-user-install \
         --dest ./output
