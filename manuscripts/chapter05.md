# 第5章 パッケージとアクセス制御

この章では、Javaのパッケージシステムとアクセス修飾子について学習します。

## 5.1 パッケージの基礎

### パッケージ宣言

```java
// com/example/utils/StringUtil.java
package com.example.utils;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
```

### パッケージのインポート

```java
// com/example/app/Main.java
package com.example.app;

import com.example.utils.StringUtil;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (StringUtil.isEmpty("")) {
            System.out.println("文字列は空です");
        }
    }
}
```

## 5.2 アクセス修飾子

```java
public class AccessExample {
    public String publicField;        // どこからでもアクセス可能
    protected String protectedField;  // 同じパッケージ + サブクラス
    String packageField;              // 同じパッケージのみ
    private String privateField;      // 同じクラスのみ
    
    public void accessTest() {
        // 全てのフィールドにアクセス可能
        publicField = "public";
        protectedField = "protected";
        packageField = "package";
        privateField = "private";
    }
}
```

## 5.3 実践例

### ライブラリパッケージの設計

```java
// com/mylib/math/Calculator.java
package com.mylib.math;

public class Calculator {
    public static double add(double a, double b) {
        return a + b;
    }
    
    public static double multiply(double a, double b) {
        return a * b;
    }
}

// com/mylib/string/TextProcessor.java
package com.mylib.string;

public class TextProcessor {
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
```

## まとめ

パッケージとアクセス制御により、大規模なアプリケーションの構造化と保守性向上が実現できます。