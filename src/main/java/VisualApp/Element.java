package VisualApp;

class Element {
    double compression;
    double angle;
    int connection;
    int rotation;

    public Element(double compression, double angle, int connection) {
        if(convertAngle(angle) < -359 || convertAngle(angle) > 359 || compression <= 0 || compression > 1) {
            try {
                throw new Exception("Wrong input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.compression = compression;
        this.angle = convertAngle(angle);
        this.connection = connection;
    }

    private double convertAngle(double angle) {
        return angle * Math.PI / 180.0;
    }
}
