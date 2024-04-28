package byow.Core;

import byow.TileEngine.TETile;
import static byow.Core.FileUtils.*;

import java.io.*;
import java.util.List;
import java.util.Random;


public class Record implements Serializable {
    private long seed;
    private TETile[][] world;
    private List<Room> rooms;
    private Point door, avatar;
    private Random rand;
    private int round = 1;
    private String moves;
    private int doorSide;


    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void setWorld(TETile[][] world) {
        this.world = world;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Point getDoor() {
        return door;
    }

    public void setDoor(Point door) {
        this.door = door;
    }

    public Point getAvatar() {
        return avatar;
    }

    public void setAvatar(Point avatar) {
        this.avatar = avatar;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public int getDoorSide() {
        return doorSide;
    }

    public void setDoorSide(int doorSide) {
        this.doorSide = doorSide;
    }

    public static void saveRecord(Record rec) {
        File f = new File(Engine.CWD, "record.txt");
        writeObject(f, rec);
    }

    public static Record loadRecord(String fn) {
        File f = new File(Engine.CWD, fn);
        return readObject(f, Record.class);
    }
}
