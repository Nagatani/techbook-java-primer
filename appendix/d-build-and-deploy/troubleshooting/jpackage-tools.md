# 必要なツールのインストール

## jpackageで必要なプラットフォーム固有ツール

### Windows

#### WiX Toolset（.msi・.exeインストーラー作成に必要）

JDK 24以降のjpackageは、WiX Toolsetのv3に加えてv4とv5に対応しています。
JDK 25のjpackageが対応していない新しい版を入れるとインストーラーを作れないため、v5（またはv3）を使います。

WiX v5は、.NET SDKの`dotnet tool`でインストールします。

```powershell
# WiX v5のインストール（.NET SDKが必要）
dotnet tool install --global wix --version 5.*

# インストール確認
wix --version
```

WiX v3を使う場合は、[WiX Toolset v3](https://github.com/wixtoolset/wix3/releases)のインストーラーを実行し、環境変数PATHにWiXの`bin`ディレクトリが追加されていることを確認します。

```powershell
# WiX v3のインストール確認
candle -?
light -?
```

### macOS

#### Xcode Command Line Tools

```bash
# インストール
xcode-select --install

# インストール確認
xcode-select -p

# バージョン確認
pkgutil --pkg-info=com.apple.pkg.CLTools_Executables
```

#### 開発者証明書（アプリ署名用）

1. Apple Developer Programへの登録
2. Certificates, Identifiers & Profilesで証明書を作成
3. キーチェーンアクセスで証明書をインストール

```bash
# 証明書の確認
security find-identity -v -p codesigning
```

### Linux

#### Debian/Ubuntu系

```bash
# .debパッケージ作成用（binutilsはjpackageが使うobjcopyを含む）
sudo apt-get update
sudo apt-get install -y fakeroot dpkg-dev binutils

# .rpmパッケージ作成用（オプション）
sudo apt-get install -y rpm
```

#### Red Hat/CentOS系

```bash
# .rpmパッケージ作成用
sudo dnf install -y rpm-build
```

Red Hat系のディストリビューションでは、.debパッケージは作らず.rpmパッケージを作るのが普通です。

### 共通の確認事項

#### jpackageの存在確認

```bash
# jpackageコマンドの確認
jpackage --version

# JDKバージョンの確認（jpackageはJDK 16以降の正式な機能。本書はJDK 25を使う）
java -version
```

#### トラブルシューティング

```bash
# jpackageが見つからない場合
# JAVA_HOMEが正しく設定されているか確認
echo $JAVA_HOME

# PATHにJDKのbinディレクトリが含まれているか確認
echo $PATH | grep -o "[^:]*java[^:]*"

# 正しいJDKを使用しているか確認
which java
which jpackage
```