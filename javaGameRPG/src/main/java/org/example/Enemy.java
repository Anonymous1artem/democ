import java.awt.*;
import java.util.Random;

public class Enemy {
    public int x, y;
    public int hp, maxHp;
    public int attack, defense;
    public int expReward, goldReward;
    public String name;
    public EnemyType type;
    public boolean alive = true;
    private float visualX, visualY;
    private int frame, frameTimer;
    private Random rng = new Random();
    private int aiTimer = 0;

    public enum EnemyType { SLIME, GOBLIN, SKELETON, WOLF, DARK_KNIGHT }

    public Enemy(EnemyType type, int x, int y) {
        this.type = type;
        this.x = x; this.y = y;
        this.visualX = x * GameMap.TILE;
        this.visualY = y * GameMap.TILE;
        switch (type) {
            case SLIME:
                name="Слизень"; hp=30; maxHp=30; attack=5; defense=0; expReward=20; goldReward=5; break;
            case GOBLIN:
                name="Гоблин"; hp=50; maxHp=50; attack=12; defense=3; expReward=40; goldReward=15; break;
            case SKELETON:
                name="Скелет"; hp=70; maxHp=70; attack=18; defense=5; expReward=60; goldReward=20; break;
            case WOLF:
                name="Волк"; hp=45; maxHp=45; attack=15; defense=2; expReward=35; goldReward=8; break;
            case DARK_KNIGHT:
                name="Тёмный рыцарь"; hp=150; maxHp=150; attack=30; defense=15; expReward=150; goldReward=80; break;
        }
    }

    public void update(Player player) {
        float targetX = x * GameMap.TILE, targetY = y * GameMap.TILE;
        float speed = 2f;
        if (Math.abs(visualX - targetX) > speed) visualX += (targetX > visualX) ? speed : -speed;
        else visualX = targetX;
        if (Math.abs(visualY - targetY) > speed) visualY += (targetY > visualY) ? speed : -speed;
        else visualY = targetY;
        frameTimer++;
        if (frameTimer >= 12) { frame = (frame + 1) % 4; frameTimer = 0; }
    }

    public void draw(Graphics2D g, int camX, int camY) {
        if (!alive) return;
        int px = (int)visualX - camX;
        int py = (int)visualY - camY;
        int t = GameMap.TILE;

        g.setColor(new Color(0,0,0,50));
        g.fillOval(px + 6, py + t - 8, t - 12, 8);

        switch (type) {
            case SLIME: drawSlime(g, px, py, t); break;
            case GOBLIN: drawGoblin(g, px, py, t); break;
            case SKELETON: drawSkeleton(g, px, py, t); break;
            case WOLF: drawWolf(g, px, py, t); break;
            case DARK_KNIGHT: drawDarkKnight(g, px, py, t); break;
        }

        // HP bar
        int barW = t - 4;
        g.setColor(new Color(150,0,0));
        g.fillRect(px + 2, py - 10, barW, 5);
        g.setColor(new Color(220, 50, 50));
        g.fillRect(px + 2, py - 10, (int)(barW * (hp / (float)maxHp)), 5);
        g.setColor(Color.WHITE);
        g.drawRect(px + 2, py - 10, barW, 5);

        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.setColor(Color.WHITE);
        g.drawString(name, px + 2, py - 13);
    }

    private void drawSlime(Graphics2D g, int px, int py, int t) {
        int bounce = (frame < 2) ? 0 : 2;
        g.setColor(new Color(80, 200, 100));
        g.fillOval(px + 4, py + t/2 - bounce, t - 8, t/2 + bounce);
        g.setColor(new Color(50, 160, 70));
        g.drawOval(px + 4, py + t/2 - bounce, t - 8, t/2 + bounce);
        g.setColor(Color.WHITE);
        g.fillOval(px + t/2 - 7, py + t/2 + 2 - bounce, 6, 6);
        g.fillOval(px + t/2 + 2, py + t/2 + 2 - bounce, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(px + t/2 - 5, py + t/2 + 3 - bounce, 4, 4);
        g.fillOval(px + t/2 + 4, py + t/2 + 3 - bounce, 4, 4);
    }

    private void drawGoblin(Graphics2D g, int px, int py, int t) {
        g.setColor(new Color(100, 160, 60));
        g.fillOval(px + t/2 - 8, py + 8, 16, 14);
        g.setColor(new Color(80, 130, 40));
        g.fillRect(px + t/2 - 6, py + 20, 12, 18);
        g.setColor(new Color(60, 100, 30));
        g.fillRect(px + t/2 - 6, py + 36, 5, 10);
        g.fillRect(px + t/2 + 2, py + 36, 5, 10);
        g.setColor(Color.BLACK);
        g.fillOval(px + t/2 - 4, py + 13, 3, 3);
        g.fillOval(px + t/2 + 2, py + 13, 3, 3);
        // Ears
        g.setColor(new Color(100, 160, 60));
        int[] ex1 = {px+t/2-8, px+t/2-14, px+t/2-8};
        int[] ey1 = {py+10, py+14, py+18};
        g.fillPolygon(ex1, ey1, 3);
        int[] ex2 = {px+t/2+8, px+t/2+14, px+t/2+8};
        g.fillPolygon(ex2, ey1, 3);
    }

    private void drawSkeleton(Graphics2D g, int px, int py, int t) {
        g.setColor(new Color(230, 220, 200));
        g.fillOval(px + t/2 - 7, py + 4, 14, 14);
        g.setColor(new Color(200, 190, 170));
        for (int i = 0; i < 4; i++) g.fillRect(px + t/2 - 4 + i * 2, py + 18, 2, 16);
        g.fillRect(px + t/2 - 8, py + 20, 4, 2);
        g.fillRect(px + t/2 + 4, py + 20, 4, 2);
        g.fillRect(px + t/2 - 4, py + 34, 3, 10);
        g.fillRect(px + t/2 + 1, py + 34, 3, 10);
        g.setColor(new Color(60,200,60, 200));
        g.fillOval(px + t/2 - 4, py + 8, 4, 5);
        g.fillOval(px + t/2 + 1, py + 8, 4, 5);
    }

    private void drawWolf(Graphics2D g, int px, int py, int t) {
        g.setColor(new Color(120, 100, 80));
        g.fillOval(px + 4, py + 16, t - 8, 20);
        g.fillOval(px + 6, py + 8, 20, 18);
        int[] earX = {px+8, px+4, px+12};
        int[] earY = {py+10, py+2, py+2};
        g.fillPolygon(earX, earY, 3);
        int[] ear2X = {px+20, px+16, px+24};
        g.fillPolygon(ear2X, earY, 3);
        g.setColor(Color.BLACK);
        g.fillOval(px + 10, py + 13, 4, 4);
        g.fillOval(px + 18, py + 13, 4, 4);
        g.setColor(new Color(200, 60, 60));
        g.fillOval(px + 11, py + 20, 10, 5);
        // Tail
        g.setColor(new Color(120, 100, 80));
        g.fillArc(px + t - 10, py + 12, 14, 20, 90, 180);
    }

    private void drawDarkKnight(Graphics2D g, int px, int py, int t) {
        // Armor
        g.setColor(new Color(40, 40, 50));
        g.fillRect(px + 6, py + 16, t - 12, 22);
        // Head/helmet
        g.setColor(new Color(30, 30, 40));
        g.fillRect(px + t/2 - 9, py + 4, 18, 16);
        // Visor glow
        g.setColor(new Color(200, 50, 50, 200));
        g.fillRect(px + t/2 - 6, py + 10, 12, 4);
        // Shoulders
        g.setColor(new Color(50, 50, 60));
        g.fillOval(px + 2, py + 16, 12, 10);
        g.fillOval(px + t - 14, py + 16, 12, 10);
        // Legs
        g.setColor(new Color(35, 35, 45));
        g.fillRect(px + 8, py + 36, 8, 12);
        g.fillRect(px + t - 16, py + 36, 8, 12);
        // Sword
        g.setColor(new Color(100, 0, 0));
        g.fillRect(px + t - 4, py + 6, 3, 24);
        g.setColor(new Color(180, 100, 0));
        g.fillRect(px + t - 8, py + 18, 11, 3);
        // Aura
        g.setColor(new Color(150, 0, 0, 40 + (int)(Math.sin(frame * 0.8) * 20)));
        g.fillOval(px - 4, py, t + 8, t);
    }

    public float getVisualX() { return visualX; }
    public float getVisualY() { return visualY; }
}