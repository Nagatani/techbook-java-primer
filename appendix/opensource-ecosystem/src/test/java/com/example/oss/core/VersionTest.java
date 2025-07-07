package com.example.oss.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Versionクラスのテスト
 */
class VersionTest {
    
    @Test
    @DisplayName("正常なバージョン文字列の解析")
    void testVersionParsing() {
        // Given
        String versionString = "1.2.3-alpha.1+build.123";
        
        // When
        Version version = Version.parse(versionString);
        
        // Then
        assertThat(version.getMajor()).isEqualTo(1);
        assertThat(version.getMinor()).isEqualTo(2);
        assertThat(version.getPatch()).isEqualTo(3);
        assertThat(version.getPreRelease()).isEqualTo("alpha.1");
        assertThat(version.getBuild()).isEqualTo("build.123");
    }
    
    @Test
    @DisplayName("シンプルなバージョン文字列の解析")
    void testSimpleVersionParsing() {
        // Given
        String versionString = "2.0.0";
        
        // When
        Version version = Version.parse(versionString);
        
        // Then
        assertThat(version.getMajor()).isEqualTo(2);
        assertThat(version.getMinor()).isEqualTo(0);
        assertThat(version.getPatch()).isEqualTo(0);
        assertThat(version.getPreRelease()).isNull();
        assertThat(version.getBuild()).isNull();
        assertThat(version.isPreRelease()).isFalse();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "1.2.3.4",
        "1.2",
        "1",
        "a.b.c",
        "1.2.3-",
        "1.2.3+"
    })
    @DisplayName("不正なバージョン形式で例外が発生すること")
    void testInvalidVersionFormat(String invalidVersion) {
        assertThatThrownBy(() -> Version.parse(invalidVersion))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid version format");
    }
    
    @Test
    @DisplayName("nullのバージョン文字列で例外が発生すること")
    void testNullVersionString() {
        assertThatThrownBy(() -> Version.parse(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Version string cannot be null");
    }
    
    @ParameterizedTest
    @CsvSource({
        "1.0.0, 2.0.0, true",
        "1.0.0, 1.0.0, false",
        "1.2.3, 1.2.4, false",
        "2.0.0, 1.9.9, true"
    })
    @DisplayName("破壊的変更の判定")
    void testHasBreakingChanges(String version1, String version2, boolean expected) {
        // Given
        Version v1 = Version.parse(version1);
        Version v2 = Version.parse(version2);
        
        // When & Then
        assertThat(v1.hasBreakingChanges(v2)).isEqualTo(expected);
    }
    
    @ParameterizedTest
    @CsvSource({
        "1.0.0, 1.0.0, true",
        "1.2.0, 1.1.0, true",
        "1.0.1, 1.0.0, true",
        "2.0.0, 1.0.0, false",
        "1.0.0, 2.0.0, false"
    })
    @DisplayName("互換性の判定")
    void testIsCompatibleWith(String version1, String version2, boolean expected) {
        // Given
        Version v1 = Version.parse(version1);
        Version v2 = Version.parse(version2);
        
        // When & Then
        assertThat(v1.isCompatibleWith(v2)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("バージョンの比較 - 同じバージョン")
    void testCompareToEqual() {
        Version v1 = Version.parse("1.2.3");
        Version v2 = Version.parse("1.2.3");
        
        assertThat(v1.compareTo(v2)).isEqualTo(0);
        assertThat(v1).isEqualTo(v2);
    }
    
    @Test
    @DisplayName("バージョンの比較 - メジャーバージョンの違い")
    void testCompareToMajor() {
        Version v1 = Version.parse("2.0.0");
        Version v2 = Version.parse("1.9.9");
        
        assertThat(v1.compareTo(v2)).isGreaterThan(0);
        assertThat(v2.compareTo(v1)).isLessThan(0);
    }
    
    @Test
    @DisplayName("バージョンの比較 - プレリリース版")
    void testCompareToPreRelease() {
        Version release = Version.parse("1.0.0");
        Version preRelease = Version.parse("1.0.0-alpha");
        
        // リリース版の方が新しい
        assertThat(release.compareTo(preRelease)).isGreaterThan(0);
        assertThat(preRelease.compareTo(release)).isLessThan(0);
    }
    
    @Test
    @DisplayName("toString()メソッドの検証")
    void testToString() {
        assertThat(Version.parse("1.2.3").toString()).isEqualTo("1.2.3");
        assertThat(Version.parse("1.2.3-alpha").toString()).isEqualTo("1.2.3-alpha");
        assertThat(Version.parse("1.2.3+build").toString()).isEqualTo("1.2.3+build");
        assertThat(Version.parse("1.2.3-alpha+build").toString()).isEqualTo("1.2.3-alpha+build");
    }
    
    @Test
    @DisplayName("equalsとhashCodeの一貫性")
    void testEqualsAndHashCode() {
        Version v1 = Version.parse("1.2.3-alpha+build");
        Version v2 = Version.parse("1.2.3-alpha+build");
        Version v3 = Version.parse("1.2.3-beta+build");
        
        // 等価性
        assertThat(v1).isEqualTo(v2);
        assertThat(v1).isNotEqualTo(v3);
        
        // hashCodeの一貫性
        assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
        assertThat(v1.hashCode()).isNotEqualTo(v3.hashCode());
    }
}