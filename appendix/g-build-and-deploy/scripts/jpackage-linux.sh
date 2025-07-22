#!/bin/bash
# Linux向けjpackageスクリプト

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
         --linux-app-category "office" \
         --dest ./output