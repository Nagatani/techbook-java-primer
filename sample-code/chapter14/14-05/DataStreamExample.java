import java.io.*;

public class DataStreamExample {
    public static void main(String[] args) throws IOException {
        String filename = "data.bin";
        
        // バイナリファイルの書き込み
        // DataOutputStreamを使用してプリミティブ型のデータをバイナリ形式で保存します。
        // この方法により、データの型情報を保持したまま効率的にファイルに保存できます。
        // データの書き込み
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filename)))) {
            // プリミティブ型の書き込み
            dos.writeInt(12345);
            dos.writeLong(9876543210L);
            dos.writeDouble(3.14159);
            dos.writeBoolean(true);
            dos.writeUTF("こんにちは、世界！");  // 修正UTF-8形式
            
            IO.println("データを書き込みました。");
        }
        
        // データの読み込み（書き込んだ順序と同じ順序で読む）
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filename)))) {
            int intValue = dis.readInt();
            long longValue = dis.readLong();
            double doubleValue = dis.readDouble();
            boolean boolValue = dis.readBoolean();
            String strValue = dis.readUTF();
            
            IO.println("読み込んだデータ: %d, %d, %.5f, %b, %s".formatted(
                    intValue, longValue, doubleValue, 
                    boolValue, strValue));
        }
    }
}
