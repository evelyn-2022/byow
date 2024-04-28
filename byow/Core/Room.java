package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import static byow.Core.Engine.*;
import static byow.Core.RandomUtils.*;
import static byow.Core.Point.*;

public class Room implements Serializable {
    Point bl;
    Point tr;
    int minSize = 4;
    int maxSize = 20;

    Room(Random RANDOM) {
        this.bl = new Point(uniform(RANDOM, margin, WIDTH - margin), uniform(RANDOM, margin, HEIGHT - margin));
        this.tr = new Point(bl.x + uniform(RANDOM, minSize, maxSize),
                            bl.y + uniform(RANDOM, minSize, maxSize));
    }

    public boolean isLegit(TETile[][] world) {
        return this.isInBound() && this.isNotOverlapped(world);
    }

    public boolean isInBound() {
        return tr.x < WIDTH - margin - 1 && tr.y < HEIGHT - marginLarge && tr.x >= margin - 1 && tr.y >= margin - 1;
    }

    public boolean isNotOverlapped(TETile[][] world) {
        for (int dx = bl.x; dx <= tr.x; dx++) {
            for (int dy = bl.y; dy <= tr.y; dy++) {
                if (world[dx][dy] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    public void drawRoom(TETile[][] world) {
        drawRow(bl, tr.x - bl.x, Tileset.WALL, world);
        drawRow(new Point(bl.x, tr.y), tr.x - bl.x, Tileset.WALL, world);
        drawCol(tr, tr.y - bl.y + 1, Tileset.WALL, world);
        drawCol(new Point(bl.x, tr.y), tr.y - bl.y + 1, Tileset.WALL, world);

        for (int dy = 0; dy < tr.y - bl.y - 1; dy++) {
            drawRow(new Point(bl.x + 1, bl.y + dy + 1), tr.x - bl.x - 1, Tileset.FLOOR, world);
        }
    }

    public static Room nearest(Room r, List<Room> rooms) {
        double minDist = Double.POSITIVE_INFINITY;
        Room minPoint = null;
        for (Room q : rooms) {
            if (!r.equals(q)) {
                double dist = distance(r.getCenter(), q.getCenter());
                if (dist < minDist) {
                    minDist = dist;
                    minPoint = q;
                }
            }
        }
        return minPoint;
    }

    public Point getCenter() {
        return new Point((bl.x + tr.x) / 2, (bl.y + tr.y) / 2);
    }

    public static Room getCornerRoom(List<Room> rooms, char c) {
        Room tr = rooms.get(0);
        Point t = null;
        switch (c) {
            case 't' -> t = new Point(WIDTH / 2, HEIGHT);
            case 'r' -> t = new Point(WIDTH, HEIGHT / 2);
            case 'b' -> t = new Point(WIDTH / 2, 0);
            case 'l' -> t = new Point(0, HEIGHT / 2);
        }
        double closest = Math.pow(tr.getCenter().x - t.x, 2) + Math.pow(tr.getCenter().y - t.y, 2);
        for (Room r : rooms) {
            double dist = Math.pow(r.getCenter().x - t.x, 2) + Math.pow(r.getCenter().y - t.y, 2);
            if (dist < closest) {
                tr = r;
                closest = dist;
            }
        }
        return tr;
    }
}
