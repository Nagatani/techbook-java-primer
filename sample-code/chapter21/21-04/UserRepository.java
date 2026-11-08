// UserRepository.java - DBからユーザー情報を取得するクラス
public class UserRepository {
    public String findById(String id) {
        // データベースへの接続と検索処理が行われます
        System.out.println("...データベースに接続中...");
        return "Taro Yamada";
    }
}
