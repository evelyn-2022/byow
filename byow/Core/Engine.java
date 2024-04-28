package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import static byow.Core.Edge.*;
import static byow.Core.Record.*;
import static byow.Core.RandomUtils.uniform;
import static byow.Core.SoundPlayer.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


public class Engine {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static final int margin = 5;
    public static final int marginLarge = 10;
    public static final int fontSize = 30;

    private static final List<Character> validMoves = Arrays.asList('w', 's', 'a', 'd', ':', 'q');
    private boolean gameover = false;
    private String desc = "";
    private boolean initialized = false;
    private Record currentRec;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        showMenu();
        play("byow/Assets/Music/menu.wav");

        char opt = getOpt();
        if (opt == 'n') {
            String seed = "n" + getSeed() + "s";
            interactWithInputString(seed);
        } else if (opt == 'l') {
            currentRec = loadRecord("record.txt");
            TETile[][] world = currentRec.getWorld();
            ter.renderFrame(world);
            displayRound(currentRec.getRound());
        }
        stop();
    }

    private char getOpt() {
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if ( c == 'n' || c == 'l' || c == 'q') {
                    break;
                }
            }
        }
        return c;
    }

    private String getSeed() {
        StdDraw.clear(Color.BLACK);
        displayText(WIDTH / 2, HEIGHT / 2 + HEIGHT / 6, "Enter A Random Seed", fontSize);
        displayText(WIDTH / 2, HEIGHT / 2 - HEIGHT / 6, "Start(S)", fontSize);

        StringBuilder str = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 's'&& str.length() != 0) {
                    displayText(WIDTH / 2, HEIGHT / 2 - 18, "Generating Map...", fontSize - 12);
                    StdDraw.pause(2000);
                    break;
                }
                if (c == 's'&& str.length() == 0) {
                    displayText(WIDTH / 2, HEIGHT / 2 - 18, "Seed Cannot Be Empty", fontSize - 12);
                    StdDraw.pause(2000);
                }
                if (Character.isDigit(c)) {
                    str.append(c);
                }
                if (!Character.isDigit(c) && c != 's') {
                    displayText(WIDTH / 2, HEIGHT / 2 - 18, "Seed Can Only Contain Numbers", fontSize - 12);
                    StdDraw.pause(2000);
                }

                StdDraw.clear(Color.BLACK);
                displayText(WIDTH / 2, HEIGHT / 2 + HEIGHT / 6, "Enter A Random Seed", fontSize);
                displayText(WIDTH / 2, HEIGHT / 2 - HEIGHT / 6, "Start(S)", fontSize);
                displayText(WIDTH / 2, HEIGHT / 2, str.toString(), fontSize);
            }
        }
        return str.toString();
    }

    private void showMenu() {
        initialized = true;
        ter.initialize(WIDTH, HEIGHT);

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        displayText(WIDTH / 2, HEIGHT / 2 + 12, "CS61B: THE GAME", fontSize + 12);
        displayText(WIDTH / 2, HEIGHT / 2, "New Game(N)", fontSize);
        displayText(WIDTH / 2, HEIGHT / 2 - 4, "Load Game(L)", fontSize);
        displayText(WIDTH / 2, HEIGHT / 2 - 8, "Quit(Q)", fontSize);
    }

    private void displayText(double x, double y, String str, int fs) {
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, fs);
        StdDraw.setFont(font);
        StdDraw.text(x, y, str);
        StdDraw.show();
    }

    private void displayPara(double x, double y, String para) {
        StdDraw.pause(1000);
        String[] sentences = para.split(",");
        for (int i = 0; i < sentences.length; i++) {
            StdDraw.clear(Color.black);
            for (int j = 0; j <= i; j++) {
                displayText(x, y - 4 * j, sentences[j].strip(), fontSize - 8);
            }
            StdDraw.pause(1800);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toLowerCase();
        String directions = "";

        if (input.startsWith("n")) {
            int indexOfS = input.indexOf('s');
            long seed = Long.parseLong(input.substring(1, indexOfS));
            currentRec = new Record();
            Random rand = new Random(seed);
            currentRec.setSeed(seed);
            currentRec.setRand(rand);
            directions = input.substring(indexOfS + 1);
            initWorld(currentRec.getRand(), 1);
        }

        if (input.startsWith("l")) {
            currentRec = loadRecord("record.txt");
            directions = input.substring(1);
        }

        if (directions.length() > 0) {
            for (char c : directions.toCharArray()) {
                if (c == 'w' || c == 's' || c == 'a' || c == 'd') {
                    moveAvatar(c);
                }
                if (c == ':') {
                    saveRecord(currentRec);
                }
            }
        }
        return currentRec.getWorld();
    }

    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void drawRow(Point sp, int length, TETile tile, TETile[][] world) {
        for (int dx = 0; dx < length; dx++) {
            if (world[sp.x + dx][sp.y] != Tileset.FLOOR){
                world[sp.x + dx][sp.y] = tile;
            }
        }
    }

    public static void drawCol(Point sp, int length, TETile tile, TETile[][] world) {
        for (int dy = 0; dy < length; dy++) {
            if (world[sp.x][sp.y - dy] != Tileset.FLOOR) {
                world[sp.x][sp.y - dy] = tile;
            }
        }
    }

    private List<Room> initRoom(Random rand, int round, TETile[][] world) {
        int numAddRooms = 100 * round;
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numAddRooms; i++) {
            Room newRoom = new Room(rand);
            if (newRoom.isLegit(world)) {
                newRoom.drawRoom(world);
                rooms.add(newRoom);
            }
        }
        return rooms;
    }

    private Point initDoor(Random rand, List<Room> rooms, TETile[][] world) {
        int side = uniform(rand, 4);
        Point door = null;
        switch (side) {
            case 0 -> {
                Room r = Room.getCornerRoom(rooms, 't');
                door = new Point(r.getCenter().x, r.tr.y);
            }
            case 1 -> {
                Room r = Room.getCornerRoom(rooms, 'r');
                door = new Point(r.tr.x, r.getCenter().y);
            }
            case 2 -> {
                Room r = Room.getCornerRoom(rooms, 'l');
                door = new Point(r.bl.x, r.getCenter().y);
            }
            case 3 -> {
                Room r = Room.getCornerRoom(rooms, 'b');
                door = new Point(r.getCenter().x, r.bl.y);
            }
        }

        world[door.x][door.y] = Tileset.LOCKED_DOOR;
        currentRec.setDoorSide(side);
        return door;
    }

    private Point initAvatar(List<Room> rooms, TETile[][] world) {
        int doorSide = currentRec.getDoorSide();
        int avatarSide = 3 - doorSide;

        Point avatar = null;
        switch (avatarSide) {
            case 0 -> {
                Room r = Room.getCornerRoom(rooms, 't');
                avatar = new Point(r.getCenter().x, r.tr.y - 1);
            }
            case 1 -> {
                Room r = Room.getCornerRoom(rooms, 'r');
                avatar = new Point(r.tr.x - 1, r.getCenter().y);
            }
            case 2 -> {
                Room r = Room.getCornerRoom(rooms, 'l');
                avatar = new Point(r.bl.x + 1, r.getCenter().y);
            }
            case 3 -> {
                Room r = Room.getCornerRoom(rooms, 'b');
                avatar = new Point(r.getCenter().x, r.bl.y + 1);
            }
        }
        world[avatar.x][avatar.y] = Tileset.AVATAR;
        return avatar;
    }

    public void initWorld(Random rand, int round) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);

        // Init world
        List<Room> rooms = initRoom(rand, round, world);
        List<Edge> edges = getMST(rooms);
        for (Edge edge : edges) {
            edge.initHallway(world);
        }
        Point door = initDoor(rand, rooms, world);
        Point avatar = initAvatar(rooms, world);

        // Update record
        currentRec.setWorld(world);
        currentRec.setRooms(rooms);
        currentRec.setAvatar(avatar);
        currentRec.setDoor(door);
    }

    public void renderWorld() {
        ter.renderFrame(currentRec.getWorld());
        displayRound(currentRec.getRound());
    }

    private void displayRound(int round) {
        displayText(10.5, HEIGHT - 2, "Round: " + round, fontSize);
    }

    private void displayDesc(TETile[][] world, int round) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        String str = world[x][y].description();
        if (str.equals("nothing")) {
            str = "";
        }

        if (!str.equals(desc)) {
            desc = str;

            ter.renderFrame(world);
            displayRound(round);
            displayText(WIDTH - 10.5, HEIGHT - 2, capitalize(desc), fontSize);
        }
    }

    private String capitalize(String str) {
        if (str.length() < 1) {
            return "";
        }

        String[] words = str.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        }
        return String.join(" ", words);
    }

    private Point getNewAvaPoint(char c, Record rec) {
        Point p = null;
        switch(c) {
            case 'w' -> p = new Point(rec.getAvatar().x, rec.getAvatar().y + 1);
            case 's' -> p = new Point(rec.getAvatar().x, rec.getAvatar().y - 1);
            case 'a' -> p = new Point(rec.getAvatar().x - 1, rec.getAvatar().y);
            case 'd' -> p = new Point(rec.getAvatar().x + 1, rec.getAvatar().y);
        }
        return p;
    }

    public int moveAvatar(Character c) {
        Point p = getNewAvaPoint(c, currentRec);
        TETile[][] world = currentRec.getWorld();
        Point avatar = currentRec.getAvatar();
        int round = currentRec.getRound();

        if (p != null && world[p.x][p.y].description().equals("floor")) {
            play("byow/Assets/Music/footsteps.wav");
            world[avatar.x][avatar.y] = Tileset.FLOOR;
            avatar = p;
            world[avatar.x][avatar.y] = Tileset.AVATAR;

            currentRec.setWorld(world);
            currentRec.setAvatar(avatar);
            return 1;
        }

        if (p != null && world[p.x][p.y].description().equals("locked door")) {
            play("byow/Assets/Music/doorOpen.wav");
            world[avatar.x][avatar.y] = Tileset.FLOOR;
            avatar = p;
            world[avatar.x][avatar.y] = Tileset.UNLOCKED_DOOR;

            round++;
            currentRec.setRound(round);

            initWorld(currentRec.getRand(), round);
            return 2;
        }
        play("byow/Assets/Music/hitWall.wav");
        return 0;
    }

    public void runEngine() {
        if (!initialized) {
            initialized = true;
            ter.initialize(WIDTH, HEIGHT);
        }

        play("byow/Assets/Music/intro.wav");
        StdDraw.pause(1000);
        displayPara(WIDTH / 2, HEIGHT/ 2 + 5,
                "Driven by curiosity,"
                        + "You ventured forth into the unknown…"
        );
        StdDraw.pause(1000);
        stop();

        loop("byow/Assets/Music/dungeon.wav");
        renderWorld();

        while (!gameover) {
            TETile[][] world = currentRec.getWorld();
            int round = currentRec.getRound();
            String moves = currentRec.getMoves();
            if (moves == null) {
                moves = "";
            }

            displayDesc(world, round);

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());

                if (moves.endsWith(":") && c != 'q') {
                    moves = moves.substring(0, moves.length() - 1);
                    currentRec.setMoves(moves);
                    continue;
                }

                if (validMoves.contains(c)) moves += c;

                int state = moveAvatar(c);
                if (state == 1) {
                    ter.renderFrame(world);
                    displayRound(round);
                } else if (state == 2) {
                    ter.renderFrame(world);
                    displayRound(round);
                    StdDraw.pause(1000);

                    StdDraw.clear(Color.black);
                    displayText(WIDTH / 2, HEIGHT / 2 + 4, "Good Job!", fontSize + 18);
                    displayText(WIDTH / 2, HEIGHT / 2 - 8, "Loading Next Level...", fontSize - 4);
                    StdDraw.pause(2000);
                    stop();

                    renderWorld();
                }

                if (moves.contains(":q")) {
                    moves = moves.substring(0, moves.length() - 2);
                    gameover = true;
                    // todo: cannot use exit for autograder
                }
            }
            currentRec.setMoves(moves);
        }
        saveRecord(currentRec);
    }

    public static void main(String[] args) {
        Engine e = new Engine();
        e.interactWithKeyboard();
        e.runEngine();
    }
}
