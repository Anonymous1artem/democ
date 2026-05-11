import java.util.*;

public class QuestManager {
    private List<Quest> activeQuests;
    private List<Quest> completedQuests;
    private Map<String, Quest> allQuests;

    public QuestManager() {
        activeQuests = new ArrayList<>();
        completedQuests = new ArrayList<>();
        allQuests = new HashMap<>();
        initQuests();
    }

    private void initQuests() {
        Quest q1 = new Quest("q1", "Проблемы фермера",
                "Убейте 5 гоблинов", Quest.QuestType.KILL, 5, "Гоблин", 100, 50);
        Quest q2 = new Quest("q2", "Очистить лес",
                "Уничтожьте 4 скелета", Quest.QuestType.KILL, 4, "Скелет", 150, 80);
        Quest q3 = new Quest("q3", "Падение тьмы",
                "Победите Тёмного рыцаря", Quest.QuestType.KILL, 1, "Тёмный рыцарь", 300, 200);

        allQuests.put("q1", q1);
        allQuests.put("q2", q2);
        allQuests.put("q3", q3);
    }

    public Quest getQuestByNPC(String questId) {
        return allQuests.get(questId);
    }

    public void addActiveQuest(Quest quest) {
        if (!activeQuests.contains(quest) && !completedQuests.contains(quest)) {
            activeQuests.add(quest);
        }
    }

    public void updateQuestProgress(String enemyName) {
        for (Quest quest : activeQuests) {
            if (quest.getType() == Quest.QuestType.KILL &&
                    quest.getTargetEnemy().equals(enemyName)) {
                quest.addProgress(1);

                if (quest.isCompleted()) {
                    activeQuests.remove(quest);
                    completedQuests.add(quest);
                }
                break;
            }
        }
    }

    public boolean isQuestActive(Quest quest) {
        return activeQuests.contains(quest);
    }

    public boolean isQuestCompleted(Quest quest) {
        return completedQuests.contains(quest);
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }
}