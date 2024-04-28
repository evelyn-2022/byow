package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

import static byow.Core.Point.*;
import static byow.Core.Engine.*;
import static byow.Core.Room.*;

public class Edge {
    private double weight;
    private Room r1;
    private Room r2;

    public Edge(double weight, Room r1, Room r2) {
        this.weight = weight;
        this.r1 = r1;
        this.r2 = r2;
    }

    public double getWeight() {
        return weight;
    }

    public void initHallway(TETile[][] world) {
        Point c1 = r1.getCenter();
        Point c2 = r2.getCenter();
        if (c1.x <= c2.x && c1.y <= c2.y) {
            Point intersect = new Point(c2.x, c1.y);
            connectH(c1, intersect, world);
            connectV(c2, intersect, world);
        } else if (c1.x > c2.x && c1.y >= c2.y) {
            Point intersect = new Point(c1.x, c2.y);
            connectH(c2, intersect, world);
            connectV(c1, intersect, world);
        } else if (c1.x <= c2.x && c1.y > c2.y) {
            Point intersect = new Point(c2.x, c1.y);
            connectH(c1, intersect, world);
            connectV(intersect, c2, world);
        } else {
            Point intersect = new Point(c1.x, c2.y);
            connectH(c2, intersect, world);
            connectV(intersect, c1, world);
        }
    }

    public void connectH(Point c1, Point c2, TETile[][] world) {
        drawRow(c1.shift(0, 1), c2.x - c1.x + 2, Tileset.WALL, world);
        drawRow(c1, c2.x - c1.x + 1, Tileset.FLOOR, world);
        drawRow(c1.shift(0, -1), c2.x - c1.x + 2, Tileset.WALL, world);

    }
    public void connectV(Point c1, Point c2, TETile[][] world) {
        drawCol(c1.shift(-1, 0), c1.y - c2.y + 2, Tileset.WALL, world);
        drawCol(c1, c1.y - c2.y + 1, Tileset.FLOOR, world);
        drawCol(c1.shift(1, 0), c1.y - c2.y + 2, Tileset.WALL, world);
    }
    public static List<Edge> getMST(List<Room> rooms) {
        // Initialize sets of points and edges
        List<Room> visited = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // Choose the bottom left point to be the starting point
        Room start = rooms.get(0);
        for (Room r : rooms) {
            if (r.bl.getX() < start.bl.getX() && r.bl.getY() < start.bl.getY()) {
                start = r;
            }
        }

        // Add the starting point to the visited set
        visited.add(start);

        // For each point not in the visited set, calculate its distance to the visited set
        while (visited.size() < rooms.size()) {
            // Find the point with the smallest distance to the visited set
            double minDist = Double.POSITIVE_INFINITY;
            Room minPoint = null;
            for (Room r : rooms) {
                if (!visited.contains(r)) {
                    double dist = Double.POSITIVE_INFINITY;
                    for (Room q : visited) {
                        dist = Math.min(dist, distance(r.getCenter(), q.getCenter()));
                    }
                    if (dist < minDist) {
                        minDist = dist;
                        minPoint = r;
                    }
                }
            }

            // Add the point to the visited set and the edge to the edges set
            visited.add(minPoint);
            edges.add(new Edge(minDist, minPoint, nearest(minPoint, visited)));
        }

        // Return the minimum spanning tree
        return edges;
    }

    public String toString() {
        return r1.getCenter().toString() + r2.getCenter().toString() + " is " + weight;
    }
}
