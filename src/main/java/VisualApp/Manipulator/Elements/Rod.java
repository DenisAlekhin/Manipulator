package VisualApp.Manipulator.Elements;

public class Rod extends Element{
    private double compression;

    public Rod(double compression, int connection) {
        super(connection);
        if(compression <= 0 || compression > 1) {
            try {
                throw new Exception("Compression is out of range (0,1]!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.compression = compression;
    }

    public double getCompression() {
        return compression;
    }
}
