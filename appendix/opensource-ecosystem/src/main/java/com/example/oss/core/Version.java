package com.example.oss.core;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * セマンティックバージョニングを実装するクラス
 * 
 * <p>このクラスは、Semantic Versioning 2.0.0仕様に準拠したバージョン管理を提供します。
 * バージョンの解析、比較、互換性チェックなどの機能を持ちます。
 * 
 * <h2>バージョン形式</h2>
 * <pre>
 * MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]
 * 例: 1.2.3-alpha.1+build.123
 * </pre>
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * Version v1 = Version.parse("1.2.3");
 * Version v2 = Version.parse("2.0.0");
 * 
 * if (v2.hasBreakingChanges(v1)) {
 *     // メジャーバージョンアップの処理
 * }
 * }</pre>
 * 
 * @since 1.0.0
 */
public class Version implements Comparable<Version> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
        "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-(\\w+(?:\\.\\w+)*))?(?:\\+(\\w+(?:\\.\\w+)*))?$"
    );
    
    private final int major;
    private final int minor;
    private final int patch;
    private final String preRelease;
    private final String build;
    
    /**
     * バージョンを構築します
     * 
     * @param major メジャーバージョン
     * @param minor マイナーバージョン
     * @param patch パッチバージョン
     * @param preRelease プレリリース識別子（nullも可）
     * @param build ビルド識別子（nullも可）
     * @throws IllegalArgumentException バージョン番号が負の値の場合
     */
    private Version(int major, int minor, int patch, String preRelease, String build) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers cannot be negative");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.build = build;
    }
    
    /**
     * 文字列からVersionオブジェクトを解析します
     * 
     * @param versionString バージョン文字列
     * @return 解析されたVersionオブジェクト
     * @throws IllegalArgumentException バージョン形式が不正な場合
     * @throws NullPointerException versionStringがnullの場合
     */
    public static Version parse(String versionString) {
        Objects.requireNonNull(versionString, "Version string cannot be null");
        
        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid version format: " + versionString);
        }
        
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String preRelease = matcher.group(4);
        String build = matcher.group(5);
        
        return new Version(major, minor, patch, preRelease, build);
    }
    
    /**
     * 指定されたバージョンと互換性があるかチェックします
     * 
     * <p>セマンティックバージョニングに基づき、メジャーバージョンが同じで、
     * このバージョンが指定されたバージョン以上であれば互換性があると判定します。
     * 
     * @param other 比較対象のバージョン
     * @return 互換性がある場合true
     * @throws NullPointerException otherがnullの場合
     */
    public boolean isCompatibleWith(Version other) {
        Objects.requireNonNull(other, "Other version cannot be null");
        return this.major == other.major && this.compareTo(other) >= 0;
    }
    
    /**
     * 指定されたバージョンと比較して破壊的変更があるかチェックします
     * 
     * @param other 比較対象のバージョン
     * @return 破壊的変更がある場合true
     * @throws NullPointerException otherがnullの場合
     */
    public boolean hasBreakingChanges(Version other) {
        Objects.requireNonNull(other, "Other version cannot be null");
        return this.major != other.major;
    }
    
    /**
     * メジャーバージョンを取得します
     * 
     * @return メジャーバージョン
     */
    public int getMajor() {
        return major;
    }
    
    /**
     * マイナーバージョンを取得します
     * 
     * @return マイナーバージョン
     */
    public int getMinor() {
        return minor;
    }
    
    /**
     * パッチバージョンを取得します
     * 
     * @return パッチバージョン
     */
    public int getPatch() {
        return patch;
    }
    
    /**
     * プレリリース識別子を取得します
     * 
     * @return プレリリース識別子（存在しない場合null）
     */
    public String getPreRelease() {
        return preRelease;
    }
    
    /**
     * ビルド識別子を取得します
     * 
     * @return ビルド識別子（存在しない場合null）
     */
    public String getBuild() {
        return build;
    }
    
    /**
     * プレリリースバージョンかどうかをチェックします
     * 
     * @return プレリリースバージョンの場合true
     */
    public boolean isPreRelease() {
        return preRelease != null;
    }
    
    @Override
    public int compareTo(Version other) {
        Objects.requireNonNull(other, "Other version cannot be null");
        
        int result = Integer.compare(this.major, other.major);
        if (result != 0) return result;
        
        result = Integer.compare(this.minor, other.minor);
        if (result != 0) return result;
        
        result = Integer.compare(this.patch, other.patch);
        if (result != 0) return result;
        
        // プレリリースの比較
        if (this.preRelease == null && other.preRelease == null) return 0;
        if (this.preRelease == null) return 1;  // リリース版の方が新しい
        if (other.preRelease == null) return -1;
        
        return this.preRelease.compareTo(other.preRelease);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append(major).append('.')
            .append(minor).append('.')
            .append(patch);
        
        if (preRelease != null) {
            sb.append('-').append(preRelease);
        }
        if (build != null) {
            sb.append('+').append(build);
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major &&
               minor == version.minor &&
               patch == version.patch &&
               Objects.equals(preRelease, version.preRelease) &&
               Objects.equals(build, version.build);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, build);
    }
}