import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    public int x, y;
    public int hp, maxHp;
    public int attack, defense;
    public int level, exp, expToNext;
    public int gold;
    public String name;
    public List<String> inventory;

    // Animation
    private int frame = 0;
    private int frameTimer = 0;
    private boolean moving = false;
    private int direction = 0; // 0=down,1=up,2=left,3=right
    private float visualX, visualY; // smooth movement

    public Player(String name, int startX, int startY) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.visualX = startX * GameMap.TILE;
        this.visualY = startY * GameMap.TILE;
        this.hp = 100; this.maxHp = 100;
        this.attack = 15; this.defense = 5;
        this.level = 1; this.exp = 0; this.expToNext = 100;
        this.gold = 50;
        this.inventory = new ArrayList<>();
        inventory.add("Зелье лечения x3");
    }

    public void update() {
        int targetX = x * GameMap.TILE;
        int targetY = y * GameMap.TILE;
        float speed = 4f;
        if (Math.abs(visualX - targetX) > speed) visualX += (targetX > visualX) ? speed : -speed;
        else visualX = targetX;
        if (Math.abs(visualY - targetY) > speed) visualY += (targetY > visualY) ? speed : -speed;
        else visualY = targetY;

        moving = (visualX != targetX || visualY != targetY);
        if (moving) {
            frameTimer++;
            if (frameTimer >= 8) { frame = (frame + 1) % 4; frameTimer = 0; }
        } else { frame = 0; }
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int px = (int)visualX - camX;
        int py = (int)visualY - camY;
        int t = GameMap.TILE;

        // Shadow
        g.setColor(new Color(0,0,0,60));
        g.fillOval(px + 6, py + t - 8, t - 12, 8);

        // Body (cloak)
        g.setColor(new Color(70, 100, 200));
        int[] bodyX = {px + t/2, px + 4, px + t - 4};
        int[] bodyY = {py + 14, py + t, py + t};
        g.fillPolygon(bodyX, bodyY, 3);

        // Legs animation
        g.setColor(new Color(40, 60, 120));
        if (moving && frame % 2 == 0) {
            g.fillRect(px + t/2 - 5, py + t - 14, 5, 14);
            g.fillRect(px + t/2 + 1, py + t - 10, 5, 10);
        } else if (moving) {
            g.fillRect(px + t/2 - 5, py + t - 10, 5, 10);
            g.fillRect(px + t/2 + 1, py + t - 14, 5, 14);
        } else {
            g.fillRect(px + t/2 - 5, py + t - 12, 5, 12);
            g.fillRect(px + t/2 + 1, py + t - 12, 5, 12);
        }

        // Head
        g.setColor(new Color(240, 200, 150));
        g.fillOval(px + t/2 - 8, py + 2, 16, 16);

        // Hair / hood
        g.setColor(new Color(60, 40, 100));
        g.fillArc(px + t/2 - 8, py, 16, 14, 0, 180);

        // Eyes
        g.setColor(Color.BLACK);
        if (direction != 1) {
            g.fillOval(px + t/2 - 4, py + 8, 3, 3);
            g.fillOval(px + t/2 + 2, py + 8, 3, 3);
        }

        // Sword
        g.setColor(new Color(200, 200, 220));
        g.fillRect(px + t - 6, py + 10, 3, 16);
        g.setColor(new Color(180, 140, 60));
        g.fillRect(px + t - 9, py + 18, 9, 3);

        // HP bar above player
        int barW = t - 4;
        g.setColor(new Color(180, 0, 0));
        g.fillRect(px + 2, py - 10, barW, 5);
        g.setColor(new Color(0, 220, 60));
        g.fillRect(px + 2, py - 10, (int)(barW * (hp / (float)maxHp)), 5);
        g.setColor(Color.WHITE);
        g.drawRect(px + 2, py - 10, barW, 5);
    }

    public void gainExp(int amount) {
        exp += amount;
        while (exp >= expToNext) {
            exp -= expToNext;
            level++;
            expToNext = (int)(expToNext * 1.5);
            maxHp += 20;
            hp = maxHp;
            attack += 5;
            defense += 2;
        }
    }

    public float getVisualX() { return visualX; }
    public float getVisualY() { return visualY; }
    public void setDirection(int dir) { this.direction = dir; }
}