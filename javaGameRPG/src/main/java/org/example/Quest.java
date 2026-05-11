import java.util.*;

public class Quest {
    private String id;
    private String name;
    private String description;
    private QuestType type;
    private int requiredAmount;
    private int currentProgress;
    private String targetEnemy;
    private int expReward;
    private int goldReward;
    private List<String> itemRewards;
    private boolean isCompleted;

    public enum QuestType { KILL, COLLECT, TALK }

    public Quest(String id, String name, String description, QuestType type,
                 int requiredAmount, String targetEnemy, int expReward, int goldReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredAmount = requiredAmount;
        this.currentProgress = 0;
        this.targetEnemy = targetEnemy;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.itemRewards = new ArrayList<>();
        this.isCompleted = false;
    }

    public void addProgress(int amount) {
        if (!isCompleted) {
            currentProgress = Math.min(requiredAmount, currentProgress + amount);
            if (currentProgress >= requiredAmount) {
                isCompleted = true;
            }
        }
    }

    public boolean isCompleted() { return isCompleted; }
    public String getName() { return name; }
    public int getProgress() { return currentProgress; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public QuestType getType() { return type; }
    public String getTargetEnemy() { return targetEnemy; }

    @Override
    public String toString() {
        return name + " (" + currentProgress + "/" + requiredAmount + ")";
    }
}