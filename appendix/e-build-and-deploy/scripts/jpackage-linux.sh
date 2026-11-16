#!/bin/bash
# Linux向けjpackageスクリプト（.deb）
#
# 必要なパッケージ（Debian/Ubuntu）: fakeroot dpkg-dev binutils
# --linux-app-category は、debではcontrolファイルのSection になる。
# Debianのセクション名（utils、misc、editors など）から選ぶ

set -eu

jpackage --type deb \
         --name "todomanager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.png \
         --linux-menu-group "Office" \
         --linux-shortcut \
         --linux-deb-maintainer "dev@mycompany.com" \
         --linux-app-category "utils" \
         --dest ./output
