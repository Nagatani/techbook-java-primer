#!/bin/bash
# Windows向けjpackageスクリプト

jpackage --type msi \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --copyright "Copyright (c) 2024 MyCompany Inc." \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.ico \
         --win-menu \
         --win-shortcut \
         --win-dir-chooser \
         --win-per-user-install \
         --dest ./output