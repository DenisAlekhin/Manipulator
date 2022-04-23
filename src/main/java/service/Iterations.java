package service;

public class Iterations {
    private static int iterations;

    public static void add(int iter) {
        iterations += iter;
    }

    public static void reset() {
        iterations = 0;
    }

    public static int get() {
        return iterations;
    }
}
