import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    public static final int TILE = 48;

    // Tile IDs
    public static final int GRASS   = 0;
    public static final int WATER   = 1;
    public static final int TREE    = 2;
    public static final int STONE   = 3;
    public static final int PATH    = 4;
    public static final int SAND    = 5;
    public static final int DUNGEON = 6;
    public static final int HOUSE   = 7;

    public int[][] tiles;
    public int width, height;
    public List<NPC> npcs;
    public List<Enemy> enemies;

    // Animation
    private int waterFrame = 0;
    private int waterTimer = 0;

    public GameMap() {
        width = 30;
        height = 30;
        tiles = new int[height][width];
        npcs = new ArrayList<>();
        enemies = new ArrayList<>();
        buildMap();
        spawnNPCs();
        spawnEnemies();
    }

    private void buildMap() {
        // Fill with grass
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                tiles[y][x] = GRASS;

        // Water lake
        int[][] waterTiles = {
                {5,2},{5,3},{6,2},{6,3},{6,4},{7,2},{7,3},{7,4},{7,5},{8,3},{8,4}
        };
        for (int[] t : waterTiles) tiles[t[0]][t[1]] = WATER;

        // Path from village to dungeon
        for (int x = 8; x < 22; x++) tiles[14][x] = PATH;
        for (int y = 6; y < 14; y++) tiles[y][8] = PATH;
        for (int y = 14; y < 24; y++) tiles[y][22] = PATH;

        // Trees border
        for (int x = 0; x < width; x++) { tiles[0][x] = TREE; tiles[height-1][x] = TREE; }
        for (int y = 0; y < height; y++) { tiles[y][0] = TREE; tiles[y][width-1] = TREE; }

        // Forest clusters
        int[][] trees = {
                {3,3},{3,4},{4,3},{4,4},{4,5},
                {10,2},{10,3},{11,2},{11,3},
                {2,12},{2,13},{3,12},{3,13},
                {20,3},{20,4},{21,3},{21,4},{22,4},
                {25,10},{25,11},{26,10},{26,11},
                {18,20},{18,21},{19,20},{19,21},{20,21},
                {3,22},{3,23},{4,22},{4,23},{5,23},
                {25,20},{25,21},{26,20},{26,21}
        };
        for (int[] t : trees) if (t[0]<height && t[1]<width) tiles[t[0]][t[1]] = TREE;

        // Village area (stones + houses)
        tiles[6][8] = STONE;
        tiles[6][9] = HOUSE;
        tiles[6][10] = HOUSE;
        tiles[7][9] = HOUSE;

        // Sand around water
        int[][] sand = {{4,2},{4,3},{5,1},{6,1},{7,1},{8,2},{9,3},{9,4},{8,5}};
        for (int[] t : sand) if (t[0]<height && t[1]<width) tiles[t[0]][t[1]] = SAND;

        // Dungeon entrance
        tiles[23][22] = DUNGEON;
        tiles[22][21] = STONE;
        tiles[22][22] = STONE;
        tiles[22][23] = STONE;
    }

    private void spawnNPCs() {
        npcs.add(new NPC("Иван-фермер", 9, 7, NPC.Type.FARMER, "q1"));
        npcs.add(new NPC("Стражник Борис", 10, 7, NPC.Type.GUARD, "q2"));
        npcs.add(new NPC("Жрец Мефодий", 11, 8, NPC.Type.PRIEST, "q3"));
        npcs.add(new NPC("Купец Торик", 9, 8, NPC.Type.MERCHANT, "q4"));
        npcs.add(new NPC("Королева Эльдра", 10, 8, NPC.Type.QUEEN, "q5"));
    }

    private void spawnEnemies() {
        // Slimes in fields
        enemies.add(new Enemy(Enemy.EnemyType.SLIME, 15, 5));
        enemies.add(new Enemy(Enemy.EnemyType.SLIME, 18, 7));
        enemies.add(new Enemy(Enemy.EnemyType.SLIME, 13, 9));
        // Goblins
        enemies.add(new Enemy(Enemy.EnemyType.GOBLIN, 16, 18));
        enemies.add(new Enemy(Enemy.EnemyType.GOBLIN, 14, 20));
        enemies.add(new Enemy(Enemy.EnemyType.GOBLIN, 17, 22));
        enemies.add(new Enemy(Enemy.EnemyType.GOBLIN, 13, 17));
        enemies.add(new Enemy(Enemy.EnemyType.GOBLIN, 15, 23));
        // Skeletons
        enemies.add(new Enemy(Enemy.EnemyType.SKELETON, 24, 15));
        enemies.add(new Enemy(Enemy.EnemyType.SKELETON, 26, 17));
        enemies.add(new Enemy(Enemy.EnemyType.SKELETON, 23, 18));
        enemies.add(new Enemy(Enemy.EnemyType.SKELETON, 27, 13));
        // Wolves
        enemies.add(new Enemy(Enemy.EnemyType.WOLF, 5, 18));
        enemies.add(new Enemy(Enemy.EnemyType.WOLF, 4, 20));
        enemies.add(new Enemy(Enemy.EnemyType.WOLF, 6, 22));
        // Boss
        enemies.add(new Enemy(Enemy.EnemyType.DARK_KNIGHT, 22, 25));
    }

    public boolean isWalkable(int tx, int ty) {
        if (tx < 0 || ty < 0 || tx >= width || ty >= height) return false;
        int t = tiles[ty][tx];
        if (t == TREE || t == WATER || t == STONE || t == HOUSE) return false;
        for (NPC npc : npcs) if (npc.x == tx && npc.y == ty) return false;
        for (Enemy e : enemies) if (e.alive && e.x == tx && e.y == ty) return false;
        return true;
    }

    public void update() {
        waterTimer++;
        if (waterTimer >= 20) { waterFrame = (waterFrame+1)%3; waterTimer=0; }
        for (Enemy e : enemies) if (e.alive) e.update(null);
        for (NPC n : npcs) n.update();
    }

    public void draw(Graphics2D g, int camX, int camY, int screenW, int screenH) {
        int startX = Math.max(0, camX / TILE - 1);
        int startY = Math.max(0, camY / TILE - 1);
        int endX   = Math.min(width,  (camX + screenW) / TILE + 2);
        int endY   = Math.min(height, (camY + screenH) / TILE + 2);

        for (int ty = startY; ty < endY; ty++) {
            for (int tx = startX; tx < endX; tx++) {
                int px = tx * TILE - camX;
                int py = ty * TILE - camY;
                drawTile(g, tiles[ty][tx], px, py, tx, ty);
            }
        }
    }

    private void drawTile(Graphics2D g, int type, int px, int py, int tx, int ty) {
        int t = TILE;
        switch (type) {
            case GRASS -> {
                g.setColor(new Color(86, 140, 60));
                g.fillRect(px, py, t, t);
                // Variation
                if ((tx + ty) % 3 == 0) {
                    g.setColor(new Color(96, 155, 65));
                    g.fillRect(px+2, py+2, t-4, t-4);
                }
                // Grass tufts
                if ((tx*7+ty*3) % 5 == 0) {
                    g.setColor(new Color(110, 170, 70));
                    g.fillRect(px+8, py+28, 3, 8);
                    g.fillRect(px+12, py+26, 3, 10);
                    g.fillRect(px+16, py+29, 3, 7);
                }
            }
            case WATER -> {
                Color[] wColors = {
                        new Color(60,120,200), new Color(50,100,180), new Color(70,130,210)
                };
                g.setColor(wColors[waterFrame]);
                g.fillRect(px, py, t, t);
                g.setColor(new Color(150,200,255,80));
                int wave = (int)(Math.sin((tx+ty+waterFrame)*0.8)*4);
                g.fillOval(px+4, py+10+wave, 20, 6);
                g.fillOval(px+t-24, py+26+wave, 18, 5);
            }
            case TREE -> {
                g.setColor(new Color(60,100,40));
                g.fillRect(px, py, t, t);
                // Trunk
                g.setColor(new Color(100,70,40));
                g.fillRect(px+t/2-4, py+t/2, 8, t/2);
                // Canopy
                g.setColor(new Color(50,120,40));
                g.fillOval(px+2, py+2, t-4, t/2+8);
                g.setColor(new Color(70,150,55));
                g.fillOval(px+6, py+4, t-12, t/2);
                // Highlight
                g.setColor(new Color(100,180,70,120));
                g.fillOval(px+10, py+8, 12, 8);
            }
            case STONE -> {
                g.setColor(new Color(120,115,110));
                g.fillRect(px, py, t, t);
                g.setColor(new Color(100,95,90));
                g.fillRect(px+2, py+2, t-4, t-4);
                g.setColor(new Color(160,155,150,100));
                g.drawLine(px+4, py+4, px+t-8, py+8);
            }
            case PATH -> {
                g.setColor(new Color(180,155,100));
                g.fillRect(px, py, t, t);
                g.setColor(new Color(160,135,80));
                if ((tx+ty)%2==0) g.fillRect(px+4, py+4, t-8, t-8);
                // Pebbles
                g.setColor(new Color(140,120,70));
                g.fillOval(px+8, py+12, 5, 4);
                g.fillOval(px+28, py+28, 4, 3);
            }
            case SAND -> {
                g.setColor(new Color(220,195,130));
                g.fillRect(px, py, t, t);
                g.setColor(new Color(200,175,110,100));
                g.fillOval(px+10, py+10, 20, 10);
            }
            case DUNGEON -> {
                g.setColor(new Color(40,35,45));
                g.fillRect(px, py, t, t);
                // Door
                g.setColor(new Color(80,60,40));
                g.fillRect(px+t/2-8, py+8, 16, t-8);
                // Arch
                g.setColor(new Color(60,55,65));
                g.fillArc(px+t/2-8, py+2, 16, 16, 0, 180);
                // Glow
                g.setColor(new Color(200,50,50,80));
                g.fillRect(px+t/2-6, py+10, 12, t-12);
                // Iron bars
                g.setColor(new Color(100,100,110));
                for (int i = 0; i < 3; i++)
                    g.fillRect(px+t/2-5+i*5, py+10, 2, t-16);
                // Label
                g.setFont(new Font("Arial", Font.BOLD, 8));
                g.setColor(new Color(200,100,100));
                g.drawString("DUNGEON", px+1, py+t-2);
            }
            case HOUSE -> {
                g.setColor(new Color(180,140,80));
                g.fillRect(px, py, t, t);
                // Wall
                g.setColor(new Color(220,200,160));
                g.fillRect(px+2, py+12, t-4, t-12);
                // Roof
                g.setColor(new Color(160,60,40));
                int[] rx = {px, px+t/2, px+t};
                int[] ry = {py+16, py, py+16};
                g.fillPolygon(rx, ry, 3);
                // Window
                g.setColor(new Color(150,200,255,180));
                g.fillRect(px+6, py+20, 10, 10);
                g.setColor(Color.WHITE);
                g.drawRect(px+6, py+20, 10, 10);
                g.drawLine(px+11, py+20, px+11, py+30);
                g.drawLine(px+6, py+25, px+16, py+25);
                // Door
                g.setColor(new Color(100,70,40));
                g.fillRect(px+t/2-4, py+t-16, 8, 16);
                g.fillArc(px+t/2-4, py+t-20, 8, 8, 0, 180);
            }
        }
    }
}