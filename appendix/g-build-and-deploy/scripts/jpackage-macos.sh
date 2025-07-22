#!/bin/bash
# macOS向けjpackageスクリプト

jpackage --type dmg \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.icns \
         --mac-package-name "com.mycompany.todomanager" \
         --mac-package-identifier "com.mycompany.todomanager" \
         --mac-sign \
         --mac-signing-key-user-name "Developer ID Application: MyCompany Inc." \
         --dest ./output