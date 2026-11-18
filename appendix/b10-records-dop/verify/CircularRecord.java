import java.util.ArrayList;
import java.util.List;

/**
 * 変更できるリストをコンポーネントに持つレコードは、自分自身を要素に入れて循環を作れる。
 * そのとき、暗黙のtoStringとhashCodeは自分を何度もたどり、StackOverflowErrorになる。
 *
 *   javac CircularRecord.java
 *   java CircularRecord
 */
public class CircularRecord {
    record Node(String value, List<Node> children) { }

    record SafeNode(String value, List<SafeNode> children) {
        SafeNode {
            children = List.copyOf(children);
        }
    }

    public static void main(String[] args) {
        var children = new ArrayList<Node>();
        var node = new Node("root", children);
        children.add(node);                     // nodeの子にnode自身が入る
        try {
            node.hashCode();
        } catch (StackOverflowError e) {
            IO.println("hashCode: StackOverflowError");
        }
        try {
            node.toString();
        } catch (StackOverflowError e) {
            IO.println("toString: StackOverflowError");
        }
        // コンパクトコンストラクターで変更できないコピーにすると、作ったあとに循環を作れない
        var list = new ArrayList<SafeNode>();
        var safe = new SafeNode("root", list);
        list.add(safe);                         // 元のリストを変えても、safeの子は変わらない
        IO.println("SafeNode: " + safe);
    }
}
