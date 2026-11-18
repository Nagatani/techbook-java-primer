import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.MethodModel;
import java.lang.constant.ClassDesc;
import java.lang.reflect.ClassFileFormatVersion;

/**
 * 自分自身のクラスファイル（ClassFileHeader.class）を読み、先頭の構造を表示する。
 *
 *   javac ClassFileHeader.java && java ClassFileHeader
 *   javap -v ClassFileHeader      （定数プールなどをすべて見る）
 */
public class ClassFileHeader {
    public static void main(String[] args) throws IOException {
        byte[] bytes;
        try (InputStream in = ClassFileHeader.class.getResourceAsStream("ClassFileHeader.class")) {
            bytes = in.readAllBytes();
        }

        // 先頭の8バイト: マジックナンバー（4バイト）、マイナーバージョン（2バイト）、メジャーバージョン（2バイト）
        IO.println("先頭の8バイト: " + hex(bytes, 0, 8));
        IO.println("マジックナンバー: " + hex(bytes, 0, 4));
        IO.println("マイナーバージョン: " + u2(bytes, 4));
        IO.println("メジャーバージョン: " + u2(bytes, 6));
        // Javaの版とクラスファイルのメジャーバージョンの対応
        for (ClassFileFormatVersion v : new ClassFileFormatVersion[] {
                ClassFileFormatVersion.RELEASE_8, ClassFileFormatVersion.RELEASE_11, ClassFileFormatVersion.RELEASE_17,
                ClassFileFormatVersion.RELEASE_21, ClassFileFormatVersion.RELEASE_25 }) {
            IO.println(v + " -> " + v.major());
        }

        // Class-File API（java.lang.classfile、Java 24で正式な機能）で中身を読む
        ClassModel model = ClassFile.of().parse(bytes);
        IO.println("このクラス: " + model.thisClass().asInternalName());
        IO.println("スーパークラス: " + model.superclass().orElseThrow().asInternalName());
        for (MethodModel m : model.methods()) {
            IO.println("メソッド: " + m.methodName().stringValue() + m.methodType().stringValue());
        }

        // メジャーバージョンが70（このJVMより新しい版）のクラスファイルを組み立てて、読み込ませる
        byte[] future = ClassFile.of().build(ClassDesc.of("Future"), cb -> cb.withVersion(70, 0));
        try {
            new ByteLoader().define("Future", future);
            IO.println("版70のクラスを読み込めた");
        } catch (UnsupportedClassVersionError e) {
            IO.println("UnsupportedClassVersionError: " + e.getMessage());
        }
    }

    /** バイト列からクラスを定義するだけのクラスローダー */
    static class ByteLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    static String hex(byte[] b, int from, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < from + len; i++) {
            sb.append("%02X".formatted(b[i] & 0xFF));
        }
        return sb.toString();
    }

    static int u2(byte[] b, int at) {
        return ((b[at] & 0xFF) << 8) | (b[at + 1] & 0xFF);
    }
}
