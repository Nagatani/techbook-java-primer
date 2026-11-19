// 第19章：java.util.ObserverとObservableはJava 9で非推奨
public class ObserverDeprecated {
    static void use(java.util.Observable o, java.util.Observer w) {
        o.addObserver(w);
    }
}
