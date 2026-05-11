import java.awt.*;

public class NPC {
    public int x, y;
    public String name;
    public Type type;
    public String questId;
    private int frame, frameTimer;
    private float bobOffset = 0;

    public enum Type { FARMER, GUARD, PRIEST, MERCHANT, QUEEN }

    public NPC(String name, int x, int y, Type type, String questId) {
        this.name = name; this.x = x; this.y = y;
        this.type = type; this.questId = questId;
    }

    public void update() {
        frameTimer++;
        if (frameTimer >= 30) { frameTimer = 0; frame = (frame+1)%4; }
        bobOffset = (float)Math.sin(frameTimer * 0.2) * 2;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int px = x * GameMap.TILE - camX;
        int py = y * GameMap.TILE - camY + (int)bobOffset;
        int t = GameMap.TILE;

        // Shadow
        g.setColor(new Color(0,0,0,50));
        g.fillOval(px+6, py+t-8, t-12, 8);

        switch (type) {
            case FARMER -> drawFarmer(g, px, py, t);
            case GUARD  -> drawGuard(g, px, py, t);
            case PRIEST -> drawPriest(g, px, py, t);
            case MERCHANT -> drawMerchant(g, px, py, t);
            case QUEEN  -> drawQueen(g, px, py, t);
        }

        // Floating name
        g.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g.getFontMetrics();
        int nameW = fm.stringWidth(name);
        g.setColor(new Color(0,0,0,140));
        g.fillRoundRect(px + t/2 - nameW/2 - 2, py - 22, nameW + 4, 12, 4, 4);
        g.setColor(new Color(255, 230, 100));
        g.drawString(name, px + t/2 - nameW/2, py - 12);

        // Talk icon
        g.setColor(new Color(100, 220, 100));
        g.fillOval(px + t - 10, py, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 8));
        g.drawString("!", px + t - 7, py + 9);
    }

    private void drawFarmer(Graphics2D g, int px, int py, int t) {
        // Body
        g.setColor(new Color(100, 140, 60)); g.fillRect(px+t/2-7, py+16, 14, 18);
        // Head
        g.setColor(new Color(240,200,150)); g.fillOval(px+t/2-7, py+5, 14, 14);
        // Hat
        g.setColor(new Color(160,120,40));
        g.fillRect(px+t/2-9, py+8, 18, 4);
        g.fillRect(px+t/2-5, py+2, 10, 8);
        // Legs
        g.setColor(new Color(80,100,40));
        g.fillRect(px+t/2-6, py+32, 5, 12);
        g.fillRect(px+t/2+1, py+32, 5, 12);
        // Pitchfork
        g.setColor(new Color(140,100,50));
        g.fillRect(px+t/2+7, py+8, 2, 28);
        g.setColor(new Color(180,160,60));
        g.fillRect(px+t/2+6, py+7, 2, 5);
        g.fillRect(px+t/2+9, py+7, 2, 5);
    }

    private void drawGuard(Graphics2D g, int px, int py, int t) {
        // Armor
        g.setColor(new Color(140,130,110)); g.fillRect(px+t/2-8, py+14, 16, 20);
        // Head/helmet
        g.setColor(new Color(150,140,120)); g.fillRect(px+t/2-7, py+4, 14, 14);
        g.setColor(new Color(120,110,90)); g.fillRect(px+t/2-4, py+12, 8, 6);
        // Face
        g.setColor(new Color(240,200,150)); g.fillOval(px+t/2-5, py+6, 10, 10);
        // Legs
        g.setColor(new Color(100,90,80));
        g.fillRect(px+t/2-6, py+32, 5, 12);
        g.fillRect(px+t/2+1, py+32, 5, 12);
        // Spear
        g.setColor(new Color(140,100,50)); g.fillRect(px+t-6, py+4, 2, 32);
        g.setColor(new Color(200,200,180));
        int[] sx = {px+t-7, px+t-5, px+t-4};
        int[] sy = {py+6, py+2, py+6};
        g.fillPolygon(sx, sy, 3);
    }

    private void drawPriest(Graphics2D g, int px, int py, int t) {
        // Robe
        g.setColor(new Color(240,240,255)); g.fillRect(px+t/2-8, py+14, 16, 22);
        // Head
        g.setColor(new Color(240,200,150)); g.fillOval(px+t/2-6, py+6, 12, 12);
        // Hood
        g.setColor(new Color(200,200,230));
        g.fillArc(px+t/2-8, py+2, 16, 16, 0, 180);
        g.fillRect(px+t/2-8, py+10, 16, 6);
        // Cross
        g.setColor(new Color(200,180,100));
        g.fillRect(px+t/2-1, py+16, 2, 10);
        g.fillRect(px+t/2-4, py+20, 8, 2);
        // Legs
        g.setColor(new Color(180,180,200));
        g.fillRect(px+t/2-5, py+34, 4, 10);
        g.fillRect(px+t/2+1, py+34, 4, 10);
    }

    private void drawMerchant(Graphics2D g, int px, int py, int t) {
        // Cloak
        g.setColor(new Color(180,100,40)); g.fillRect(px+t/2-8, py+14, 16, 22);
        // Head
        g.setColor(new Color(240,200,150)); g.fillOval(px+t/2-6, py+5, 12, 12);
        // Cap
        g.setColor(new Color(140,80,30));
        g.fillOval(px+t/2-7, py+4, 14, 8);
        g.fillArc(px+t/2-4, py, 8, 8, 0, 180);
        // Belt
        g.setColor(new Color(80,60,30)); g.fillRect(px+t/2-8, py+24, 16, 3);
        g.setColor(new Color(220,180,50)); g.fillRect(px+t/2-3, py+22, 6, 6);
        // Bag
        g.setColor(new Color(160,120,60)); g.fillOval(px+2, py+22, 10, 10);
        // Legs
        g.setColor(new Color(120,80,30));
        g.fillRect(px+t/2-5, py+34, 4, 10);
        g.fillRect(px+t/2+1, py+34, 4, 10);
    }

    private void drawQueen(Graphics2D g, int px, int py, int t) {
        // Dress
        g.setColor(new Color(160,40,120));
        int[] dx = {px+t/2-10, px+t/2+10, px+t/2+16, px+t/2-16};
        int[] dy = {py+16, py+16, py+t, py+t};
        g.fillPolygon(dx, dy, 4);
        // Body
        g.setColor(new Color(180,60,140)); g.fillRect(px+t/2-7, py+12, 14, 10);
        // Head
        g.setColor(new Color(250,210,170)); g.fillOval(px+t/2-7, py+3, 14, 14);
        // Crown
        g.setColor(new Color(220,180,50));
        g.fillRect(px+t/2-7, py+2, 14, 5);
        int[] cx = {px+t/2-7, px+t/2-5, px+t/2-3, px+t/2, px+t/2+3, px+t/2+5, px+t/2+7};
        for (int i=0; i<cx.length; i+=2) {
            g.fillRect(cx[i], py-2, 2, 6);
        }
        // Gems
        g.setColor(new Color(200,50,50));
        g.fillOval(px+t/2-1, py-1, 4, 4);
        // Hair
        g.setColor(new Color(200,170,80));
        g.fillOval(px+t/2-8, py+4, 16, 10);
        g.fillRect(px+t/2-8, py+12, 3, 8);
        g.fillRect(px+t/2+5, py+12, 3, 8);
        // Scepter
        g.setColor(new Color(220,180,50)); g.fillRect(px+t/2+8, py+6, 2, 24);
        g.setColor(new Color(200,50,50)); g.fillOval(px+t/2+6, py+3, 6, 6);
    }
}