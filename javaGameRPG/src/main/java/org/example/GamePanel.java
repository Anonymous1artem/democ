import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private Player player;
    private GameMap map;
    private Timer timer;
    private int camX, camY;
    private NPC currentNPC;
    private QuestManager questManager;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        player = new Player("Герой", 10, 8);
        map = new GameMap();
        questManager = new QuestManager();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateCamera();
        player.update();
        map.update();
        repaint();
    }

    private void updateCamera() {
        camX = (int)player.getVisualX() - WIDTH / 2;
        camY = (int)player.getVisualY() - HEIGHT / 2;

        camX = Math.max(0, Math.min(camX, map.width * GameMap.TILE - WIDTH));
        camY = Math.max(0, Math.min(camY, map.height * GameMap.TILE - HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Рисуем карту
        map.draw(g2d, camX, camY, WIDTH, HEIGHT);

        // Рисуем NPC
        for (NPC npc : map.npcs) {
            npc.draw(g2d, camX, camY);
        }

        // Рисуем врагов
        for (Enemy enemy : map.enemies) {
            if (enemy.alive) {
                enemy.draw(g2d, camX, camY);
            }
        }

        // Рисуем игрока
        player.draw(g2d, camX, camY);

        // UI панель
        drawUI(g2d);
    }

    private void drawUI(Graphics2D g) {
        // Полупрозрачный фон для UI
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, 250, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Герой: " + player.name, 10, 30);
        g.drawString("Уровень: " + player.level, 10, 55);
        g.drawString("HP: " + player.hp + "/" + player.maxHp, 10, 80);
        g.drawString("Опыт: " + player.exp + "/" + player.expToNext, 10, 105);
        g.drawString("Золото: " + player.gold, 10, 130);
        g.drawString("Атака: " + player.attack, 10, 155);
        g.drawString("Защита: " + player.defense, 10, 180);

        // Квесты
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("--- Активные квесты ---", 10, 220);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        int y = 245;
        for (Quest q : questManager.getActiveQuests()) {
            g.drawString(q.toString(), 10, y);
            y += 20;
        }

        // Подсказка
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("WASD - движение | E - взаимодействие", 10, HEIGHT - 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int newX = player.x;
        int newY = player.y;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: newY--; player.setDirection(1); break;
            case KeyEvent.VK_S: newY++; player.setDirection(0); break;
            case KeyEvent.VK_A: newX--; player.setDirection(2); break;
            case KeyEvent.VK_D: newX++; player.setDirection(3); break;
            case KeyEvent.VK_E: interact(); return;
        }

        if (map.isWalkable(newX, newY)) {
            player.x = newX;
            player.y = newY;
        }
    }

    private void interact() {
        // Проверяем взаимодействие с NPC
        for (NPC npc : map.npcs) {
            if (Math.abs(npc.x - player.x) <= 1 && Math.abs(npc.y - player.y) <= 1) {
                showQuestDialog(npc);
                return;
            }
        }

        // Проверяем взаимодействие с врагами (бой)
        for (Enemy enemy : map.enemies) {
            if (enemy.alive && Math.abs(enemy.x - player.x) <= 1 && Math.abs(enemy.y - player.y) <= 1) {
                startBattle(enemy);
                return;
            }
        }
    }

    private void showQuestDialog(NPC npc) {
        Quest quest = questManager.getQuestByNPC(npc.questId);
        if (quest == null) return;

        if (questManager.isQuestActive(quest)) {
            JOptionPane.showMessageDialog(this,
                    npc.name + ": Твой прогресс по квесту '" + quest.getName() + "' - " +
                            quest.getProgress() + "/" + quest.getRequiredAmount());
        } else if (questManager.isQuestCompleted(quest)) {
            JOptionPane.showMessageDialog(this, npc.name + ": Спасибо, герой!");
        } else {
            int option = JOptionPane.showConfirmDialog(this,
                    npc.name + ": " + getQuestDescription(npc) + "\nВзять квест?",
                    "Квест", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                questManager.addActiveQuest(quest);
                JOptionPane.showMessageDialog(this, "Квест взят!");
            }
        }
    }

    private String getQuestDescription(NPC npc) {
        switch (npc.type) {
            case FARMER: return "Помоги мне! Гоблины украли мою пшеницу! Убей 5 гоблинов.";
            case GUARD: return "В лесу завелись скелеты. Уничтожь 4 скелета!";
            case PRIEST: return "Тёмный рыцарь оскверняет наши земли. Победи его!";
            default: return "Возьми задание";
        }
    }

    private void startBattle(Enemy enemy) {
        int playerDamage = Math.max(1, player.attack - enemy.defense + (int)(Math.random() * 10));
        enemy.hp -= playerDamage;

        if (enemy.hp <= 0) {
            enemy.alive = false;
            player.gainExp(enemy.expReward);
            player.gold += enemy.goldReward;

            // Обновляем квесты
            questManager.updateQuestProgress(enemy.name);
            JOptionPane.showMessageDialog(this,
                    "Победа! +" + enemy.expReward + " опыта, +" + enemy.goldReward + " золота");
        } else {
            int enemyDamage = Math.max(1, enemy.attack - player.defense + (int)(Math.random() * 8));
            player.hp -= enemyDamage;
            JOptionPane.showMessageDialog(this,
                    "Вы нанесли " + playerDamage + " урона\n" +
                            enemy.name + " нанёс " + enemyDamage + " урона\n" +
                            enemy.name + " HP: " + enemy.hp + "/" + enemy.maxHp);

            if (player.hp <= 0) {
                JOptionPane.showMessageDialog(this, "Вы погибли! Игра окончена.");
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}