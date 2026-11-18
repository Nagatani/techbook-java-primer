import java.util.ArrayList;
import java.util.List;

// List<String>はList<Object>のサブタイプではない（配列と違って共変ではない）
class Invariance {
    List<Object> objects = new ArrayList<String>();
}
