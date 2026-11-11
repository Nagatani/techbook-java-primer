public class OperationDemo {
    public static void main(String[] args) {
        double x = 6.0;
        double y = 2.0;
        IO.println("x = " + x + ", y = " + y);
        for (Operation op : Operation.values()) {
            IO.println("%f %s %f = %f".formatted(x, op, y, op.apply(x, y)));
        }
    }
}
