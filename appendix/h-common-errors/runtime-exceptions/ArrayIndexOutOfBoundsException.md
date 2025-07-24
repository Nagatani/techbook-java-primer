# ArrayIndexOutOfBoundsException

## 概要

`ArrayIndexOutOfBoundsException`は、配列の有効範囲外のインデックスにアクセスした際に発生する実行時例外です。配列のインデックスは0から始まり、`length - 1`で終わることを忘れがちなため、頻繁に発生するエラーです。

## 発生原因

1. **境界値の誤り**: 配列の最後の要素を超えたアクセス
2. **負のインデックス**: 負の値でのアクセス
3. **空配列へのアクセス**: 長さ0の配列への要素アクセス
4. **計算ミス**: インデックス計算の誤り

## 典型的な発生パターン

### パターン1：境界値エラー

```java
int[] numbers = new int[5];  // インデックスは0〜4
for (int i = 0; i <= numbers.length; i++) {  // i <= lengthが問題
    numbers[i] = i;  // i=5でArrayIndexOutOfBoundsException
}
```

### パターン2：1から始めるミス

```java
int[] scores = {90, 85, 78, 92, 88};
// 1から始めてしまい、最後の要素を見逃す
for (int i = 1; i <= scores.length; i++) {
    System.out.println("Score " + i + ": " + scores[i]);  // i=5でエラー
}
```

### パターン3：負のインデックス

```java
public int getLastElement(int[] array, int offsetFromEnd) {
    // offsetFromEndが配列長より大きい場合、負のインデックスになる
    return array[array.length - offsetFromEnd];  // 負の値でエラー
}
```

### パターン4：空配列

```java
int[] emptyArray = new int[0];
int firstElement = emptyArray[0];  // ArrayIndexOutOfBoundsException
```

## 解決策

### 1. 正しいループ条件

```java
// 正しい方法1：< を使用
for (int i = 0; i < numbers.length; i++) {
    numbers[i] = i;
}

// 正しい方法2：拡張for文
for (int number : numbers) {
    System.out.println(number);
}
```

### 2. 境界チェック

```java
public void setElement(int[] array, int index, int value) {
    if (index >= 0 && index < array.length) {
        array[index] = value;
    } else {
        throw new IllegalArgumentException(
            "インデックスが範囲外です: " + index + 
            " (配列長: " + array.length + ")");
    }
}
```

### 3. 空配列のチェック

```java
public int getFirstElement(int[] array) {
    if (array.length == 0) {
        throw new IllegalArgumentException("配列が空です");
    }
    return array[0];
}
```

### 4. 安全なアクセスメソッド

```java
public class SafeArray {
    private int[] data;
    
    public Optional<Integer> get(int index) {
        if (index >= 0 && index < data.length) {
            return Optional.of(data[index]);
        }
        return Optional.empty();
    }
    
    public int getOrDefault(int index, int defaultValue) {
        if (index >= 0 && index < data.length) {
            return data[index];
        }
        return defaultValue;
    }
}
```

## 高度な解決策

### 1. 循環インデックス

```java
public class CircularArray {
    private int[] data;
    
    public int get(int index) {
        // 負の値でも正しく動作する循環インデックス
        int normalizedIndex = ((index % data.length) + data.length) % data.length;
        return data[normalizedIndex];
    }
}
```

### 2. 範囲クランプ

```java
public class ClampedArray {
    private int[] data;
    
    public int get(int index) {
        // インデックスを有効範囲内にクランプ
        int clampedIndex = Math.max(0, Math.min(index, data.length - 1));
        return data[clampedIndex];
    }
}
```

### 3. スライス操作

```java
public int[] slice(int[] array, int start, int end) {
    // 範囲を安全に正規化
    int safeStart = Math.max(0, Math.min(start, array.length));
    int safeEnd = Math.max(safeStart, Math.min(end, array.length));
    
    return Arrays.copyOfRange(array, safeStart, safeEnd);
}
```

## よくある間違いと対策

### 1. 文字列の長さとの混同

```java
// 間違い
String text = "Hello";
char lastChar = text.charAt(text.length());  // StringIndexOutOfBoundsException

// 正しい
char lastChar = text.charAt(text.length() - 1);
```

### 2. 二次元配列での誤り

```java
int[][] matrix = new int[3][4];

// 間違い
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix.length; j++) {  // matrix[i].lengthを使うべき
        matrix[i][j] = i * j;
    }
}

// 正しい
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
        matrix[i][j] = i * j;
    }
}
```

### 3. 動的なサイズ変更

```java
// Listを使用して動的にサイズを管理
List<Integer> numbers = new ArrayList<>();
numbers.add(10);  // サイズを気にせず要素を追加
numbers.add(20);
// インデックスアクセスも安全にチェック
if (index < numbers.size()) {
    int value = numbers.get(index);
}
```

## デバッグのコツ

### 1. エラーメッセージの読み方

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 5
    at com.example.ArrayDemo.main(ArrayDemo.java:10)
```

- `5`は問題のあるインデックス値
- 配列のサイズを確認し、なぜこのインデックスになったか調査

### 2. デバッグ出力

```java
public void debugArray(int[] array, int index) {
    System.out.println("配列長: " + array.length);
    System.out.println("アクセスしようとしたインデックス: " + index);
    System.out.println("有効な範囲: 0 〜 " + (array.length - 1));
    
    if (index >= 0 && index < array.length) {
        System.out.println("値: " + array[index]);
    } else {
        System.out.println("インデックスが範囲外です！");
    }
}
```

### 3. アサーションの活用

```java
private void processArray(int[] array, int index) {
    assert array != null : "配列がnullです";
    assert index >= 0 : "インデックスが負です: " + index;
    assert index < array.length : "インデックスが大きすぎます: " + index;
    
    // 処理を続行
    int value = array[index];
}
```

## ベストプラクティス

1. **拡張for文の優先使用**: インデックスが不要な場合は拡張for文を使用
2. **境界チェックの習慣化**: 配列アクセス前に必ず範囲をチェック
3. **コレクションの活用**: 可変長が必要な場合はListを使用
4. **明確なエラーメッセージ**: 範囲外アクセス時は詳細な情報を提供
5. **単体テストでの境界値テスト**: 空配列、1要素、最大インデックスでテスト

## 関連項目

- [NullPointerException](NullPointerException.md)
- [IndexOutOfBoundsException](IndexOutOfBoundsException.md)
- コレクションフレームワークガイド