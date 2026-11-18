import java.util.List;

// List.classの型はClass<List>（raw型）で、Class<List<String>>ではない
class ClassOfList {
    Class<List<String>> type = List.class;
}
