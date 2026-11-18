import java.util.List;

// Classは具象化可能な型しか表せないので、パラメーター化型のクラスリテラルは書けない
class ParameterizedClassLiteral {
    Object type = List<String>.class;
}
