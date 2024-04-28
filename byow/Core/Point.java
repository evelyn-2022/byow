package byow.Core;

import java.io.Serializable;

public class Point implements Serializable {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static double distance(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Point shift(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}
