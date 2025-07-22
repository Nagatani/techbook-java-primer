# 必要なツールのインストール

## jpackageで必要なプラットフォーム固有ツール

### Windows

#### WiX Toolset（.msiインストーラ作成に必要）

1. [WiX Toolset](https://wixtoolset.org/releases/)から最新版をダウンロード
2. インストーラを実行
3. 環境変数PATHにWiXのbinディレクトリが追加されていることを確認

```powershell
# インストール確認
candle -?
light -?
```

#### Visual Studio再頒布可能パッケージ

```powershell
# 必要に応じてインストール
# https://aka.ms/vs/17/release/vc_redist.x64.exe
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
# .debパッケージ作成用
sudo apt-get update
sudo apt-get install -y fakeroot dpkg-dev

# .rpmパッケージ作成用（オプション）
sudo apt-get install -y rpm
```

#### Red Hat/CentOS系

```bash
# .rpmパッケージ作成用
sudo yum install -y rpm-build

# .debパッケージ作成用（オプション）
sudo yum install -y dpkg-dev fakeroot
```

### 共通の確認事項

#### jpackageの存在確認

```bash
# jpackageコマンドの確認
jpackage --version

# JDKバージョンの確認（14以上が必要）
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