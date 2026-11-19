// 第12章コラム「Optionalはどこまで使うか」：OptionalはSerializableでない、nullのOptionalはNPEになる
import java.io.Serializable;
import java.util.Optional;

public class OptionalFacts {
    public static void main(String[] args) {
        IO.println("Serializable: " + Serializable.class.isAssignableFrom(Optional.class));
        Optional<String> nothing = null;
        try {
            nothing.isPresent();
        } catch (NullPointerException e) {
            IO.println("nullのOptional: NullPointerException");
        }
    }
}
