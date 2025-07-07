package com.example.oss.plugins.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拡張を示すアノテーション
 * 
 * <p>プラグイン内のフィールドに付与することで、
 * 自動的に拡張ポイントに登録されます。
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * public class MyPlugin implements Plugin {
 *     @Extension("dataTransformers")
 *     private final DataTransformer transformer = new MyTransformer();
 * }
 * }</pre>
 * 
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    
    /**
     * 拡張ポイントの名前
     * 
     * @return 拡張ポイント名
     */
    String value();
}