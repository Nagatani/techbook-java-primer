String name = "Java 25";

void main() {
    List<String> words = new ArrayList<>(List.of("compact", "source", "file"));
    IO.println(greet(name));
    IO.println(words);
}

String greet(String who) {
    return "Hello, " + who + "!";
}
